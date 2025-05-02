package gov.nasa.pds.api.registry.model.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.SearchUtil;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import gov.nasa.pds.api.registry.util.LogExecutionTime;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.model.WyriwygProduct;
import gov.nasa.pds.model.WyriwygProductKeyValuePair;
import gov.nasa.pds.model.WyriwygProducts;

public class WyriwygProductTransformer extends ResponseTransformerImpl {

  private static final Logger log = LoggerFactory.getLogger(PdsProductTransformer.class);

  protected boolean isJSON;
  

  private static final List<String> EXCLUDED_PROPERTIES = List.of(PdsProperty.XML_BLOB, PdsProperty.JSON_BLOB);

  private WyriwygProduct generateWyriwygProduct(Map<String, Object> hit,
  List<String> included_fields) {
  WyriwygProduct product = new WyriwygProduct();
  String jsonProperty;
  for (Entry<String, Object> pair : hit.entrySet()) {
    WyriwygProductKeyValuePair kvp = new WyriwygProductKeyValuePair();
    try {
      jsonProperty = SearchUtil.openPropertyToJsonProperty(pair.getKey());
      if (!EXCLUDED_PROPERTIES.contains(jsonProperty)) {
        if (included_fields == null || included_fields.isEmpty() || included_fields.contains(jsonProperty)) {
          kvp.setKey(jsonProperty);
          kvp.setValue(getStringValueOf(pair.getValue()));
          product.addKeyValuePairsItem(kvp);
        }
      }
    } catch (UnsupportedSearchProperty e) {
      log.warn("openSearch property " + pair.getKey() + " is not supported, ignored");
    }

}

return product;


}

private String getStringValueOf(Object o) {
  String valueOf;
  if (o instanceof Iterable) {
    List<String> stringRepresentations = new ArrayList<>();
    for (Object el : (Iterable<Object>) o) {
      stringRepresentations.add(String.valueOf(el));
    }

    String delimiter = "|";
    valueOf = String.join(delimiter, stringRepresentations);
  } else {
    valueOf = String.valueOf(o);
  }

  return valueOf;
}

  @Override
  @LogExecutionTime
  public Object transform(RawMultipleProductResponse input, List<String> fields) {
    Set<String> uniqueProperties = new TreeSet<String>();
    WyriwygProducts products = new WyriwygProducts();

    for (Map<String, Object> hit : input.getProducts()) {
      uniqueProperties.addAll(getFilteredProperties(hit, fields, EXCLUDED_PROPERTIES).keySet());
      WyriwygProduct product = generateWyriwygProduct(hit, fields);
      products.addDataItem(product);
    }

    Summary summary = input.getSummary();
    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);
    return products;
  }

  @Override
  @LogExecutionTime
  public Object transform(Map<String, Object> kvp, List<String> fields) {
    return generateWyriwygProduct(kvp, fields);
  }

}
