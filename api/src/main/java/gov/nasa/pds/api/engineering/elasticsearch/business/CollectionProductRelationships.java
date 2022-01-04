package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
import gov.nasa.pds.model.Product;

public class CollectionProductRelationships implements Iterable<Product> { 
	
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
	
	List<String> fields;
	
	public List<String> getFields() {
		return fields;
	}

	public CollectionProductRelationships(
			String lidvid, 
			int start,
			int limit,
			List<String> fields,
			ElasticSearchRegistryConnection elasticSearchConnection) throws IOException {
		this.elasticSearchConnection = elasticSearchConnection;
		this.start = start;
		this.limit = limit;
		this.fields = fields;
		
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
    		
    		if (searchCollectionRefResponse != null) {
    			this.searchHits = searchCollectionRefResponse.getHits();
 
    		}
    		else {
    			this.searchHits = null;
    		}
    		
    		
		} catch (IOException e) {
			CollectionProductRelationships.log.error("Couldn't get collection " + lidvid + " from elasticSearch", e);
            throw(e);
		}

		
	}
	
	 // code for data structure 
    public Iterator<Product> iterator() { 
        return new CollectionProductIterator<Product>(this); 
    }
    
    public SearchHits getSearchHits() {
    	return this.searchHits;
    }
    

	public RestHighLevelClient getRestHighLevelClient() {
		return restHighLevelClient;
	}

	

}

      
   
