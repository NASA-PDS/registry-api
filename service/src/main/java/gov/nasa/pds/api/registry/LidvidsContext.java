package gov.nasa.pds.api.registry;

import java.util.List;

public interface LidvidsContext {
  public String getLidVid();

  public Integer getLimit();

  public List<String> getSearchAfter();

  public boolean getSingletonResultExpected();
}
