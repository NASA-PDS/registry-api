package gov.nasa.pds.api.registry.elasticsearch.business;

import java.io.IOException;

import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchRegistryConnection;


public class CollectionProductRefBusinessObject {
	
	public static final int PRODUCT_REFERENCES_BATCH_SIZE = 500;
	
	
	ElasticSearchRegistryConnection elasticSearchConnection;
	
	public CollectionProductRefBusinessObject(ElasticSearchRegistryConnection elasticSearchConnection) {
		this.elasticSearchConnection = elasticSearchConnection;
	}
	
	public CollectionProductRelationships getCollectionProductsIterable(String lidvid, int start, int limit) throws IOException, LidVidNotFoundException {
		
		return new CollectionProductRelationships(
				lidvid,
				start,
				limit,
				this.elasticSearchConnection);
	}

}
