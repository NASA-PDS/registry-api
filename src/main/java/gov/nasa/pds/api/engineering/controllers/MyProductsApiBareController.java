package gov.nasa.pds.api.engineering.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
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
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

public class MyProductsApiBareController {
	
	private static final Logger log = LoggerFactory.getLogger(MyProductsApiBareController.class);  
	
    protected final ObjectMapper objectMapper;

    protected final HttpServletRequest request;
    
    protected static final String DEFAULT_NULL_VALUE = null;    

	protected Map<String, String> presetCriteria = new HashMap<String, String>();
	
	static final String LIDVID_SEPARATOR = "::";

	@Autowired
	ElasticSearchRegistryConnection esRegistryConnection;
	 
    public MyProductsApiBareController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
        
        
    }
    
    
    protected Map<String, Object> getFilteredProperties(Map<String, Object> sourceAsMap, List<String> fields){
    	
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
    	
     	ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder = new ElasticSearchRegistrySearchRequestBuilder(
     			this.esRegistryConnection.getRegistryIndex(),
     			this.esRegistryConnection.getRegistryRefIndex(),
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
    
    public String getLatestLidVidFromLid(String lid) throws IOException {
    	/*
    	 * if lid is a lidvid then it return the same lidvid if available in the elasticsearch database
    	 */
    	
    	ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder = new ElasticSearchRegistrySearchRequestBuilder(
    			this.esRegistryConnection.getRegistryIndex(),
    			this.esRegistryConnection.getRegistryRefIndex(),
    			this.esRegistryConnection.getTimeOutSeconds());
		
    	lid = !(lid.contains(LIDVID_SEPARATOR))?lid+LIDVID_SEPARATOR:lid;
		SearchRequest searchRequest = searchRequestBuilder.getSearchProductRequestHasLidVidPrefix(lid);
		
		SearchResponse searchResponse = this.esRegistryConnection.getRestHighLevelClient().search(searchRequest, 
    			RequestOptions.DEFAULT);
    	
    	if (searchResponse != null) {
    		
    		ArrayList<String> lidvids = new ArrayList<String>();
    		String lidvid;
    		for (SearchHit searchHit : searchResponse.getHits()) {
    	        lidvid = (String)searchHit.getSourceAsMap().get("lidvid");;
    	        lidvids.add(lidvid);    	        
    	    }    
     
    		Collections.sort(lidvids);
        	
    		return lidvids.get(lidvids.size() - 1);
            
    	}
    	else {
    		return null;
    	}
		
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
    
    
    protected ResponseEntity<Product> getProductResponseEntity(String lidvid){
    	String accept = request.getHeader("Accept");
        if ((accept != null) 
        		&& (accept.contains("application/json")
				|| accept.contains("text/html")
				|| accept.contains("*/*")
				|| accept.contains("application/xml")
				|| accept.contains("application/pds4+xml"))) {
        	
            try {
            	
 
            	
            	MyProductsApiBareController.log.info("request lidvdid: " + lidvid + " Headers, Accept=" + accept);
               	
            	GetRequest getProductRequest = new GetRequest(this.esRegistryConnection.getRegistryIndex(), 
            			lidvid);
                GetResponse getResponse = null;
                
                RestHighLevelClient restHighLevelClient = this.esRegistryConnection.getRestHighLevelClient();
                 
            	getResponse = restHighLevelClient.get(getProductRequest, 
            			RequestOptions.DEFAULT);
            	
	        	if (getResponse.isExists()) {
	        		log.info("get response " + getResponse.toString());
	        		Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
	        		EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);
	        		
	        		ProductWithXmlLabel product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct);

	        		Map<String, Object> sourceAsMapJsonProperties = ElasticSearchUtil.elasticHashMapToJsonHashMap(sourceAsMap);
	        		product.setProperties(sourceAsMapJsonProperties);
	        		
	        		return new ResponseEntity<Product>(product, HttpStatus.OK);
	        	}		        		
	   
	        	else {
	        		// TO DO send error 404, or 302 redirection to the correct server
	        		return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
	        	}
	        		

            } catch (IOException e) {
                log.error("Couldn't get or serialize response for content type " + accept, e);
                return new ResponseEntity<Product>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Product>(HttpStatus.NOT_IMPLEMENTED);
    }
    	

}
