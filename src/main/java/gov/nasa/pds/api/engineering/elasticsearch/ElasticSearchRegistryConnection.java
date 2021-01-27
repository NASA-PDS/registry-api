package gov.nasa.pds.api.engineering.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

public interface ElasticSearchRegistryConnection {
	public RestHighLevelClient getRestHighLevelClient();
	public String getRegistryIndex();
	public int getTimeOutSeconds();
	

}
