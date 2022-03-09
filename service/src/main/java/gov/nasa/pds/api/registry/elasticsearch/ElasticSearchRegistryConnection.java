package gov.nasa.pds.api.registry.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

public interface ElasticSearchRegistryConnection 
{
	public RestHighLevelClient getRestHighLevelClient();
	public String getRegistryIndex();
	public String getRegistryRefIndex();
	public int getTimeOutSeconds();
	public void close();
}
