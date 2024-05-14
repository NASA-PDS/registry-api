package gov.nasa.pds.api.registry;

import java.util.List;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.generic.OpenSearchGenericClient;

public interface ConnectionContext {

  public OpenSearchClient getOpenSearchClient();

  public OpenSearchGenericClient getOpenSearchGenericClient();

  public String getHost();

  public List<String> getRegistryIndices();

  public List<String> getRegistryRefIndices();

  public int getTimeOutSeconds();

  public List<String> getArchiveStatus();


}
