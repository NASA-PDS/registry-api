package gov.nasa.pds.api.registry.opensearch;

import org.opensearch.client.RestHighLevelClient;

public interface OpenSearchRegistryConnection 
{
	public RestHighLevelClient getRestHighLevelClient();
	public String getRegistryIndex();
	public String getRegistryRefIndex();
	public int getTimeOutSeconds();
	public void close();
}
