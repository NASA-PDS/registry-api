package gov.nasa.pds.api.registry.model;

import java.util.List;

public interface Pagination<T> {
  public int limit(); // maximum number of items T in a page

  public List<T> page(); // actual page of items T

  public int size(); // number of items T in List<T> returned by page()

//  TODO: reimplement this as proper searchAfter (key[], value[]) object, vs hardcoded single-value (lidvid)
  public List<String> searchAfter(); // lidvid of last element of previous page in this index in all possible items T over all pages

  public int total(); // total number of items T over all pages
}
