package gov.nasa.pds.api.registry.model.api_responses;

import java.net.URL;
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
import gov.nasa.pds.api.registry.model.Pds4ProductFactory;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.model.Pds4Product;
import gov.nasa.pds.model.Pds4Products;
import gov.nasa.pds.model.Summary;


public class Pds4ProductBusinessObject extends ProductBusinessLogicImpl {
  private static final Logger log = LoggerFactory.getLogger(Pds4ProductBusinessObject.class);
  @SuppressWarnings("unused")
  private ObjectMapper objectMapper;
  private Pds4Product product = null;
  private Pds4Products products = null;
  @SuppressWarnings("unused")
  private URL baseURL;

  public final boolean isJSON;
  public final String[] PDS4_PRODUCT_FIELDS;

  public Pds4ProductBusinessObject(boolean isJSON) {
    super();
    String temp[] = {
        // BLOB
        (isJSON ? Pds4ProductFactory.FLD_JSON_BLOB : Pds4ProductFactory.FLD_XML_BLOB),

        // Data File Info
        Pds4ProductFactory.FLD_DATA_FILE_NAME, Pds4ProductFactory.FLD_DATA_FILE_CREATION,
        Pds4ProductFactory.FLD_DATA_FILE_REF, Pds4ProductFactory.FLD_DATA_FILE_SIZE,
        Pds4ProductFactory.FLD_DATA_FILE_MD5, Pds4ProductFactory.FLD_DATA_FILE_MIME_TYPE,

        // Label Info
        Pds4ProductFactory.FLD_LABEL_FILE_NAME, Pds4ProductFactory.FLD_LABEL_FILE_CREATION,
        Pds4ProductFactory.FLD_LABEL_FILE_REF, Pds4ProductFactory.FLD_LABEL_FILE_SIZE,
        Pds4ProductFactory.FLD_LABEL_FILE_MD5,

        // Tracking Meta
        Pds4ProductFactory.FLD_TRACK_META_ARCHIVE_STATUS,

        // Node Name
        Pds4ProductFactory.FLD_NODE_NAME};

    this.PDS4_PRODUCT_FIELDS = temp;
    this.isJSON = isJSON;
  }

  @Override
  public String[] getMaximallyRequiredFields() {
    return this.PDS4_PRODUCT_FIELDS;
  }

  @Override
  public String[] getMinimallyRequiredFields() {
    return this.PDS4_PRODUCT_FIELDS;
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
    // TODO: to be implemented
    this.product = null;
  }

  @Override
  public void setResponse(SearchHit hit, List<String> fields) {
    this.product = Pds4ProductFactory.createProduct(hit.getId(), hit.getSourceAsMap(), this.isJSON);
  }

  @Override
  public int setResponse(HitIterator hits, Summary summary, List<String> fields) {
    List<Pds4Product> list = new ArrayList<Pds4Product>();
    Pds4Products products = new Pds4Products();
    Set<String> uniqueProperties = new TreeSet<String>();

    for (Map<String, Object> kvp : hits) {

      uniqueProperties.addAll(getFilteredProperties(kvp, fields, null).keySet());

      try {
        Pds4Product prod = Pds4ProductFactory.createProduct(hits.getCurrentId(), kvp, this.isJSON);
        list.add(prod);
      } catch (Throwable t) {
        String lidvid = "unknown";
        if (kvp.containsKey("lidvid")) {
          lidvid = kvp.get("lidvid").toString();
        }
        log.error("DATA ERROR: could not convert opensearch document to Pds4Product for lidvid: "
            + lidvid, t);
      }
    }

    products.setData(list);
    products.setSummary(summary);
    summary.setProperties(new ArrayList<String>(uniqueProperties));
    this.products = products;
    return list.size();
  }

  @Override
  public int setResponse(SearchHits hits, Summary summary, List<String> fields) {
    List<Pds4Product> list = new ArrayList<Pds4Product>();
    Pds4Products products = new Pds4Products();
    Set<String> uniqueProperties = new TreeSet<String>();

    // Products
    for (SearchHit hit : hits) {
      String id = hit.getId();
      Map<String, Object> fieldMap = hit.getSourceAsMap();

      uniqueProperties.addAll(getFilteredProperties(fieldMap, fields, null).keySet());

      try {
        Pds4Product prod = Pds4ProductFactory.createProduct(id, fieldMap, this.isJSON);
        list.add(prod);
      } catch (Throwable t) {
        log.error("DATA ERROR: could not convert opensearch document to Pds4Product for lidvid: "
            + hit.getId(), t);
      }
    }
    products.setData(list);
    products.setSummary(summary);
    summary.setProperties(new ArrayList<String>(uniqueProperties));
    this.products = products;
    return (int) hits.getTotalHits().value;
  }


  @Override
  public void setResponse(List<Map<String, Object>> hits, Summary summary, List<String> fields) {
    List<Pds4Product> list = new ArrayList<Pds4Product>();
    Pds4Products products = new Pds4Products();
    Set<String> uniqueProperties = new TreeSet<String>();
    String id;
    // Products
    for (Map<String, Object> hit : hits) {
      // TODO complete that
      id = (String) hit.get("_id");
      uniqueProperties.addAll(getFilteredProperties(hit, fields, null).keySet());

      try {
        Pds4Product prod = Pds4ProductFactory.createProduct(id, hit, this.isJSON);
        list.add(prod);
      } catch (Throwable t) {
        log.error(
            "DATA ERROR: could not convert opensearch document to Pds4Product for lidvid: " + id,
            t);
      }
    }
    products.setData(list);
    products.setSummary(summary);
    summary.setProperties(new ArrayList<String>(uniqueProperties));
    this.products = products;
  }


}
