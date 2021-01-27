package gov.nasa.pds.api.engineering.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityCollection;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.model.ProductWithXmlLabel;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

public class MyProductsApiBareController {
	
	private static final Logger log = LoggerFactory.getLogger(MyProductsApiBareController.class);  
	
    protected final ObjectMapper objectMapper;

    protected final HttpServletRequest request;
    
    protected static final String DEFAULT_NULL_VALUE = null;    

	protected Map<String, String> presetCriteria = new HashMap<String, String>();

	@Autowired
	ElasticSearchRegistryConnection esRegistryConnection;
	 
    public MyProductsApiBareController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
        
        
    }
    
    
    private Map<String, Object> getFilteredProperties(Map<String, Object> sourceAsMap, List<String> fields){
    	
    	Map<String, Object> sourceAsMapJsonProperties =	ElasticSearchUtil.elasticHashMapToJsonHashMap(sourceAsMap);
	         
        Map<String, Object> filteredMapJsonProperties;
        
        
        if ((fields == null) || (fields.size() ==0)) {
        	filteredMapJsonProperties = new HashMap<String, Object>(sourceAsMapJsonProperties);
        }
        else {
        	filteredMapJsonProperties = new HashMap<String, Object>();
        	for (String field : fields) {
        		if (sourceAsMapJsonProperties.containsKey(field)) {
        			filteredMapJsonProperties.put(field, sourceAsMapJsonProperties.get(field));
        		}
        		else {
        			filteredMapJsonProperties.put(field, MyProductsApiBareController.DEFAULT_NULL_VALUE);
        		}
        		
        	}
        		
        }
        
        
        return filteredMapJsonProperties;
	        
	        
    }
    
    protected Products getProducts(String q, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException {
    	
     	ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder = new ElasticSearchRegistrySearchRequestBuilder(this.esRegistryConnection.getRegistryIndex(),
    			this.esRegistryConnection.getTimeOutSeconds());
    		        	
    	SearchRequest searchRequest = searchRequestBuilder.getSearchProductsRequest(q, start, limit, this.presetCriteria);
    	
    	SearchResponse searchResponse = this.esRegistryConnection.getRestHighLevelClient().search(searchRequest, 
    			RequestOptions.DEFAULT);
    	
    	Products products = new Products();
    	
    	HashSet<String> uniqueProperties = new HashSet<String>();
    	
      	Summary summary = new Summary();
    	

      	summary.setQ((q != null)?q:"" );
    	summary.setStart(start);
    	summary.setLimit(limit);
    	
    	if (sort == null) {
    		sort = Arrays.asList();
    	}	
    	summary.setSort(sort);
    	
    	products.setSummary(summary);
    	
    	if (searchResponse != null) {
    		
    		for (SearchHit searchHit : searchResponse.getHits()) {
    	        Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
    	        
    	        Map<String, Object> filteredMapJsonProperties = this.getFilteredProperties(sourceAsMap, fields);
    	        
    	        uniqueProperties.addAll(filteredMapJsonProperties.keySet());

    	        if (!onlySummary) {
        	        EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);
        	        ProductWithXmlLabel product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct);
        	        product.setProperties(filteredMapJsonProperties);
        	        products.addDataItem(product);
    	        }
    	        
    	    }
     
            
    	}
    	
    	
    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	
    	return products;
    }
    
    protected ResponseEntity<Products> getProductsResponseEntity(String q, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) {
        String accept = this.request.getHeader("Accept");
        log.info("accept value is " + accept);
        if ((accept != null 
        		&& (accept.contains("application/json") 
        				|| accept.contains("text/html")
        				|| accept.contains("application/xml")
        				|| accept.contains("application/pds4+xml")
        				|| accept.contains("*/*")))
        	|| (accept == null)) {
        	
        	try {
	        	
        	
        		Products products = this.getProducts(q, start, limit, fields, sort, onlySummary);
	        	
	        	return new ResponseEntity<Products>(products, HttpStatus.OK);
	        	
    	  } catch (IOException e) {
              log.error("Couldn't serialize response for content type " + accept, e);
              return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
          }
            
        }
        else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
    }
    	

}
