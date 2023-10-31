package gov.nasa.pds.api.registry.model;

import gov.nasa.pds.api.registry.LidvidsContext;

import java.util.List;

class Unlimited implements LidvidsContext {
  final private String lidvid;

  Unlimited(String id) {
    this.lidvid = id;
  }

  @Override
  public String getLidVid() {
    return this.lidvid;
  }

  @Override
  public Integer getLimit() {
    return Integer.MAX_VALUE;
  }

  @Override
  public List<String> getSearchAfter() {
    return null;
  }

  @Override
  public boolean getSingletonResultExpected() {
    return false;
  }
}
