package gov.nasa.pds.api.registry;

import org.opensearch.client.opensearch.OpenSearchClient;

public interface ConnectionContextNew extends ConnectionContextBase {

  // OpenSearchClient or RestHighLevelClient
  public OpenSearchClient getOpenSearchClient();
}
