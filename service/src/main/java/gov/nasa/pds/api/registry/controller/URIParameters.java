package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;

/*
 * Maybe not the most obvious properties class or bean or whatever name but here are some things
 * that are being done and must be maintained
 *
 * 1. If the set value is null, then leave the default value in place. The reason for ignoring null,
 * is that 100 different places do not have to test for null then do the default thing. The default
 * thing when not provided by the user via the URI parameters is to use these values that all act as
 * objects which null does not. 2. All of the set functions return this class. This is useful when
 * stacking a bunch of set calls. Instead of a single line for each set, then can be concatenated
 * via the . to make the code more readable by keeping the all of the set calls collocated. 3. By
 * default, the context assumes a Singular (single-element-valued) return type for the API route. If
 * the route is a Plural (collection-valued) return type, the transmuter will call either/both of
 * setStart() and setLimit(), which will mutate singletonResultExpected to its correct false value.
 * See swagger.yml for further detail of the Singular and Plural return types.
 */
class URIParameters implements UserContext {
  private boolean verifyClassAndId = false;
  private String accept = "application/json";
  private List<String> fields = new ArrayList<String>();
  private String group = "";
  private String identifier = "";
  private List<String> keywords = new ArrayList<String>();
  private PdsProductIdentifier productIdentifier = null;
  private Integer start = 0;
  private Integer limit = 0; // Actual default value is passed in from the upstream frames of the
                             // call stack,
                             // but it's unclear where it comes from. Not swagger.yml, at least.
  private Boolean singletonResultExpected = true;
  private String query = "";
  private ProductVersionSelector selector = ProductVersionSelector.LATEST;
  private List<String> sort = new ArrayList<String>();
  private String version = "latest";

  @Override
  public String getAccept() {
    return accept;
  }

  @Override
  public List<String> getFields() {
    return fields;
  }

  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public List<String> getKeywords() {
    return keywords;
  }

  @Override
  public Integer getLimit() {
    return limit;
  }

  @Override
  public boolean getSingletonResultExpected() {
    return singletonResultExpected;
  }

  @Override
  public String getLidVid() {
    return productIdentifier != null ? productIdentifier.toString() : "";
  }

  @Override
  public String getQuery() {
    return query;
  }

  @Override
  public ProductVersionSelector getSelector() {
    return selector;
  }

  @Override
  public List<String> getSort() {
    return sort;
  }

  @Override
  public Integer getStart() {
    return start;
  }

  public boolean getVerifyClassAndId() {
    return verifyClassAndId;
  }

  @Override
  public String getVersion() {
    return version;
  }

  public URIParameters setAccept(String accept) {
    if (accept != null)
      this.accept = accept;
    return this;
  }

  public URIParameters setFields(List<String> fields) {
    if (fields != null)
      this.fields = fields;
    return this;
  }

  public URIParameters setGroup(String group) {
    if (group != null)
      this.group = group;
    return this;
  }

  public URIParameters setIdentifier(String identifier) {
    if (identifier != null)
      this.identifier = identifier;
    return this;
  }

  public URIParameters setKeywords(List<String> keywords) {
    if (keywords != null)
      this.keywords = keywords;
    return this;
  }

  public URIParameters setLimit(Integer limit) {
    if (limit == null) {
      return this;
    }

    if (limit < 0) {
      String errMsg = String.format("start index must be 0 or higher (got '%d'))", start);
      throw new IllegalArgumentException(errMsg);
    }

    this.limit = limit;
    this.singletonResultExpected = false;
    return this;
  }

  public URIParameters setProductIdentifier(ControlContext control)
      throws IOException, LidVidNotFoundException {
    this.productIdentifier = LidVidUtils.resolve(this.getIdentifier(), ProductVersionSelector.TYPED,
        control, RequestBuildContextFactory.empty());
    return this;
  }

  public URIParameters setQuery(String query) {
    if (query != null)
      this.query = query;
    return this;
  }

  public URIParameters setSort(List<String> sort) {
    if (sort != null)
      this.sort = sort;
    return this;
  }

  public URIParameters setStart(Integer start) {
    if (start == null) {
      return this;
    }

    if (start < 0) {
      String errMsg = String.format("start index must be 0 or higher (got '%d'))", start);
      throw new IllegalArgumentException(errMsg);
    }

    this.start = start;
    this.singletonResultExpected = false;
    return this;
  }

  public URIParameters setVerifyClassAndId(boolean verify) {
    this.verifyClassAndId = verify;
    return this;
  }

  public URIParameters setVersion(String version) {
    if (version != null) {
      this.version = version;
      if ("all".equalsIgnoreCase(version))
        this.selector = ProductVersionSelector.ALL;
      else
        this.selector = ProductVersionSelector.LATEST;
    }
    return this;
  }

  public URIParameters setVersion(ProductVersionSelector version) {
    if (version != null) {
      this.version = version.toString().toLowerCase();
      this.selector = version;
    }
    return this;
  }
}
