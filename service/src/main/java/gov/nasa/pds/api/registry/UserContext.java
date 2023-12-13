package gov.nasa.pds.api.registry;

import java.util.List;

import gov.nasa.pds.api.registry.model.ProductVersionSelector;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;

public interface UserContext extends LidvidsContext {
  public String getAccept();

  public List<String> getFields();

  public String getGroup();

  public PdsProductIdentifier getIdentifier();

  public List<String> getKeywords();

  public String getQuery();

  public ProductVersionSelector getSelector();

  public List<String> getSortFields();

  public List<String> getSearchAfterValues();

  public String getVersion();
}
