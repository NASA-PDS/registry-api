package gov.nasa.pds.api.registry.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;
import jakarta.servlet.http.HttpServletRequest;

@Component
@RequestScope
public class URIParametersBuilder {

  public boolean verifyClassAndId = false;
  public String accept = "application/json";
  public List<String> fields = new ArrayList<String>();
  public String group = "";
  public String identifier = "";
  public List<String> keywords = new ArrayList<String>();
  public List<String> searchAfter = null;
  public Integer limit = 0; // Actual default value is passed in from the upstream frames of the
                            // call stack, but it's unclear where it comes from. Not swagger.yml,
                            // at least.
  public Boolean singletonResultExpected = true;
  public String query = "";
  public ProductVersionSelector selector = ProductVersionSelector.LATEST;
  public List<String> sort = new ArrayList<String>();
  public String version = "latest";


  @Autowired
  private HttpServletRequest request;

  public URIParametersBuilder() {}


  public URIParametersBuilder setAccept(String accept) {
    if (accept != null)
      this.accept = accept;
    return this;
  }

  public URIParametersBuilder setFields(List<String> fields) {
    if (fields != null)
      this.fields = fields;
    return this;
  }

  public URIParametersBuilder setGroup(String group) {
    if (group != null)
      this.group = group;
    return this;
  }

  public URIParametersBuilder setIdentifier(String identifier) {
    if (identifier != null)
      this.identifier = identifier;
    return this;
  }

  public URIParametersBuilder setKeywords(List<String> keywords) {
    if (keywords != null)
      this.keywords = keywords;
    return this;
  }

  public URIParametersBuilder setLimit(Integer limit) {
    if (limit == null) {
      return this;
    }

    if (limit < 0) {
      String errMsg = String.format("hit limit must be 0 or higher (got '%d'))", limit);
      throw new IllegalArgumentException(errMsg);
    }

    this.limit = limit;
    this.singletonResultExpected = false;
    return this;
  }

  public URIParametersBuilder setQuery(String query) {
    if (query != null)
      this.query = query;
    return this;
  }

  public URIParametersBuilder setSort(List<String> sort) {
    if (searchAfter != null) {
      throw new RuntimeException(
          "Cannot call URIParmetersBuilder.setSort() after URIParametersBuilder.setSearchAfter() has already been called");
    }

    if (sort != null) {
      this.sort = sort;
    }

    return this;
  }


  public URIParametersBuilder setSearchAfter(List<String> sortFields, List<String> searchAfterValues) {
    sortFields = sortFields == null ? new ArrayList<>() : sortFields;

    if (searchAfterValues == null || searchAfterValues.isEmpty()) {
//      if searchAfterValues is null/empty, fill with empty values to match length of sortFields
      searchAfterValues = new ArrayList<>();
      while (searchAfterValues.size() < sortFields.size()) {
        searchAfterValues.add("");
      }
    }

    if (sortFields.size() != searchAfterValues.size()) {
        throw new RuntimeException("Cannot set searchAfterValues with length not matching sortFields");
    }

    this.sort = sortFields;
    this.searchAfter = searchAfterValues;

  return this;
  }

  public URIParametersBuilder setVerifyClassAndId(boolean verify) {
    this.verifyClassAndId = verify;
    return this;
  }

  public URIParametersBuilder setVersion(String version) {
    if (version != null) {
      this.version = version;
      if ("all".equalsIgnoreCase(version))
        this.selector = ProductVersionSelector.ALL;
      else
        this.selector = ProductVersionSelector.LATEST;
    }
    return this;
  }

  public URIParametersBuilder setVersion(ProductVersionSelector version) {
    if (version != null) {
      this.version = version.toString().toLowerCase();
      this.selector = version;
    }
    return this;
  }



  public URIParameters build() {

    this.accept = this.request.getHeader("Accept");
    return new URIParameters(this);
  }
}
