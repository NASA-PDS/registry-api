package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.model.PdsProduct;
import gov.nasa.pds.model.PdsProducts;
import gov.nasa.pds.model.Summary;


public class PdsProductBusinessObject extends ProductBusinessLogicImpl {
  private ObjectMapper objectMapper;
  private PdsProduct product = null;
  private PdsProducts products = null;


  @Override
  public String[] getMaximallyRequiredFields() {
    return new String[0];
  }

  @Override
  public String[] getMinimallyRequiredFields() {
    return EntityProduct.JSON_PROPERTIES;
  }

  @Override
  public Object getResponse() {
    return this.product == null ? this.products : this.product;
  }

  @Override
  public void setObjectMapper(ObjectMapper om) {
    this.objectMapper = om;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setResponse(SearchHit hit, List<String> fields) {
    Map<String, Object> kvp = hit.getSourceAsMap();;
    PdsProduct product;

    product = SearchUtil.entityProductToAPIProduct(
        objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL);

    product.setProperties(
        (Map<String, List<String>>) ProductBusinessObject.getFilteredProperties(kvp, null, null));
    this.product = product;
  }

  @Override
  @SuppressWarnings("unchecked")
  public int setResponse(HitIterator hits, Summary summary, List<String> fields) {
    int count;
    PdsProducts products = new PdsProducts();
    Set<String> uniqueProperties = new TreeSet<String>();

    for (Map<String, Object> kvp : hits) {
      uniqueProperties
          .addAll(ProductBusinessObject.getFilteredProperties(kvp, fields, null).keySet());


      products.addDataItem(SearchUtil.entityProductToAPIProduct(
          objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
      products.getData().get(products.getData().size() - 1)
          .setProperties((Map<String, List<String>>) (Map<String, ?>) ProductBusinessObject
              .getFilteredProperties(kvp, null, null));
    }
    count = products.getData().size();

    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);
    this.products = products;
    return count;
  }

  @Override
  @SuppressWarnings("unchecked")
  public int setResponse(SearchHits hits, Summary summary, List<String> fields) {
    Map<String, Object> kvp;
    PdsProducts products = new PdsProducts();
    Set<String> uniqueProperties = new TreeSet<String>();

    for (SearchHit hit : hits) {
      kvp = hit.getSourceAsMap();
      uniqueProperties
          .addAll(ProductBusinessObject.getFilteredProperties(kvp, fields, null).keySet());

      products.addDataItem(SearchUtil.entityProductToAPIProduct(
          objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
      products.getData().get(products.getData().size() - 1)
          .setProperties((Map<String, List<String>>) (Map<String, ?>) ProductBusinessObject
              .getFilteredProperties(kvp, null, null));
    }

    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);
    this.products = products;
    return (int) hits.getTotalHits().value;
  }
}
