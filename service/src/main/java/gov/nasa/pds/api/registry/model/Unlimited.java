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
  public List<String> getSortFields() {
    return List.of("ops:Harvest_Info/ops:harvest_date_time");
  }

  @Override
  public List<String> getSearchAfterValues() {
    return List.of("");  // TODO: Check whether this actually works or if it needs to be set per-page in the contexts in which Unlimited is used
  }

  @Override
  public boolean getSingletonResultExpected() {
    return false;
  }
}
