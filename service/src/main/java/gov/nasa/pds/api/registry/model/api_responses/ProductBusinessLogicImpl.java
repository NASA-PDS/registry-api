package gov.nasa.pds.api.registry.model.api_responses;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import gov.nasa.pds.api.registry.model.BlobUtil;
import gov.nasa.pds.api.registry.model.SearchUtil;

@Component
@RequestScope
public abstract class ProductBusinessLogicImpl implements ProductBusinessLogic {
  private static final Logger log = LoggerFactory.getLogger(ProductBusinessLogicImpl.class);

  private static final String DEFAULT_NULL_VALUE = null;

  protected URL baseURL;

  protected List<String> excludedProperties = getExcludedProperties();

  private static List<String> getExcludedProperties() {
    List<String> excludedProperties = new ArrayList<String>();
    try {
      excludedProperties.add(SearchUtil.openPropertyToJsonProperty(BlobUtil.JSON_BLOB_PROPERTY));
      excludedProperties.add(SearchUtil.openPropertyToJsonProperty(BlobUtil.XML_BLOB_PROPERTY));
    } catch (UnsupportedSearchProperty e) {
      log.error("That should not happen, unless there is an error in the code");
    }
    log.info("The following properties will not be sent in the API response " + excludedProperties);
    return excludedProperties;
  }


  public ProductBusinessLogicImpl() {
    try {

      HttpServletRequest request =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

      String proxyContextPath = request.getContextPath();
      ProductBusinessLogicImpl.log.debug("contextPath is: '" + proxyContextPath + "'");

      if (ProductBusinessLogicImpl.proxyRunsOnDefaultPort(request)) {
        this.baseURL = new URL(request.getScheme(), request.getServerName(), proxyContextPath);
      } else {
        this.baseURL = new URL(request.getScheme(), request.getServerName(),
            request.getServerPort(), proxyContextPath);
      }

      log.debug("baseUrl is " + this.baseURL.toString());


    } catch (MalformedURLException e) {
      log.error("Server URL was not retrieved");

    }
  }


  private static boolean proxyRunsOnDefaultPort(HttpServletRequest request) {
    return (("https".equals(request.getScheme()) && (request.getServerPort() == 443))
        || ("http".equals(request.getScheme()) && (request.getServerPort() == 80)));
  }


  private static List<String> object2PropertyValue(Object o) {
    ArrayList<String> pv = new ArrayList<String>();

    if (o instanceof List<?>) {
      for (Object p : (List<?>) o) {
        ((ArrayList<String>) pv).add(String.valueOf(p));
      }

    } else {
      // TODO find a type which make String castable in PropertyValue,
      // currently I am desperate so I transform String in a List<String>
      ((ArrayList<String>) pv).add(String.valueOf(o));
    }

    return pv;

  }

  /**
   * @param sourceAsMap source map coming from openSearch
   * @param included_fields, in API syntax, with .
   * @param excluded_fields is ignored is included_fields is not null and not empty, in API syntax
   * @return
   */
  public static Map<String, List<String>> getFilteredProperties(Map<String, Object> sourceAsMap, // in
                                                                                                 // ES
                                                                                                 // syntax
      List<String> included_fields, // in API syntax
      List<String> excluded_fields) { // in API syntax

    Map<String, List<String>> filteredMapJsonProperties = new HashMap<String, List<String>>();

    if ((included_fields == null) || (included_fields.size() == 0)) {
      String apiProperty;
      log.debug("Excluded fields are " + excluded_fields);
      for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
        try {
          apiProperty = SearchUtil.openPropertyToJsonProperty(entry.getKey());
          log.debug("see if property " + apiProperty + "should be added");
          if ((excluded_fields == null) || !excluded_fields.contains(apiProperty)) {
            log.debug("confirmed, the property is being added");
            filteredMapJsonProperties.put(apiProperty, object2PropertyValue(entry.getValue()));
          }
        } catch (UnsupportedSearchProperty e) {
          log.warn("openSearch property " + entry.getKey() + " is not supported, ignored");
        }
      }
    } else {
      String esField;
      for (String field : included_fields) {
        esField = SearchUtil.jsonPropertyToOpenProperty(field);

        if (sourceAsMap.containsKey(esField)) {
          filteredMapJsonProperties.put(field, object2PropertyValue(sourceAsMap.get(esField)));
        } else {
          filteredMapJsonProperties.put(field,
              object2PropertyValue(ProductBusinessLogicImpl.DEFAULT_NULL_VALUE));
        }
      }
    }
    return filteredMapJsonProperties;
  }
}


