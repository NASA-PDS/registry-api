package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.controllers.MyCollectionsApiController;
import gov.nasa.pds.model.Product;


@Component
public class CollectionProductIterator<T> implements Iterator<T> { 
    
	private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
	
	
	CollectionProductRelationships collectionProductRelationships;
	Iterator<SearchHit> searchHitsIterator;
	int numberOfReturnedResults = 0;
	ObjectMapper objectMapper;
	
	@Autowired
	private ProductsBusinessObject productsBO;
	
	private Iterator<Product> productsCache = null;
	
	
    // constructor 
	CollectionProductIterator(CollectionProductRelationships collectionProductRelationships) { 
        this.collectionProductRelationships = collectionProductRelationships;
         
        SearchHits searchHits = this.collectionProductRelationships.getSearchHits();

    	this.searchHitsIterator = searchHits.iterator();
    	this.productsCache = this.initProductIterator();
      
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        
        // skip products of in first relevant batch before pagination start
        int i =0;
        int skippedRecords = this.collectionProductRelationships.getStart() % CollectionProductRefBusinessObject.PRODUCT_REFERENCES_BATCH_SIZE;
        log.debug("Skipping " + skippedRecords + "in first product batch");
        while (this.hasNext() 
        		&& (i++<skippedRecords)) {
        	this.next();
        }
      
    } 
      
    // Checks if the next element exists 
    public boolean hasNext() { 
    	return (searchHitsIterator.hasNext() 
    			|| (this.productsCache != null) && (this.productsCache.hasNext()))
    			&& (this.numberOfReturnedResults<this.collectionProductRelationships.getLimit());
    } 
     
    
    
    
    // moves the cursor/iterator to next element 
    public T next() {

    	if ((this.productsCache == null) || (!this.productsCache.hasNext())) {
    		if (this.searchHitsIterator.hasNext()) {
    			this.productsCache = this.initProductIterator();
    		}
    		else { // should not be called since this.hasNext will be false
    			throw new NoSuchElementException();
    		}	
    	}
    	
    	
    	this.numberOfReturnedResults++ ;
    	
    	return (T)this.productsCache.next();
    	

    } 
      
    
    // Used to remove an element. Implement only if needed 
    public void remove() { 
    	throw new UnsupportedOperationException(); 
    }
    
    
    
    private Iterator<Product> initProductIterator() {
    	List<Product> products;
    	
    	

		SearchHit searchHit = this.searchHitsIterator.next();
		
		Object productLidVids = searchHit
				.getSourceAsMap()
				.get("product_lidvid");
		
		ArrayList<String> productLidVidSet = null;
		
		if (productLidVids instanceof String) {
			productLidVidSet = new ArrayList<String>() {{ add((String)productLidVids); }};
		}
		else if (productLidVids instanceof List<?>) {
			productLidVidSet = (ArrayList<String>)productLidVids;
		}
		else {
			log.error("product_lidvid attribute in index registry-refs type is unexpected " + productLidVids.getClass().getName());
		}
		
		
		try {
			products  = this.productsBO
					.getProductsFromLIDVIDs(
							productLidVidSet, 
							this.collectionProductRelationships.getFields());
			return products.iterator();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			CollectionProductIterator.log.error("Error while getting products from their lidvids referenced in registry-refs ", e);
			return null;
			
		}
    	
    	
    }
} 


