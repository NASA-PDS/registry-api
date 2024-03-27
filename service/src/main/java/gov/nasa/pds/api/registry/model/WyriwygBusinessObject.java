package gov.nasa.pds.api.registry.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.model.WyriwygProduct;
import gov.nasa.pds.model.WyriwygProductKeyValuePair;
import gov.nasa.pds.model.WyriwygProducts;

public class WyriwygBusinessObject extends ProductBusinessLogicImpl {
  private static final Logger log = LoggerFactory.getLogger(WyriwygBusinessObject.class);

  @SuppressWarnings("unused")
  private ObjectMapper om;
  @SuppressWarnings("unused")
  private URL baseURL;
  private WyriwygProduct product = null;
  private WyriwygProducts products = null;


  @Override
  public String[] getMaximallyRequiredFields() {
    return new String[0];
  }

  @Override
  public String[] getMinimallyRequiredFields() {
    return new String[0];
  }

  @Override
  public Object getResponse() {
    return this.product == null ? this.products : this.product;
  }

  @Override
  public void setObjectMapper(ObjectMapper om) {
    this.om = om;
  }


  @Override
  public void setResponse(Map<String, Object> kvps, List<String> fields) {
    // TODO: to be implemented
    WyriwygProduct product = new WyriwygProduct();
    for (Entry<String, Object> pair : kvps.entrySet()) {
      WyriwygProductKeyValuePair kvp = new WyriwygProductKeyValuePair();
      try {
        kvp.setKey(SearchUtil.openPropertyToJsonProperty(pair.getKey()));
        kvp.setValue(getStringValueOf(pair.getValue()));
        product.addKeyValuePairsItem(kvp);
      } catch (UnsupportedSearchProperty e) {
        log.warn("openSearch property " + pair.getKey() + " is not supported, ignored");
      }
    }

    this.product = product;
  }

  @Override
  public void setResponse(SearchHit hit, List<String> fields) {
    this.setResponse(hit.getSourceAsMap(), fields);
  }

  @Override
  public int setResponse(HitIterator hits, Summary summary, List<String> fields) {
    Set<String> uniqueProperties = new TreeSet<String>();
    WyriwygProducts products = new WyriwygProducts();

    for (Map<String, Object> kvps : hits) {
      uniqueProperties
          .addAll(ProductBusinessObject.getFilteredProperties(kvps, fields, null).keySet());

      WyriwygProduct product = new WyriwygProduct();
      for (Entry<String, Object> pair : kvps.entrySet()) {
        WyriwygProductKeyValuePair kvp = new WyriwygProductKeyValuePair();
        try {
          kvp.setKey(SearchUtil.openPropertyToJsonProperty(pair.getKey()));
          kvp.setValue(getStringValueOf(pair.getValue()));
          product.addKeyValuePairsItem(kvp);
        } catch (UnsupportedSearchProperty e) {
          log.warn("openSearch property " + pair.getKey() + " is not supported, ignored");
        }
      }
      products.addDataItem(product);

    }
    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);
    this.products = products;
    return products.getData().size();
  }

  @Override
  public int setResponse(SearchHits hits, Summary summary, List<String> fields) {

    Set<String> uniqueProperties = new TreeSet<String>();
    WyriwygProducts products = new WyriwygProducts();

    for (SearchHit hit : hits.getHits()) {
      Map<String, Object> kvps = hit.getSourceAsMap();
      uniqueProperties
          .addAll(ProductBusinessObject.getFilteredProperties(kvps, fields, null).keySet());

      WyriwygProduct product = new WyriwygProduct();
      for (Entry<String, Object> pair : kvps.entrySet()) {
        WyriwygProductKeyValuePair kvp = new WyriwygProductKeyValuePair();
        try {
          kvp.setKey(SearchUtil.openPropertyToJsonProperty(pair.getKey()));
          kvp.setValue(getStringValueOf(pair.getValue()));
          product.addKeyValuePairsItem(kvp);
        } catch (UnsupportedSearchProperty e) {
          log.warn("openSearch property " + pair.getKey() + " is not supported, ignored");
        }
      }
      products.addDataItem(product);
    }

    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);
    this.products = products;
    return (int) (hits.getTotalHits().value);
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
}
