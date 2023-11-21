package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
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

@Component
@RequestScope
class URIParameters implements UserContext {
  private final boolean verifyClassAndId;
  private final String accept;
  private final List<String> fields;
  private final String group;
  private final String identifier;
  private final List<String> keywords;
  private final List<String> searchAfterValues;
  private final Integer limit;
  private final Boolean singletonResultExpected;
  private final String query;
  private final ProductVersionSelector selector;
  private final List<String> sortFields;
  private final String version;

  private PdsProductIdentifier productIdentifier;



  public URIParameters(URIParametersBuilder builder) {
    this.verifyClassAndId = builder.verifyClassAndId;
    this.accept = builder.accept;
    this.fields = builder.fields;
    this.group = builder.group;
    this.identifier = builder.identifier;
    this.keywords = builder.keywords;
    this.searchAfterValues = builder.searchAfter;
    this.limit = builder.limit;
    this.singletonResultExpected = builder.singletonResultExpected;
    this.query = builder.query;
    this.selector = builder.selector;
    this.sortFields = builder.sort;
    this.version = builder.version;

  }

  public URIParameters setProductIdentifier(ControlContext control)
      throws IOException, LidVidNotFoundException {
    this.productIdentifier = LidVidUtils.resolve(this.identifier, ProductVersionSelector.TYPED,
        control, RequestBuildContextFactory.empty());
    return this;
  }

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
  public List<String> getSortFields() {
    return sortFields;
  }

  @Override
  public List<String> getSearchAfterValues() {
    return searchAfterValues;
  }

  public boolean getVerifyClassAndId() {
    return verifyClassAndId;
  }

  @Override
  public String getVersion() {
    return version;
  }

}
