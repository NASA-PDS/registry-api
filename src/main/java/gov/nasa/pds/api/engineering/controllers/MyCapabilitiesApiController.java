package gov.nasa.pds.api.engineering.controllers;

import gov.nasa.pds.api.base.CapabilitiesApi;


import gov.nasa.pds.model.Capabilities;
import gov.nasa.pds.model.Capability;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-09-08T18:25:22.504-07:00[America/Los_Angeles]")
@Controller
public class MyCapabilitiesApiController implements CapabilitiesApi {

    private static final Logger log = LoggerFactory.getLogger(MyCapabilitiesApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
        
    @org.springframework.beans.factory.annotation.Autowired
    public MyCapabilitiesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

     
    public ResponseEntity<Capabilities> capabilities() {
        String accept = request.getHeader("Accept");
        if (accept != null 
        		&& (accept.contains("application/json") || accept.contains("text/html"))) {
        	
        	Capabilities capabilities = new Capabilities();
        	
        	Capability capabilityCapabilities = new Capability();
        	capabilityCapabilities.action("/capabilities");
        	capabilityCapabilities.version("0.1");
        	capabilities.add(capabilityCapabilities);
        	
        	Capability capabilityCollections = new Capability();
        	capabilityCollections.action("/collections");
        	capabilityCollections.version("0.1");
        	capabilities.add(capabilityCollections);
        	
        	
            return new ResponseEntity<Capabilities>(capabilities, HttpStatus.NOT_IMPLEMENTED);
        }

        return new ResponseEntity<Capabilities>(HttpStatus.NOT_IMPLEMENTED);
    }

}
