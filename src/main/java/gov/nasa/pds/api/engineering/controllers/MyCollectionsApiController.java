package gov.nasa.pds.api.engineering.controllers;


import gov.nasa.pds.api.base.CollectionsApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.business.CollectionProductRefBusinessObject;
import gov.nasa.pds.api.engineering.elasticsearch.business.CollectionProductRelationships;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.model.ProductWithXmlLabel;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;



@Controller
public class MyCollectionsApiController extends MyProductsApiBareController implements CollectionsApi {

    private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
    
 
    public MyCollectionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    	super(objectMapper, request);
    	
		this.presetCriteria.put("product_class", "Product_Collection");
	
    }
    
    
    public ResponseEntity<Product> collectionsByLidvid(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid) {
    	return this.getProductResponseEntity(lidvid);
    }



    public ResponseEntity<Products> getCollection(@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
    		,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
    		,@ApiParam(value = "search query, complex query uses eq,ne,gt,ge,lt,le,(,),not,and,or. Properties are named as in 'properties' attributes, literals are strings between \" or numbers. Detailed query specification is available at https://bit.ly/393i1af") @Valid @RequestParam(value = "q", required = false) String q
    		,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
    		,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
    		) {
    	
    	return this.getProductsResponseEntity(q, start, limit, fields, sort, onlySummary);
    }
    
    
    public ResponseEntity<Products> productsOfACollection(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid
    		,@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
    		,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
    		,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
    		,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
    		) {
    	 String accept = this.request.getHeader("Accept");
    	 MyCollectionsApiController.log.info("accept value is " + accept);
		 if ((accept != null 
		 		&& (accept.contains("application/json") 
		 				|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("application/pds4+xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null)) {
		 	
		 	try {
		    	
		 	
		 		Products products = this.getProductChildren(lidvid, start, limit, fields, sort, onlySummary);
		    	
		    	return new ResponseEntity<Products>(products, HttpStatus.OK);
		    	
		  } catch (IOException e) {
		       log.error("Couldn't serialize response for content type " + accept, e);
		       return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		     
		 }
		 else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
    }
    		
	
    
    private Products getProductChildren(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException {
    	MyCollectionsApiController.log.info("request bundle lidvid, collections children: " + lidvid);
       	
    	
    
    	
        SearchResponse searchCollectionRefResponse = null;
        
        RestHighLevelClient restHighLevelClient = this.esRegistryConnection.getRestHighLevelClient();
         
    	try {
	    	Products products = new Products();
	    	
	    	HashSet<String> uniqueProperties = new HashSet<String>();
	    	
	      	Summary summary = new Summary();
	    	
	    	summary.setStart(start);
	    	summary.setLimit(limit);
	    	
	    	if (sort == null) {
	    		sort = Arrays.asList();
	    	}	
	    	summary.setSort(sort);
	    	
	    	products.setSummary(summary);
	    	
	    	CollectionProductRefBusinessObject  collectionProductRefBO = new CollectionProductRefBusinessObject(this.esRegistryConnection);
	    	CollectionProductRelationships collectionProductRelationships = collectionProductRefBO.getCollectionProductsIterable(lidvid);
	    	
	    	int i = 0;
	    	for (EntityProduct eProd : collectionProductRelationships) {
	    		if ((i>=start) 
	    		  && (i<=start+limit)) {
		    		  if (eProd != null) {
			        	MyCollectionsApiController.log.info("request lidvdid: " + eProd.getLidVid() );
			        	
		        		ProductWithXmlLabel product = ElasticSearchUtil.ESentityProductToAPIProduct(eProd);
		
		        		Map<String, Object> sourceAsMapJsonProperties = 
		        				ElasticSearchUtil.elasticHashMapToJsonHashMap(eProd.getProperties());
		        		
		        		Map<String, Object> filteredMapJsonProperties = this.getFilteredProperties(sourceAsMapJsonProperties, fields);
	
		        		uniqueProperties.addAll(filteredMapJsonProperties.keySet());
	
		        		product.setProperties(filteredMapJsonProperties);
		        		
		        		products.addDataItem(product);
		    		}
		    		  else {
		    			  MyCollectionsApiController.log.warn("Couldn't get one product child of collection " + lidvid + " in elasticSearch");
		 	    	      
		    		  }
	    		}
	    		i+=1;
	    	}
	                   	
	    			    	
	    	
	    	summary.setProperties(new ArrayList<String>(uniqueProperties));
	    	
	    	return products;	
			
			
		} catch (IOException e) {
			MyCollectionsApiController.log.error("Couldn't get bundle " + lidvid + " from elasticSearch", e);
            throw(e);
		}
    	
    }
    


}