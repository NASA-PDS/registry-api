package gov.nasa.pds.api.registry.model.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.model.Pds4ProductFactory;
import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import gov.nasa.pds.api.registry.model.properties.PdsPropertyConstants;
import gov.nasa.pds.model.Pds4Product;
import gov.nasa.pds.model.Pds4Products;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.api.registry.util.LogExecutionTime;

public abstract class Pds4ProductTransformer extends ResponseTransformerImpl {

  private static final Logger log = LoggerFactory.getLogger(Pds4ProductTransformer.class);

  protected boolean isJSON;
  protected static final List<PdsProperty> REQUIRED_FIELDS = List.of(PdsPropertyConstants.LIDVID,
      PdsPropertyConstants.DATA_FILE.NAME, PdsPropertyConstants.DATA_FILE.CREATION,
      PdsPropertyConstants.DATA_FILE.REF, PdsPropertyConstants.DATA_FILE.SIZE,
      PdsPropertyConstants.DATA_FILE.MD5, PdsPropertyConstants.DATA_FILE.MIME_TYPE,

      // Label Info
      PdsPropertyConstants.LABEL_FILE.NAME, PdsPropertyConstants.LABEL_FILE.CREATION,
      PdsPropertyConstants.LABEL_FILE.REF, PdsPropertyConstants.LABEL_FILE.SIZE,
      PdsPropertyConstants.LABEL_FILE.MD5,

      // Tracking Meta
      PdsPropertyConstants.TRACK_META_ARCHIVE_STATUS,

      // Node Name
      PdsPropertyConstants.NODE_NAME);


  protected Pds4ProductTransformer(boolean isJSON) {
    this.isJSON = isJSON;
  }

  @Override
  public List<PdsProperty> getRequestedFields(List<PdsProperty> userRequestFields) {
    return userRequestFields;
  }


  @Override
  @LogExecutionTime
  public Object transform(RawMultipleProductResponse input, List<PdsProperty> pdsProperties) {
    List<Pds4Product> list = new ArrayList<>();
    Pds4Products products = new Pds4Products();
    Set<String> uniqueJsonProperties = new TreeSet<>();
    String id;
    // Products
    for (Map<String, Object> hit : input.getProducts()) {
      id = (String) hit.get("lidvid");
      uniqueJsonProperties.addAll(getFilteredProperties(hit, pdsProperties, null).keySet());

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
    Summary summary = input.getSummary();
    summary.setProperties(new ArrayList<>(uniqueJsonProperties));
    products.setSummary(summary);

    return products;
  }

  @Override
  @LogExecutionTime
  public Object transform(Map<String, Object> kvp, List<PdsProperty> fields) {
    String id = (String) kvp.get(PdsPropertyConstants.LIDVID.toOpenPropertyString());

    // TODO add warning fields are not supported

    return Pds4ProductFactory.createProduct(id, kvp, this.isJSON);
  }
}
