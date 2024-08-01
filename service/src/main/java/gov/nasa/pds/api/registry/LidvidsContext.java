package gov.nasa.pds.api.registry;

import java.util.List;

public interface LidvidsContext {
  public String getProductIdentifierStr();

  public Integer getLimit();

  public List<String> getSortFields();

  public List<String> getSearchAfterValues();

  public boolean getSingletonResultExpected();
}
