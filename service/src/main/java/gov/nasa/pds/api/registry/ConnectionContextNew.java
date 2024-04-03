package gov.nasa.pds.api.registry;

import java.util.List;
import org.opensearch.client.opensearch.OpenSearchClient;

public interface ConnectionContextNew extends ConnectionContextBase {

  public OpenSearchClient getOpenSearchClient();

  public List<String> getRegistryIndices();

  public List<String> getRegistryRefIndices();

}
