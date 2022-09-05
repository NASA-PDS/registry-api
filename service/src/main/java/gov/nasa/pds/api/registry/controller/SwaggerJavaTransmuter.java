package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.base.GidApi;
import gov.nasa.pds.api.base.UidApi;
import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.ErrorFactory;

@Controller
public class SwaggerJavaTransmuter implements ControlContext, GidApi, UidApi
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

	@Override
	public ResponseEntity<Object> groupList(
			String group,
			@Valid List<String> fields,
			@Valid List<String> keywords,
			@Min(0) @Valid Integer limit,
			@Valid String q,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start,
			@Valid String node)
	{
		return this.processs(new Standard(), new URIParameters()
				.setGroup(group)
				.setFields(fields)
				.setKeywords(keywords)
				.setLimit(limit)
				.setQuery(q)
				.setSort(sort)
				.setStart(start)
				.setNode(node));
	}

	@Override
	public ResponseEntity<Object> groupReferencingId(
			String group,
			String identifier,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start,
			@Valid String node)
	{
		return this.processs(new GroupReferencingId(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start)
				.setNode(node));
	}

	@Override
	public ResponseEntity<Object> groupReferencingIdVers(
			String group,
			String identifier,
			String versions,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start,
			@Valid String node)
	{
		return this.processs(new GroupReferencingId(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setVersion(versions)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start)
				.setNode(node));
	}

	@Override
	public ResponseEntity<Object> idReferencingGroup(
			String group,
			String identifier,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start)
	{
		return this.processs(new IdReferencingGroup(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start));
	}

	@Override
	public ResponseEntity<Object> idReferencingGroupVers(
			String group,
			String identifier,
			String versions,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start)
	{
		return this.processs(new IdReferencingGroup(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setVersion(versions)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start));
	}

	private ResponseEntity<Object> processs (EndpointHandler handler, URIParameters parameters)
	{
        try { return handler.transmute(this, parameters.setAccept(this.request.getHeader("Accept")).setLidVid(this)); } 
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
	@Override
	public ResponseEntity<Object> selectByLidvid(String identifier, @Valid List<String> fields)
	{
		return this.processs(new Standard(), new URIParameters()
				.setIdentifier(identifier)
				.setFields(fields));
	}

	@Override
	public ResponseEntity<Object> selectByLidvidAll(
			String identifier,
			@Valid List<String> fields,
			@Min(0)@Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start)
	{
		return this.processs(new Standard(), new URIParameters()
				.setIdentifier(identifier)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start)
				.setVersion("all"));
	}

	@Override
	public ResponseEntity<Object> selectByLidvidLatest(String identifier, @Valid List<String> fields)
	{
		return this.processs(new Standard(), new URIParameters()
				.setIdentifier(identifier)
				.setFields(fields)
				.setVersion("latest"));
	}
}
