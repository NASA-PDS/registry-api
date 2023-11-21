package gov.nasa.pds.api.registry.model;

import java.util.List;

public interface Pagination<T> {
  public int limit(); // maximum number of items T in a page

  public List<T> page(); // return page of items T

  public int size(); // number of items T in List<T> returned by page()

  public List<String> getSortFields(); // sequence of sort fields, for use with searchAfter

  public List<String>
      getSearchAfterValues(); // values of sort fields for the last element in the most-recently-returned
                     // page

  public int total(); // total number of items T over all pages
}
