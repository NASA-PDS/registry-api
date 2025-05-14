package gov.nasa.pds.api.registry.model.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import gov.nasa.pds.api.registry.model.properties.PdsPropertyConstants;
import gov.nasa.pds.api.registry.util.LogExecutionTime;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.model.WyriwygProduct;
import gov.nasa.pds.model.WyriwygProductKeyValuePair;
import gov.nasa.pds.model.WyriwygProducts;

public class WyriwygProductTransformer extends ResponseTransformerImpl {

  private static final Logger log = LoggerFactory.getLogger(WyriwygProductTransformer.class);

  protected boolean isJSON;


  private static final List<PdsProperty> EXCLUDED_PROPERTIES =
      List.of(PdsPropertyConstants.XML_BLOB, PdsPropertyConstants.JSON_BLOB);

  private WyriwygProduct generateWyriwygProduct(Map<String, Object> hit,
      List<PdsProperty> includedFields) {
    WyriwygProduct product = new WyriwygProduct();
    PdsProperty pdsProperty;
    List<String> includedFieldJsonStrings = includedFields == null ? null
        : includedFields.stream().map(PdsProperty::toJsonPropertyString).toList();
    log.debug("Included fields are {}", includedFieldJsonStrings);
    for (Entry<String, Object> pair : hit.entrySet()) {
      WyriwygProductKeyValuePair kvp = new WyriwygProductKeyValuePair();
      pdsProperty = new PdsProperty(pair.getKey());
      if (!EXCLUDED_PROPERTIES.contains(pdsProperty) && (includedFields == null
          || includedFields.isEmpty() || includedFields.contains(pdsProperty))) {
        log.debug(
            "In this case we keep the property in the response, it is not excluded and request implicitly or explicitelly by the user");
        kvp.setKey(pdsProperty.toJsonPropertyString());
        kvp.setValue(getStringValueOf(pair.getValue()));
        product.addKeyValuePairsItem(kvp);
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
  public Object transform(RawMultipleProductResponse input, List<PdsProperty> fields) {
    Set<String> uniqueProperties = new TreeSet<String>();
    WyriwygProducts products = new WyriwygProducts();

    for (Map<String, Object> hit : input.getProducts()) {
      // TODO: check if uniqueProperties are useful in the context of a WYSIWYG product
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
  public Object transform(Map<String, Object> kvp, List<PdsProperty> fields) {
    return generateWyriwygProduct(kvp, fields);
  }

}
