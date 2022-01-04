package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.List;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;

public class CollectionProductRefBusinessObject {
	
	public static final int PRODUCT_REFERENCES_BATCH_SIZE = 500;
	
	
	ElasticSearchRegistryConnection elasticSearchConnection;
	
	public CollectionProductRefBusinessObject(ElasticSearchRegistryConnection elasticSearchConnection) {
		this.elasticSearchConnection = elasticSearchConnection;
	}
	
	public CollectionProductRelationships getCollectionProductsIterable(String lidvid, int start, int limit, List<String> fields) throws IOException {
		
		return new CollectionProductRelationships(
				lidvid,
				start,
				limit,
				fields,
				this.elasticSearchConnection);
	}

}
