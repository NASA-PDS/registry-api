package gov.nasa.pds.api.registry;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.opensearch.OpenSearchClient;


public interface ConnectionContext {
  public String getRegistryIndex();

  public String getRegistryRefIndex();

  // OpenSearchClient or RestHighLevelClient
  public RestHighLevelClient getOpenSearchClient();

  public int getTimeOutSeconds();
  // public void close();
}
