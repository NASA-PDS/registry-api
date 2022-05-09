package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.business.ErrorFactory;
import gov.nasa.pds.api.registry.business.RequestAndResponseContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;

@Component
public class MyProductsApiBareController implements ControlContext
{    
    private static final Logger log = LoggerFactory.getLogger(MyProductsApiBareController.class);  
    protected final ObjectMapper objectMapper;
    protected final HttpServletRequest request;   
    
    @Value("${server.contextPath}")
    protected String contextPath;
    
    @Autowired
    protected HttpServletRequest context;
    
    @Autowired
    ConnectionContext connectionContext;
    
    public MyProductsApiBareController(ObjectMapper objectMapper, HttpServletRequest context) {
        this.objectMapper = objectMapper;
        this.request = context;
    }

    protected void fillProductsFromLidvids (RequestAndResponseContext context, RequestBuildContext buildContext, List<String> lidvids, int real_total) throws IOException
    {
    	context.setResponse(new HitIterator(lidvids.size(),
    			this.connectionContext.getRestHighLevelClient(),
    			new SearchRequestFactory(RequestConstructionContextFactory.given("lidvid", lidvids, true), this.connectionContext)
    				.build(buildContext, this.connectionContext.getRegistryIndex())),
    			real_total);
    }

    
    protected void getProducts(RequestAndResponseContext context) throws IOException
    {
    	context.setResponse(this.connectionContext.getRestHighLevelClient(),
    			new SearchRequestFactory(context, this.connectionContext).build(context, this.connectionContext.getRegistryIndex()));
    }
 

    protected ResponseEntity<Object> getProductsResponseEntity(URIParameters parameters, Map<String,String> preset)
    {
        String accept = this.request.getHeader("Accept");
        log.debug("accept value is " + accept);

        try
        {
        	RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this, parameters, preset, accept);
        	this.getProducts(context);                
        	return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
        }
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
        }
        catch (IOException e)
        {
            log.error("Couldn't serialize response for content type " + accept, e);
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (LidVidNotFoundException e)
        {
            log.warn("Could not find lid(vid) in database: " + parameters.getIdentifier());
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (ParseCancellationException pce)
        {
            log.error("Could not parse the query string: " + parameters.getQuery(), pce);
            return new ResponseEntity<Object>(ErrorFactory.build(pce, this.request), HttpStatus.BAD_REQUEST);
        }
    }    
    
    
    protected ResponseEntity<Object> getAllProductsResponseEntity(URIParameters parameters, Map<String,String> preset)
    {
        String accept = this.request.getHeader("Accept");
        log.debug("accept value is " + accept);

        try
        {            
            RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this, parameters, preset, accept);
            this.getProductsByLid(context);
            return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
        }
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
        }
        catch (IOException e)
        {
            log.error("Couldn't serialize response for content type " + accept, e);
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (LidVidNotFoundException e)
        {
            log.warn("Could not find lid(vid) in database: " + parameters.getIdentifier());
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (ParseCancellationException pce)
        {
            log.error("", pce);
            return new ResponseEntity<Object>(ErrorFactory.build(pce, this.request), HttpStatus.BAD_REQUEST);
        }
    }    
    
    
    public void getProductsByLid(RequestAndResponseContext context) throws IOException 
    {
    	context.setResponse(this.connectionContext.getRestHighLevelClient(),
        		new SearchRequestFactory(context, this.connectionContext).build(context, this.connectionContext.getRegistryIndex()));
    }

    
    protected ResponseEntity<Object> getLatestProductResponseEntity(URIParameters parameters, Map<String,String> preset)
    {
        String accept = request.getHeader("Accept");
        
        try 
        {
            RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this, parameters, preset, accept);
            context.setResponse(this.connectionContext.getRestHighLevelClient(),
            		new SearchRequestFactory(context, this.connectionContext).build (context, this.connectionContext.getRegistryIndex()));            

            if (context.getResponse() == null)
            { 
            	log.warn("Could not find any matches for LIDVID: " + context.getLIDVID());
            	return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
        } 
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
        }
        catch (IOException e) 
        {
            log.error("Couldn't get or serialize response for content type " + accept, e);
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (LidVidNotFoundException e)
        {
            log.warn("Could not find lid(vid) in database: " + parameters.getIdentifier());
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
    }

    
    private boolean proxyRunsOnDefaultPort() {
        return (((this.context.getScheme() == "https")  && (this.context.getServerPort() == 443)) 
                || ((this.context.getScheme() == "http")  && (this.context.getServerPort() == 80)));
    }
 
    @Override
    public URL getBaseURL() {
        try {
            MyProductsApiBareController.log.debug("contextPath is: " + this.contextPath);
            
            URL baseURL;
            if (this.proxyRunsOnDefaultPort()) {
                baseURL = new URL(this.context.getScheme(), this.context.getServerName(), this.contextPath);
            } 
            else {
                baseURL = new URL(this.context.getScheme(), this.context.getServerName(), this.context.getServerPort(), this.contextPath);
            }
            
            log.debug("baseUrl is " + baseURL.toString());
            return baseURL;
            
        } catch (MalformedURLException e) {
            log.error("Server URL was not retrieved");
            return null;
        }
    }

	@Override
	public ObjectMapper getObjectMapper() { return this.objectMapper; }
	@Override
	public ConnectionContext getConnection() { return this.connectionContext; }
}
