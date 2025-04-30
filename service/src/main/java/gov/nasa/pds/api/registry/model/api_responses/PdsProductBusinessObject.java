package gov.nasa.pds.api.registry.model.api_responses;

import java.util.ArrayList;
import java.util.Arrays;
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
import gov.nasa.pds.api.registry.model.exceptions.UnauthorizedForwardedHostException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.OpenSearchFields;
import gov.nasa.pds.model.PdsProduct;
import gov.nasa.pds.model.PdsProducts;
import gov.nasa.pds.model.Summary;


public class PdsProductBusinessObject extends ProductBusinessLogicImpl {

  private static final Logger log = LoggerFactory.getLogger(PdsProductBusinessObject.class);

  private final static String[] REQUIRED_OPENSEARCH_FIELDS =
      {OpenSearchFields.DATA_FILE_NAME, OpenSearchFields.DATA_FILE_CREATION,
          OpenSearchFields.DATA_FILE_REF, OpenSearchFields.DATA_FILE_SIZE,
          OpenSearchFields.DATA_FILE_MD5, OpenSearchFields.DATA_FILE_MIME_TYPE,

          // Label Info
          OpenSearchFields.LABEL_FILE_NAME, OpenSearchFields.LABEL_FILE_CREATION,
          OpenSearchFields.LABEL_FILE_REF, OpenSearchFields.LABEL_FILE_SIZE,
          OpenSearchFields.LABEL_FILE_MD5,

          // Tracking Meta
          OpenSearchFields.TRACK_META_ARCHIVE_STATUS,

          // Node Name
          OpenSearchFields.NODE_NAME};

  private ObjectMapper objectMapper;
  private PdsProduct product = null;
  private PdsProducts products = null;

  public PdsProductBusinessObject() throws UnauthorizedForwardedHostException {
    super();
    // TODO Auto-generated constructor stub
  }

  public static String[] getOpensearchRequiredFields(String[] requestedFields) {

    return REQUIRED_OPENSEARCH_FIELDS;

  }


  public static String[] getAPIHiddenFields() {
    return new String[] {OpenSearchFields.JSON_BLOB};
  }



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

    product.setProperties(
        (Map<String, List<String>>) getFilteredProperties(kvp, fields, excludedProperties));
    this.product = product;
  }


  @Override
  @SuppressWarnings("unchecked")
  public void setResponse(List<Map<String, Object>> hits, Summary summary, List<String> fields) {
    PdsProducts products = new PdsProducts();
    Set<String> uniqueProperties = new TreeSet<String>();

    for (Map<String, Object> kvp : hits) {
      try {
        uniqueProperties.addAll(getFilteredProperties(kvp, fields, excludedProperties).keySet());

        products.addDataItem(SearchUtil.entityProductToAPIProduct(
            objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
        products.getData().get(products.getData().size() - 1)
            .setProperties((Map<String, List<String>>) (Map<String, ?>) getFilteredProperties(kvp,
                fields, excludedProperties));
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


}
