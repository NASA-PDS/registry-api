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
import gov.nasa.pds.api.registry.model.exceptions.UnauthorizedForwardedHostException;
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

  private boolean isJSON;
  public final String[] PDS4_PRODUCT_FIELDS;

  public Pds4ProductBusinessObject(boolean isJSON) throws UnauthorizedForwardedHostException {
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

    this.isJSON = isJSON;
    this.PDS4_PRODUCT_FIELDS = temp;

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
    String id = (String) kvp.get("lidvid");
    this.product = Pds4ProductFactory.createProduct(id, kvp, this.isJSON);

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
      id = (String) hit.get("lidvid");
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
