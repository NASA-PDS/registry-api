package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;

public class ProductBusinessObject {
  private static final Logger log = LoggerFactory.getLogger(ProductBusinessObject.class);

  private static final String DEFAULT_NULL_VALUE = null;

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
      for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
        try {
          apiProperty = SearchUtil.openPropertyToJsonProperty(entry.getKey());
          if ((excluded_fields == null) || !excluded_fields.contains(apiProperty))
            filteredMapJsonProperties.put(apiProperty,
                ProductBusinessObject.object2PropertyValue(entry.getValue()));
        } catch (UnsupportedSearchProperty e) {
          log.warn("openSearch property " + entry.getKey() + " is not supported, ignored");
        }
      }
    } else {
      String esField;
      for (String field : included_fields) {
        esField = SearchUtil.jsonPropertyToOpenProperty(field);

        if (sourceAsMap.containsKey(esField)) {
          filteredMapJsonProperties.put(field,
              ProductBusinessObject.object2PropertyValue(sourceAsMap.get(esField)));
        } else {
          filteredMapJsonProperties.put(field,
              ProductBusinessObject.object2PropertyValue(ProductBusinessObject.DEFAULT_NULL_VALUE));
        }
      }
    }
    return filteredMapJsonProperties;
  }
}
