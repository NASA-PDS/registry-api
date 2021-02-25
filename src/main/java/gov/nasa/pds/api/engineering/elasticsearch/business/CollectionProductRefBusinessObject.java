package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;

public class CollectionProductRefBusinessObject {
	
	
	ElasticSearchRegistryConnection elasticSearchConnection;
	
	public CollectionProductRefBusinessObject(ElasticSearchRegistryConnection elasticSearchConnection) {
		this.elasticSearchConnection = elasticSearchConnection;
	}
	
	public CollectionProductRelationships getCollectionProductsIterable(String lidvid) throws IOException {
		
		return new CollectionProductRelationships(
				lidvid,
				this.elasticSearchConnection);
	}

}
