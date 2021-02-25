package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.controllers.MyCollectionsApiController;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.model.ProductWithXmlLabel;

public class CollectionProductIterator<T> implements Iterator<T> { 
    
	private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
	
	
	CollectionProductRelationships collectionProductRelationships;
	Iterator<SearchHit> searchHitsIterator;
	Iterator<String> productLidVidSetIterator;
	
	ObjectMapper objectMapper;
	
    // constructor 
	CollectionProductIterator(CollectionProductRelationships collectionProductRelationships) { 
        this.collectionProductRelationships = collectionProductRelationships;
        this.searchHitsIterator = this.collectionProductRelationships.getSearchHits().iterator();
        this.productLidVidSetIterator = this.initProductIterator();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        
    } 
      
    // Checks if the next element exists 
    public boolean hasNext() { 
    	return searchHitsIterator.hasNext() || productLidVidSetIterator.hasNext();
    } 
      
    // moves the cursor/iterator to next element 
    public T next() {

    	EntityProduct entityProduct;
    	if (!productLidVidSetIterator.hasNext()) {
    		if (this.searchHitsIterator.hasNext()) {
    			this.productLidVidSetIterator = this.initProductIterator();
    		}
    		else { // should not be called since this.hasNext will be false
    			throw new NoSuchElementException();
    		}
    	}
    	
    	String productLidVid = productLidVidSetIterator.next();
    	
    	GetRequest getProductRequest = new GetRequest(this.collectionProductRelationships
    			.elasticSearchConnection
    			.getRegistryIndex(), 
    			productLidVid);
        GetResponse getResponse = null;
        
       	try {
			getResponse = collectionProductRelationships.getRestHighLevelClient().get(getProductRequest, 
					RequestOptions.DEFAULT);
		
    	
    	if (getResponse.isExists()) {
    		log.info("get response " + getResponse.toString());
    		Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
    		entityProduct = objectMapper.convertValue(
    				sourceAsMap, 
    				EntityProduct.class);
    		
    		entityProduct.setProperties(sourceAsMap);
    		
    		return (T) entityProduct;
    		
    	}
    	else {
    		CollectionProductIterator.log.error("product lidvid " + productLidVid + " not found in elasticsearch (does not exists)");
    		return null;
    	}
    	
       	} catch (IOException|NoSuchElementException e) {
       		CollectionProductIterator.log.error("product lidvid " + productLidVid + " not found in elasticsearch");
			e.printStackTrace();
			return null;
		}
        
    } 
      
    // Used to remove an element. Implement only if needed 
    public void remove() { 
    	throw new UnsupportedOperationException(); 
    }
    
    
    
    private Iterator<String> initProductIterator() {
    	SearchHit searchHit = this.searchHitsIterator.next();
    	ArrayList<String> productLidVidSet = (ArrayList<String>) searchHit
    			.getSourceAsMap()
    			.get("product_lidvid");
    	return productLidVidSet.iterator();
    }
} 


