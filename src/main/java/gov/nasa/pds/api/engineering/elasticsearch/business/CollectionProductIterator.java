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
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.controllers.MyCollectionsApiController;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.model.ProductWithXmlLabel;


public class CollectionProductIterator<T> implements Iterator<T> { 
    
	private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
	
	CollectionProductRelationships collectionProductRelationships;
	Iterator<SearchHit> searchHitsIterator;
	Iterator<String> productLidVidSetIterator;
	int numberOfReturnedResults = 0;
	ObjectMapper objectMapper;
	
    // constructor 
	CollectionProductIterator(CollectionProductRelationships collectionProductRelationships) { 
        this.collectionProductRelationships = collectionProductRelationships;
        SearchHits searchHits = this.collectionProductRelationships.getSearchHits();

    	this.searchHitsIterator = searchHits.iterator();
    	this.productLidVidSetIterator = this.initProductIterator();
      
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        
        // skip products before pagination start
        int i =0;
        int skippedRecords = this.collectionProductRelationships.getStart() % CollectionProductRefBusinessObject.PRODUCT_REFERENCES_BATCH_SIZE;
        log.debug("Skipping " + skippedRecords + "in first product batch");
        while (this.hasNext() 
        		&& (i++<skippedRecords)) {
        	this.nextProductLidVid();
        }
      
    } 
      
    // Checks if the next element exists 
    public boolean hasNext() { 
    	return (searchHitsIterator.hasNext() 
    			|| (productLidVidSetIterator!= null) && (productLidVidSetIterator.hasNext()))
    			&& (this.numberOfReturnedResults<this.collectionProductRelationships.getLimit());
    } 
     
    
    private String nextProductLidVid() {

    	if (!productLidVidSetIterator.hasNext()) {
    		if (this.searchHitsIterator.hasNext()) {
    			this.productLidVidSetIterator = this.initProductIterator();
    		}
    		else { // should not be called since this.hasNext will be false
    			throw new NoSuchElementException();
    		}
    	}
    	
    	return productLidVidSetIterator.next();
    }
    
    
    
    // moves the cursor/iterator to next element 
    public T next() {

    	String productLidVid = this.nextProductLidVid();
    	
    	GetRequest getProductRequest = new ElasticSearchRegistrySearchRequestBuilder().getGetProductRequest(productLidVid);
    	
        GetResponse getResponse = null;
        
       	try {
			getResponse = collectionProductRelationships.getRestHighLevelClient().get(getProductRequest, 
					RequestOptions.DEFAULT);
		
    	
    	if (getResponse.isExists()) {
    		log.info("get response " + getResponse.toString());
    		Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        	EntityProduct entityProduct = objectMapper.convertValue(
    				sourceAsMap, 
    				EntityProduct.class);
    		
    		entityProduct.setProperties(sourceAsMap);
    		
    		this.numberOfReturnedResults++;
    		
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
    	ArrayList<String> productLidVidSet;
    	
    	if (!this.searchHitsIterator.hasNext()) { productLidVidSet = new ArrayList<String>(); }
    	else
    	{
    		SearchHit searchHit = this.searchHitsIterator.next();
    		productLidVidSet = (ArrayList<String>) searchHit
    				.getSourceAsMap()
    				.get("product_lidvid");
    	}
    	return productLidVidSet.iterator();
    }
} 


