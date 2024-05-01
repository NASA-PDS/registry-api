package gov.nasa.pds.api.registry;

import java.util.List;
import org.opensearch.client.opensearch.OpenSearchClient;

public interface ConnectionContext {

  public OpenSearchClient getOpenSearchClient();

  public List<String> getRegistryIndices();

  public List<String> getRegistryRefIndices();

  public int getTimeOutSeconds();

  public List<String> getArchiveStatus();


}
