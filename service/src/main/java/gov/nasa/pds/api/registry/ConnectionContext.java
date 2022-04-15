package gov.nasa.pds.api.registry;

import org.opensearch.client.RestHighLevelClient;

public interface ConnectionContext
{
	public String getRegistryIndex();
	public String getRegistryRefIndex();
	public RestHighLevelClient getRestHighLevelClient();
	public int getTimeOutSeconds();
	//public void close();
}