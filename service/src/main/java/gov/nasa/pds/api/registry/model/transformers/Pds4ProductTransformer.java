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
import gov.nasa.pds.model.Pds4Product;
import gov.nasa.pds.model.Pds4Products;
import gov.nasa.pds.model.Summary;

public abstract class Pds4ProductTransformer extends ResponseTransformerImpl {

  private static final Logger log = LoggerFactory.getLogger(Pds4ProductTransformer.class);

  protected boolean isJSON;
  protected static final List<String> REQUIRED_FIELDS = List.of(
    PdsProperty.DATA_FILE_NAME, PdsProperty.DATA_FILE_CREATION,
    PdsProperty.DATA_FILE_REF, PdsProperty.DATA_FILE_SIZE,
    PdsProperty.DATA_FILE_MD5, PdsProperty.DATA_FILE_MIME_TYPE,

    // Label Info
    PdsProperty.LABEL_FILE_NAME, PdsProperty.LABEL_FILE_CREATION,
    PdsProperty.LABEL_FILE_REF, PdsProperty.LABEL_FILE_SIZE,
    PdsProperty.LABEL_FILE_MD5,

    // Tracking Meta
    PdsProperty.TRACK_META_ARCHIVE_STATUS,

    // Node Name
    PdsProperty.NODE_NAME);


  public Pds4ProductTransformer(boolean isJSON) {
    this.isJSON = isJSON;
  }

  @Override
  public List<String> getRequestedFields(List<String> userRequestFields) {
    return userRequestFields;
  }


  @Override
  @LogExecutionTime
  public Object transform(RawMultipleProductResponse input, List<String> fields) {
    List<Pds4Product> list = new ArrayList<>();
    Pds4Products products = new Pds4Products();
    Set<String> uniqueProperties = new TreeSet<>();
    String id;
    // Products
    for (Map<String, Object> hit : input.getProducts()) {
      id = (String) hit.get("lidvid");
      uniqueProperties.addAll(getFilteredProperties(hit, fields, null).keySet());

      try {
        Pds4Product prod = Pds4ProductFactory.createProduct(id, hit, this.isJSON);
        list.add(prod);
      } catch (Throwable t) {
        log.error("DATA ERROR: could not convert opensearch document to Pds4Product for lidvid: " + id, t);
      }
    }
    products.setData(list);
    Summary summary = input.getSummary();
    summary.setProperties(new ArrayList<>(uniqueProperties));
    products.setSummary(summary);

    return products;
  }

  @Override
  @LogExecutionTime
  public Object transform(Map<String, Object> kvp, List<String> fields) {
    String id = (String) kvp.get("lidvid");

    // TODO add warning fields are not supported

    return Pds4ProductFactory.createProduct(id, kvp, this.isJSON);
  }
}
