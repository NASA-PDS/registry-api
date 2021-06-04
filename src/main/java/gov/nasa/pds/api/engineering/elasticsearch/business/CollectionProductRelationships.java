package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.Iterator;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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
	int start;
	public int getStart() {
		return start;
	}


	int limit;
	
	public int getLimit() {
		return limit;
	}
	
	private boolean lidVidFound(String lidvid) throws IOException {
		GetRequest getProductRequest = new GetRequest(
				this.elasticSearchConnection.getRegistryIndex(), 
    			lidvid);
        GetResponse getResponse = null;
        
        RestHighLevelClient restHighLevelClient = this.elasticSearchConnection.getRestHighLevelClient();
         
    	getResponse = restHighLevelClient.get(getProductRequest, 
    			RequestOptions.DEFAULT);
		
    	return (getResponse.isExists())?true:false;
	}

	public CollectionProductRelationships(
			String lidvid, 
			int start,
			int limit,
			ElasticSearchRegistryConnection elasticSearchConnection) throws IOException, LidVidNotFoundException {
		this.elasticSearchConnection = elasticSearchConnection;
		this.start = start;
		this.limit = limit;
		
		searchRequest = new ElasticSearchRegistrySearchRequestBuilder(
				this.elasticSearchConnection.getRegistryIndex(),
    			this.elasticSearchConnection.getRegistryRefIndex(),
    			this.elasticSearchConnection.getTimeOutSeconds())
				.getSearchProductRefsFromCollectionLidVid(lidvid, start, limit);
		 SearchResponse searchCollectionRefResponse = null;
	        
         restHighLevelClient = this.elasticSearchConnection.getRestHighLevelClient();
         
    	 try {
    		searchCollectionRefResponse = restHighLevelClient.search(searchRequest, 
					RequestOptions.DEFAULT);
 
    		this.searchHits = (searchCollectionRefResponse != null)?searchCollectionRefResponse.getHits():null;	
    		
    		
    		if (((this.searchHits == null ) || (this.searchHits.getTotalHits().value == 0)) 
    				&& (!this.lidVidFound(lidvid))){
    			LidVidNotFoundException e = new LidVidNotFoundException(lidvid);
    			throw(e);
    		}

    		
    		
    		
		} catch (IOException e) {
			CollectionProductRelationships.log.error("Couldn't get collection " + lidvid + " from elasticSearch", e);
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

      
   
