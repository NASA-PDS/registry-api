package gov.nasa.pds.api.registry.model.transformers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@RequestScope
public abstract class ResponseTransformerImpl implements ResponseTransformer {

  private static final Logger log = LoggerFactory.getLogger(ResponseTransformerImpl.class);

  private static final String DEFAULT_NULL_VALUE = null;
  protected ObjectMapper objectMapper;
  protected URL baseURL;

  protected ResponseTransformerImpl() {
    try {

      HttpServletRequest request =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();


      String proxyContextPath = request.getContextPath();
      log.debug("contextPath is: '{}'", proxyContextPath);

      if (proxyRunsOnDefaultPort(request)) {
        this.baseURL = new URL(request.getScheme(), request.getServerName(), proxyContextPath);
      } else {
        this.baseURL = new URL(request.getScheme(), request.getServerName(),
            request.getServerPort(), proxyContextPath);
      }

      String baseURL = this.baseURL.toString();
      log.debug("baseUrl is {}", baseURL);


    } catch (MalformedURLException e) {
      log.error("Server URL was not retrieved");

    }
  }


  private static boolean proxyRunsOnDefaultPort(HttpServletRequest request) {
    return (("https".equals(request.getScheme()) && (request.getServerPort() == 443))
        || ("http".equals(request.getScheme()) && (request.getServerPort() == 80)));
  }


  @Override
  public void setObjectMapper(ObjectMapper om) {
    this.objectMapper = om;
  }

  @Override
  public List<PdsProperty> getRequestedFields(List<PdsProperty> userRequestFields) {
    return userRequestFields;
  }

  @Override
  public Object transform(RawMultipleProductResponse input, List<PdsProperty> fields) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object transform(Map<String, Object> input, List<PdsProperty> fields) {
    // TODO Auto-generated method stub
    return null;
  }

  private static List<String> object2PropertyValue(Object o) {
    ArrayList<String> pv = new ArrayList<String>();

    if (o instanceof List<?>) {
      for (Object p : (List<?>) o) {
        pv.add(String.valueOf(p));
      }

    } else {
      // TODO find a type which make String castable in PropertyValue,
      // currently I am desperate so I transform String in a List<String>
      pv.add(String.valueOf(o));
    }

    return pv;
  }

  /**
   * @param sourceAsMap source map coming from openSearch
   * @param includedFields, in API syntax, with .
   * @param excludedFields is ignored is included_fields is not null and not empty, in API syntax
   * @return
   */
  public static Map<String, List<String>> getFilteredProperties(Map<String, Object> sourceAsMap, // in
                                                                                                 // ES
                                                                                                 // syntax
      List<PdsProperty> includedFields, // in API syntax
      List<PdsProperty> excludedFields) { // in API syntax

    Map<String, List<String>> filteredMapJsonProperties = new HashMap<String, List<String>>();

    if ((includedFields == null) || (includedFields.size() == 0)) {
      PdsProperty property;
      for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
        property = new PdsProperty(entry.getKey());
        if ((excludedFields == null) || !excludedFields.contains(property))
          filteredMapJsonProperties.put(property.toJsonPropertyString(),
              object2PropertyValue(entry.getValue()));

      }
    } else {
      String esField;
      for (PdsProperty field : includedFields) {
        esField = field.toOpenPropertyString();

        if (sourceAsMap.containsKey(esField)) {
          filteredMapJsonProperties.put(field.toJsonPropertyString(),
              object2PropertyValue(sourceAsMap.get(esField)));
        } else {
          filteredMapJsonProperties.put(field.toJsonPropertyString(),
              object2PropertyValue(DEFAULT_NULL_VALUE));
        }
      }
    }
    return filteredMapJsonProperties;
  }
}
