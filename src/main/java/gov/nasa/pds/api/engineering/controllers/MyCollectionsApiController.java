package gov.nasa.pds.api.engineering.controllers;


import gov.nasa.pds.api.base.CollectionsApi;

import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.model.Metadata;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Reference;
import gov.nasa.pds.model.ErrorMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.Arrays;
import java.util.Map;


@Controller
public class MyCollectionsApiController implements CollectionsApi {

    private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
 
    public MyCollectionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Products> getCollection(
    		@ApiParam(value = "offset in matching result list, for pagination") @Valid @RequestParam(value = "start", required = false) Integer start,
    		@ApiParam(value = "maximum number of matching results returned, for pagination") @Valid @RequestParam(value = "limit", required = false) Integer limit,
    		@ApiParam(value = "search query") @Valid @RequestParam(value = "q", required = false) String q,
    		@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort-by", required = false) List<String> sortBy) {
        String accept = request.getHeader("Accept");
        log.info("accept value is " + accept);
        if (accept != null 
        		&& (accept.contains("application/json") || accept.contains("text/html"))) {
            	
        	Products collections = new Products();
        	
        	Summary summary = new Summary();
        	
        	summary.setQ("");
        	summary.setStart(0);
        	summary.setLimit(100);
        	List<String> sortFields = Arrays.asList();
        	summary.setSort(sortFields);
        	
        	collections.setSummary(summary);
        	
        	Product collection = new Product();
        	collection.id("urn:nasa:pds:orex.ocams:data_raw");
        	collection.title("OSIRIS-REx OCAMS raw science image data products");
        	collection.description("This collection contains the raw (processing level 0) science image data products produced by the OCAMS instrument onboard the OSIRIS-REx spacecraft.");

        		
        	Reference instrumentReference = new Reference();
        	instrumentReference.setTitle("OREX Camera");
        	instrumentReference.setType("Instrument");
        	instrumentReference.setRef("urn:nasa:pds:context:instrument:ocams.orex");
        	
        	collection.addObservingSystemComponentsItem(instrumentReference);

        	
        	Reference target = new Reference();
        	target.setTitle("(101955) BENNU");
        	target.setRef("urn:nasa:pds:context:target:asteroid.101955_bennu");
        	collection.addTargetsItem(target);
        	
        	
        	List<String> imgResolutions = Arrays.asList("12px");        	        	
        	collection.putPropertiesItem("img:resolution", imgResolutions);
        	
        	collections.addDataItem(collection);      			
        	
            return new ResponseEntity<Products>(collections, HttpStatus.OK);
        
        }
        else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
    }

}