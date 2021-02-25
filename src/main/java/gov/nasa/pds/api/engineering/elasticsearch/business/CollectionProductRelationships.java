package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.Iterator;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.engineering.controllers.MyCollectionsApiController;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;

public class CollectionProductRelationships implements Iterable<EntityProduct> { 
	
	private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
	  
	
	ElasticSearchRegistryConnection elasticSearchConnection;
	RestHighLevelClient restHighLevelClient;
	SearchRequest searchRequest;
	SearchHits searchHits;
	
	public CollectionProductRelationships(String lidvid, ElasticSearchRegistryConnection elasticSearchConnection) throws IOException {
		this.elasticSearchConnection = elasticSearchConnection;
		
		searchRequest = new ElasticSearchRegistrySearchRequestBuilder(
				this.elasticSearchConnection.getRegistryIndex(),
    			this.elasticSearchConnection.getRegistryRefIndex(),
    			this.elasticSearchConnection.getTimeOutSeconds())
    			.getSearchProductRefsFromCollectionLidVid(lidvid);
		 SearchResponse searchCollectionRefResponse = null;
	        
         restHighLevelClient = this.elasticSearchConnection.getRestHighLevelClient();
         
    	 try {
    		searchCollectionRefResponse = restHighLevelClient.search(searchRequest, 
					RequestOptions.DEFAULT);
    		
    		if (searchCollectionRefResponse != null) {
    			searchHits = searchCollectionRefResponse.getHits();
 
    		}
    		else {
    			searchHits = null;
    		}
    		
    		
		} catch (IOException e) {
			CollectionProductRelationships.log.error("Couldn't get bundle " + lidvid + " from elasticSearch", e);
            throw(e);
		}

		
	}
	
	 // code for data structure 
    public Iterator<EntityProduct> iterator() { 
        return new CollectionProductIterator<EntityProduct>(this); 
    }
    
    public SearchHits getSearchHits() {
    	return this.searchHits;
    }
    

	public RestHighLevelClient getRestHighLevelClient() {
		return restHighLevelClient;
	}

	

}

      
   
