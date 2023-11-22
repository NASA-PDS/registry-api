package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.api.registry.LidvidsContext;

class PaginationLidvidBuilder implements Pagination<String> {
  final private int limit;
  private List<String> sortFields;
  private List<String> searchAfterValues;
  final private List<String> page = new ArrayList<String>();

  private int total = 0;

  PaginationLidvidBuilder(LidvidsContext bounds) {
    this.limit = bounds.getLimit();
    this.sortFields = bounds.getSortFields();
    this.searchAfterValues = bounds.getSearchAfterValues();
  }

  void addAll(List<String> data) {
    page.addAll(data);
    this.total += data.size();
  }

  void add(Object sourceMapValue) {
    this.addAll(this.convert(sourceMapValue));
  }

  List<String> convert(Object sourceMapValue) {
    @SuppressWarnings("unchecked")
    List<String> values = sourceMapValue instanceof List ? (List<String>) sourceMapValue
        : (sourceMapValue == null ? new ArrayList<String>()
            : Arrays.asList((String) sourceMapValue));
    return values;
  }

  @Override
  public int limit() {
    return this.limit;
  }

  @Override
  public List<String> page() {
    return this.page;
  }

  @Override
  public int size() {
    return this.page.size();
  }

  @Override
  public List<String> getSortFields() {
    return this.sortFields;
  }
  public List<String> getSearchAfterValues() {
    return this.searchAfterValues;
  }

  @Override
  public int total() {
    return this.total;
  }

}
