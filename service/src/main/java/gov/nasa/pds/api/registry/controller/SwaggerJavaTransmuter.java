package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidMismatchException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.ErrorFactory;
import gov.nasa.pds.api.registry.model.LidVidUtils;

@Controller
public class SwaggerJavaTransmuter extends SwaggerJavaDeprecatedTransmuter implements ControlContext
{
    private static final Logger log = LoggerFactory.getLogger(SwaggerJavaTransmuter.class);  
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;   
    
    @Value("${server.contextPath}")
    protected String contextPath;
    
    @Autowired
    protected HttpServletRequest context;

    @Autowired
    ConnectionContext connection;

    @org.springframework.beans.factory.annotation.Autowired
    public SwaggerJavaTransmuter(ObjectMapper objectMapper, HttpServletRequest context)
    {
        this.objectMapper = objectMapper;
        this.request = context;
    }

    @Override
	public URL getBaseURL()
	{
		try
		{
			SwaggerJavaTransmuter.log.debug("contextPath is: " + this.contextPath);
            URL baseURL;

            if (this.proxyRunsOnDefaultPort())
            {
                baseURL = new URL(this.context.getScheme(), this.context.getServerName(), this.contextPath);
            } 
            else
            {
                baseURL = new URL(this.context.getScheme(), this.context.getServerName(), this.context.getServerPort(), this.contextPath);
            }
            
            log.debug("baseUrl is " + baseURL.toString());
            return baseURL;
            
        }
		catch (MalformedURLException e)
		{
            log.error("Server URL was not retrieved");
            return null;
        }
	}

	@Override
	public ConnectionContext getConnection() { return this.connection; }

	@Override
	public ObjectMapper getObjectMapper()
	{ return this.objectMapper; }

	protected ResponseEntity<Object> processs (EndpointHandler handler, URIParameters parameters)
	{
        try
        {
        	parameters.setAccept(this.request.getHeader("Accept")).setLidVid(this);
        	if (parameters.getVerifyClassAndId()) LidVidUtils.verify (this, parameters);
        	return handler.transmute(this, parameters);
        }
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
        }
        catch (IOException e) 
        {
            log.error("Couldn't get or serialize response for content type " + this.request.getHeader("Accept"), e);
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (LidVidMismatchException e)
        {
            log.warn("The lid(vid) '" + parameters.getIdentifier() + "' in the data base type does not match given type '" +
                     parameters.getGroup() + "'");
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (LidVidNotFoundException e)
        {
            log.warn("Could not find lid(vid) in database: " + parameters.getIdentifier());
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (MembershipException e)
        {
        	log.warn("The given lid(vid) does not support the requested membership.");
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
        catch (UnknownGroupNameException e)
        {
        	log.error("Group name not implemented", e);
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
        }
	}

	private boolean proxyRunsOnDefaultPort()
	{
        return (("https".equals(this.context.getScheme()) && (this.context.getServerPort() == 443)) 
                || ("http".equals(this.context.getScheme())  && (this.context.getServerPort() == 80)));
    }
}
