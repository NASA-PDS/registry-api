package gov.nasa.pds.api.registry.model.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.model.EntityProduct;
import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.SearchUtil;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import gov.nasa.pds.api.registry.model.properties.PdsPropertyConstants;
import gov.nasa.pds.api.registry.util.LogExecutionTime;
import gov.nasa.pds.model.PdsProduct;
import gov.nasa.pds.model.PdsProducts;
import gov.nasa.pds.model.Summary;

public class PdsProductTransformer extends ResponseTransformerImpl {

  private static final Logger log = LoggerFactory.getLogger(PdsProductTransformer.class);

  protected boolean isJSON;
  protected static final List<PdsProperty> REQUIRED_FIELDS = List.of(PdsPropertyConstants.LIDVID,
      PdsPropertyConstants.TITLE, PdsPropertyConstants.PRODUCT_CLASS,
      PdsPropertyConstants.START_DATE_TIME, PdsPropertyConstants.STOP_DATE_TIME,
      PdsPropertyConstants.MODIFICATION_DATE, PdsPropertyConstants.CREATION_DATE_TIME,
      PdsPropertyConstants.REF_LID_INSTRUMENT_HOST, PdsPropertyConstants.REF_LID_INSTRUMENT,
      PdsPropertyConstants.REF_LID_INVESTIGATION, PdsPropertyConstants.REF_LID_TARGET,
      PdsPropertyConstants.VID, PdsPropertyConstants.DATA_FILE.REF,
      PdsPropertyConstants.TRACK_META_ARCHIVE_STATUS, PdsPropertyConstants.LABEL_FILE.REF);


  private static final List<PdsProperty> EXCLUDED_PROPERTIES =
      List.of(PdsPropertyConstants.XML_BLOB, PdsPropertyConstants.JSON_BLOB);

  @Override
  public List<PdsProperty> getRequestedFields(List<PdsProperty> userRequestFields) {
    if (userRequestFields != null && !userRequestFields.isEmpty()) {
      List<PdsProperty> allFields = new ArrayList<PdsProperty>(REQUIRED_FIELDS);
      allFields.addAll(userRequestFields);
      return new ArrayList<PdsProperty>(new TreeSet<PdsProperty>(allFields));
    } else {
      // if the user did not specify anything, everything will be returned
      // we want to keep it like that
      return null;
    }
  }

  @Override
  @LogExecutionTime
  public Object transform(RawMultipleProductResponse input, List<PdsProperty> fields) {
    log.debug("transform: fields: {}, excluded fields: {}", fields, EXCLUDED_PROPERTIES);
    PdsProducts products = new PdsProducts();
    Set<String> uniqueProperties = new TreeSet<String>();
    Map<String, List<String>> properties;

    for (Map<String, Object> kvp : input.getProducts()) {
      try {
        PdsProduct product = SearchUtil.entityProductToAPIProduct(
            objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL);

        // TODO check why every value is a String
        properties = getFilteredProperties(kvp, fields, EXCLUDED_PROPERTIES);

        product.setProperties(properties);

        uniqueProperties.addAll(properties.keySet());

        products.addDataItem(product);
      } catch (Throwable t) {
        String lidvid = (String) kvp.getOrDefault("lidvid", "unknown");
        log.error("DATA ERROR: could not convert opensearch document to EntityProduct for lidvid: "
            + lidvid, t);
      }
    }

    Summary summary = input.getSummary();
    summary.setProperties(new ArrayList<String>(uniqueProperties));
    products.setSummary(summary);

    return products;
  }

  @Override
  @LogExecutionTime
  public Object transform(Map<String, Object> kvp, List<PdsProperty> fields) {
    EntityProduct ep = objectMapper.convertValue(kvp, EntityProduct.class);
    PdsProduct product = SearchUtil.entityProductToAPIProduct(ep, this.baseURL);

    Map<String, List<String>> properties = getFilteredProperties(kvp, fields, EXCLUDED_PROPERTIES);

    product.setProperties(properties);
    return product;
  }

}
