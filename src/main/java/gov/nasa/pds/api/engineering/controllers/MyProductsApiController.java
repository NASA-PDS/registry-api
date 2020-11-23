package gov.nasa.pds.api.engineering.controllers;

import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Reference;
import gov.nasa.pds.model.Metadata;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchConfig;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnectionImpl;
import gov.nasa.pds.api.engineering.entities.EntityProduct;

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
import org.springframework.beans.factory.annotation.Autowired;


import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;


import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchConfig;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-10-29T11:01:11.991-07:00[America/Los_Angeles]")
@Controller
public class MyProductsApiController implements ProductsApi {

    private static final Logger log = LoggerFactory.getLogger(MyProductsApiController.class);
    
    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    
    @org.springframework.beans.factory.annotation.Autowired
    public MyProductsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
        
        
    }
    
   
    public ResponseEntity<Products> products(@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
,@ApiParam(value = "search query") @Valid @RequestParam(value = "q", required = false) String q
,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
)  {
        String accept = request.getHeader("Accept");
        if (accept != null 
        		&& (accept.contains("application/json") 
        				|| accept.contains("text/html")
        				|| accept.contains("*/*"))) {
            try {
            	
            	
                return new ResponseEntity<Products>(objectMapper.readValue("{\n  \"metadata\" : {\n    \"q\" : \"q\",\n    \"start\" : 0,\n    \"limit\" : 6,\n    \"sort\" : [ \"sort\", \"sort\" ]\n  },\n  \"data\" : [ {\n    \"procedures\" : [ \"procedures\", \"procedures\" ],\n    \"feature_of_interest\" : [ \"feature_of_interest\", \"feature_of_interest\" ],\n    \"description\" : \"description\",\n    \"id\" : \"id\",\n    \"title\" : \"title\",\n    \"properties\" : {\n      \"key\" : [ \"properties\", \"properties\" ]\n    },\n    \"pds4_label_url\" : \"pds4_label_url\"\n  }, {\n    \"procedures\" : [ \"procedures\", \"procedures\" ],\n    \"feature_of_interest\" : [ \"feature_of_interest\", \"feature_of_interest\" ],\n    \"description\" : \"description\",\n    \"id\" : \"id\",\n    \"title\" : \"title\",\n    \"properties\" : {\n      \"key\" : [ \"properties\", \"properties\" ]\n    },\n    \"pds4_label_url\" : \"pds4_label_url\"\n  } ]\n}", Products.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
    }

    
   
    
    static public Product ESentityProductToAPIProduct(EntityProduct ep) {
		Product product = new Product();
		product.setId(ep.getLidVid());
		product.setType(ep.getProductClass());
		
		String title = ep.getTitle();
		if (title != null) {
			product.setTitle(ep.getTitle());
		}
		
		String startDateTime = ep.getStartDateTime();
		if (startDateTime != null) {
			product.setStartDateTime(startDateTime);
		}
		
		String stopDateTime = ep.getStopDateTime();
		if (stopDateTime != null) {
			product.setStopDateTime(ep.getStopDateTime());
		}
		
		for (String reference_role: ep.PROCEDURE_REFERENCE_ROLES) {
			Reference observingSystemComponentRef = ep.geReference(reference_role);
			if (observingSystemComponentRef != null) {
				product.addObservingSystemComponentsItem(observingSystemComponentRef);
			}
			
		}
		
		for (String reference_role : ep.TARGET_ROLES) {
			Reference targetReference = ep.geReference(reference_role);
			if (targetReference != null) {
				product.addTargetsItem(targetReference);
			}
		}
		
		Metadata meta = new Metadata();
		
		
		String version = ep.getVersion();
		if (version != null) {
			meta.setVersion(ep.getVersion());
		}
		
		String creationDateTime = ep.getCreationDate();
		if (creationDateTime != null) {
			meta.setCreationDateTime(ep.getCreationDate());
		}
		
		String updateDateTime = ep.getModificationDate();
		if (updateDateTime != null) {
			meta.setUpdateDateTime(updateDateTime);
		}
		
		String labelUrl = ep.getPDS4FileRef();
		if (labelUrl != null) {		
			meta.setLabelUrl(labelUrl);
		}
		
		product.setMetadata(meta);

		return product;

    	
    }
    
    @Autowired
    ElasticSearchRegistryConnection esRegistryConnection;
    
    public ResponseEntity<Product> productsByLidvid(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid) {
        String accept = request.getHeader("Accept");
        if (accept != null 
        		&& (accept.contains("application/json") 
        				|| accept.contains("text/html")
        				|| accept.contains("*/*"))) {
            try {
            			
            	GetRequest getProductRequest = new GetRequest(this.esRegistryConnection.getRegistryIndex(), lidvid);
                GetResponse getResponse = null;
                
                RestHighLevelClient restHighLevelClient = this.esRegistryConnection.getRestHighLevelClient();
                 
            	getResponse = restHighLevelClient.get(getProductRequest, 
            			RequestOptions.DEFAULT);
            	
	        	if (getResponse != null) {
	        		log.info("get response " + getResponse.toString());
	        		Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
	        		EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);
	        		
	        		Product product = MyProductsApiController.ESentityProductToAPIProduct(entityProduct);
	        		product.setProperties(sourceAsMap);
	        		
	        		return new ResponseEntity<Product>(product, HttpStatus.OK);
	        		
	        	}
	        	else {
	        		// TO DO send error 404, or 302 redirection to the correct server
	        		return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
	        	}
	        		

            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Product>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Product>(HttpStatus.NOT_IMPLEMENTED);
    }

    
 
    
}
