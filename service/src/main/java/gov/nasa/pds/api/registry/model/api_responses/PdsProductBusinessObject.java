package gov.nasa.pds.api.registry.model.api_responses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.registry.model.EntityProduct;
import gov.nasa.pds.api.registry.model.SearchUtil;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.model.PdsProduct;
import gov.nasa.pds.model.PdsProducts;
import gov.nasa.pds.model.Summary;


public class PdsProductBusinessObject extends ProductBusinessLogicImpl {
  private static final Logger log = LoggerFactory.getLogger(PdsProductBusinessObject.class);
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
  public void setResponse(Map<String, Object> kvp, List<String> fields) {
    EntityProduct ep = objectMapper.convertValue(kvp, EntityProduct.class);
    product = SearchUtil.entityProductToAPIProduct(ep, this.baseURL);
    PdsProduct product = SearchUtil.entityProductToAPIProduct(
        objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL);


    // TODO: findout why the getFilteredProperties method is used here. Should we add fields as a
    // second argument instead of null ?
    product.setProperties((Map<String, List<String>>) getFilteredProperties(kvp, null, null));
    this.product = product;
  }


  @Override
  @SuppressWarnings("unchecked")
  public void setResponse(SearchHit hit, List<String> fields) {
    Map<String, Object> kvp = hit.getSourceAsMap();;

    this.setResponse(kvp, fields);

  }


  @Override
  @SuppressWarnings("unchecked")
  public void setResponse(List<Map<String, Object>> hits, Summary summary, List<String> fields) {
    PdsProducts products = new PdsProducts();
    Set<String> uniqueProperties = new TreeSet<String>();

    for (Map<String, Object> kvp : hits) {
      try {
        uniqueProperties.addAll(getFilteredProperties(kvp, fields, null).keySet());

        products.addDataItem(SearchUtil.entityProductToAPIProduct(
            objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
        products.getData().get(products.getData().size() - 1).setProperties(
            (Map<String, List<String>>) (Map<String, ?>) getFilteredProperties(kvp, null, null));
      } catch (Throwable t) {
        String lidvid = "unknown";
        if (kvp.containsKey("lidvid")) {
          lidvid = kvp.get("lidvid").toString();
        }
        log.error("DATA ERROR: could not convert opensearch document to EntityProduct for lidvid: "
            + lidvid, t);
      }
    }

    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);
    this.products = products;
  }



  @Override
  @SuppressWarnings("unchecked")
  public int setResponse(HitIterator hits, Summary summary, List<String> fields) {
    int count;
    PdsProducts products = new PdsProducts();
    Set<String> uniqueProperties = new TreeSet<String>();

    for (Map<String, Object> kvp : hits) {
      try {
        uniqueProperties.addAll(getFilteredProperties(kvp, fields, null).keySet());

        products.addDataItem(SearchUtil.entityProductToAPIProduct(
            objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
        products.getData().get(products.getData().size() - 1).setProperties(
            (Map<String, List<String>>) (Map<String, ?>) getFilteredProperties(kvp, null, null));
      } catch (Throwable t) {
        String lidvid = "unknown";
        if (kvp.containsKey("lidvid")) {
          lidvid = kvp.get("lidvid").toString();
        }
        log.error("DATA ERROR: could not convert opensearch document to EntityProduct for lidvid: "
            + lidvid, t);
      }
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
      try {
        kvp = hit.getSourceAsMap();
        uniqueProperties.addAll(getFilteredProperties(kvp, fields, null).keySet());

        products.addDataItem(SearchUtil.entityProductToAPIProduct(
            objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
        // TODO: understand why the getFilteredProperties method is called with null arguments,
        // should we add fields or not call the method at all ?
        products.getData().get(products.getData().size() - 1).setProperties(
            (Map<String, List<String>>) (Map<String, ?>) getFilteredProperties(kvp, null, null));
      } catch (Throwable t) {
        log.error("DATA ERROR: could not convert opensearch document to EntityProduct for lidvid: "
            + hit.getId(), t);
      }
    }

    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);
    this.products = products;
    return (int) hits.getTotalHits().value;
  }
}
