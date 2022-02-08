package gov.nasa.pds.api.engineering.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.elasticsearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.KVPQueryBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.business.RequestAndResponseContext;
import gov.nasa.pds.api.engineering.elasticsearch.business.LidVidNotFoundException;
import gov.nasa.pds.api.engineering.elasticsearch.business.LidVidUtils;
import gov.nasa.pds.api.engineering.elasticsearch.business.ProductBusinessObject;
import gov.nasa.pds.api.engineering.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.engineering.exceptions.NothingFoundException;

@Component
public class MyProductsApiBareController {
    
    private static final Logger log = LoggerFactory.getLogger(MyProductsApiBareController.class);  
    
    protected final ObjectMapper objectMapper;

    protected final HttpServletRequest request;   

    protected Map<String, String> presetCriteria = new HashMap<String, String>();
    
    @Value("${server.contextPath}")
    protected String contextPath;
    
    @Autowired
    protected HttpServletRequest context;
    
    // TODO remove and replace by BusinessObjects 
    @Autowired
    ElasticSearchRegistryConnection esRegistryConnection;
    
    @Autowired
    protected ProductBusinessObject productBO;
    
    @Autowired
    ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder;
    

    public MyProductsApiBareController(ObjectMapper objectMapper, HttpServletRequest context) {
        this.objectMapper = objectMapper;
        this.request = context;
    }

    protected void fillProductsFromLidvids (RequestAndResponseContext context, List<String> lidvids, int real_total) throws IOException
    {
        KVPQueryBuilder bld = new KVPQueryBuilder(esRegistryConnection.getRegistryIndex());
        bld.setKVP("lidvid", lidvids);
        bld.setFields(context.getFields());
        SearchRequest req = bld.buildTermQuery();
        
        ElasticSearchHitIterator itr = new ElasticSearchHitIterator(lidvids.size(), 
                esRegistryConnection.getRestHighLevelClient(), req);
        
    	context.setResponse(itr, real_total);
    }

    
    protected void getProducts(RequestAndResponseContext context) throws IOException
    {
        SearchRequest searchRequest = this.searchRequestBuilder.getSearchProductsRequest(
        		context.getQueryString(),
        		context.getKeyword(),
        		context.getFields(), context.getStart(), context.getLimit(), this.presetCriteria);
        context.setResponse(this.esRegistryConnection.getRestHighLevelClient(), searchRequest);
    }
 

    protected ResponseEntity<Object> getProductsResponseEntity(String q, String keyword, int start, int limit,
            List<String> fields, List<String> sort, boolean onlySummary)
    {
        String accept = this.request.getHeader("Accept");
        log.debug("accept value is " + accept);

        try
        {
        	RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), q, keyword, start, limit, fields, sort, onlySummary, this.presetCriteria, accept);
        	this.getProducts(context);                
        	return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
        }
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
        	return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
        }
        catch (IOException e)
        {
            log.error("Couldn't serialize response for content type " + accept, e);
            return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        catch (ParseCancellationException pce)
        {
            log.error("Could not parse the query string: " + q, pce);
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }    
    
    
    protected ResponseEntity<Object> getAllProductsResponseEntity(String identifier, int start, int limit)
    {
        String accept = this.request.getHeader("Accept");
        log.debug("accept value is " + accept);

        try
        {            
            String lidvid = LidVidUtils.extractLidFromLidVid(identifier);
            RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), lidvid, start, limit, this.presetCriteria, accept);
            this.getProductsByLid(context);
            return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
        }
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
        	return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
        }
        catch (IOException e)
        {
            log.error("Couldn't serialize response for content type " + accept, e);
            return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        catch (ParseCancellationException pce)
        {
            log.error("", pce);
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }    
    
    
    public void getProductsByLid(RequestAndResponseContext context) throws IOException 
    {
        SearchRequest req = searchRequestBuilder.getSearchProductsByLid(context.getLIDVID(), context.getStart(), context.getLimit());
        context.setResponse(this.esRegistryConnection.getRestHighLevelClient(), req);
    }

    
    protected ResponseEntity<Object> getLatestProductResponseEntity(String lidvid)
    {
        String accept = request.getHeader("Accept");
        
        try 
        {
            lidvid = this.productBO.getLidVidDao().getLatestLidVidByLid(lidvid);
            RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(
                    this.objectMapper, this.getBaseURL(), lidvid, this.presetCriteria, accept);
            
            KVPQueryBuilder bld = new KVPQueryBuilder(esRegistryConnection.getRegistryIndex());
            bld.setKVP("lidvid", lidvid);
            bld.setFields(context.getFields());            
            SearchRequest request = bld.buildTermQuery();
            
            context.setResponse(esRegistryConnection.getRestHighLevelClient(), request);

            if (context.getResponse() == null)
            { 
            	log.warn("Could not find any matches for LIDVID: " + lidvid);
            	return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
        } 
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
        	return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
        }
        catch (IOException e) 
        {
            log.error("Couldn't get or serialize response for content type " + accept, e);
            return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (LidVidNotFoundException e)
        {
            log.warn("Could not find lid(vid) in database: " + lidvid);
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
    }

    
    private boolean proxyRunsOnDefaultPort() {
        return (((this.context.getScheme() == "https")  && (this.context.getServerPort() == 443)) 
                || ((this.context.getScheme() == "http")  && (this.context.getServerPort() == 80)));
    }
 
    protected URL getBaseURL() {
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

}
