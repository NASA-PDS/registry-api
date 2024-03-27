package gov.nasa.pds.api.registry;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.opensearch.OpenSearchClient;


public interface ConnectionContextBase {
  public String getRegistryIndex();

  public String getRegistryRefIndex();

  public int getTimeOutSeconds();
  // public void close();

}
