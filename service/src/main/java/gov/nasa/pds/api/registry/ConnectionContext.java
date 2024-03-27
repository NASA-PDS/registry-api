package gov.nasa.pds.api.registry;

import org.opensearch.client.RestHighLevelClient;

public interface ConnectionContext extends ConnectionContextBase {

  public RestHighLevelClient getOpenSearchClient();

}
