package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import gov.nasa.pds.model.Pds4Metadata;
import gov.nasa.pds.model.Pds4MetadataOpsDataFile;
import gov.nasa.pds.model.Pds4MetadataOpsLabelFileInfo;
import gov.nasa.pds.model.Pds4MetadataOpsTrackingMeta;
import gov.nasa.pds.model.Pds4Product;

/**
 * Creates Pds4Product object from opensearch key-value field map.
 *
 * @author karpenko
 */
public class Pds4ProductFactory {
  private static final Logger log = LoggerFactory.getLogger(Pds4ProductFactory.class);

  private static final String OPENSEARCH_JSON_BLOB = PdsProperty.toOpenPropertyString(PdsProperty.JSON_BLOB);
  private static final String OPENSEARCH_XML_BLOB = PdsProperty.toOpenPropertyString(PdsProperty.XML_BLOB);
  private static final String OPENSEARCH_NODE_NAME = PdsProperty.toOpenPropertyString(PdsProperty.NODE_NAME);
  private static final String OPENSEARCH_LABEL_FILE_NAME = PdsProperty.toOpenPropertyString(PdsProperty.LABEL_FILE.NAME);
  private static final String OPENSEARCH_LABEL_FILE_CREATION = PdsProperty.toOpenPropertyString(PdsProperty.LABEL_FILE.CREATION);
  private static final String OPENSEARCH_LABEL_FILE_REF = PdsProperty.toOpenPropertyString(PdsProperty.LABEL_FILE.REF);
  private static final String OPENSEARCH_LABEL_FILE_SIZE = PdsProperty.toOpenPropertyString(PdsProperty.LABEL_FILE.SIZE);
  private static final String OPENSEARCH_LABEL_FILE_MD5 = PdsProperty.toOpenPropertyString(PdsProperty.LABEL_FILE.MD5);
  private static final String OPENSEARCH_DATA_FILE_NAME = PdsProperty.toOpenPropertyString(PdsProperty.DATA_FILE.NAME);
  private static final String OPENSEARCH_DATA_FILE_CREATION = PdsProperty.toOpenPropertyString(PdsProperty.DATA_FILE.CREATION);
  private static final String OPENSEARCH_DATA_FILE_REF = PdsProperty.toOpenPropertyString(PdsProperty.DATA_FILE.REF);
  private static final String OPENSEARCH_DATA_FILE_SIZE = PdsProperty.toOpenPropertyString(PdsProperty.DATA_FILE.SIZE);
  private static final String OPENSEARCH_DATA_FILE_MD5 = PdsProperty.toOpenPropertyString(PdsProperty.DATA_FILE.MD5);
  private static final String OPENSEARCH_DATA_FILE_MIME_TYPE = PdsProperty.toOpenPropertyString(PdsProperty.DATA_FILE.MIME_TYPE);
  private static final String OPENSEARCH_TRACK_META_ARCHIVE_STATUS = PdsProperty.toOpenPropertyString(PdsProperty.TRACK_META_ARCHIVE_STATUS);

  /**
   * Create Pds4Product object from opensearch key-value field map.
   *
   * @param lidvid product LIDVID
   * @param fieldMap key-value field map
   * @return new Pds4Product object
   */
  public static Pds4Product createProduct(String lidvid, Map<String, Object> fieldMap,
      boolean isJSON) {
    log.debug("Creating Pds4Product with id {} object from opensearch key-value field map with keys {}", lidvid, fieldMap.keySet());
    
    Pds4Product prod = new Pds4Product();
    prod.setId(lidvid);

    if (fieldMap == null)
      return prod;

    // Pds4 JSON BLOB
    String blob = null;
    String decoded_blob = null;
    try {

      if (isJSON) {
        blob = getVal(fieldMap, OPENSEARCH_JSON_BLOB);
        decoded_blob = BlobUtil.blobToString(String.valueOf(blob));
      } else {
        int first, last;
        blob = getVal(fieldMap, OPENSEARCH_XML_BLOB);
        decoded_blob = BlobUtil.blobToString(String.valueOf(blob));
        decoded_blob = decoded_blob.replaceAll("\r", "");
        first = decoded_blob.indexOf("<?");
        while (0 <= first) {
          last = decoded_blob.indexOf("?>", first + 2);
          decoded_blob = decoded_blob.replace(decoded_blob.substring(first, last + 2), "");
          first = decoded_blob.indexOf("<?");
        }
        decoded_blob = decoded_blob.strip();
      }
    } catch (Exception e) {
      log.error("Could not convert the given blob", e);
      decoded_blob = "Could not decode blob. See logs for error details.";
    }
    prod.setPds4(decoded_blob);
    // Metadata
    prod.setMetadata(createMetadata(fieldMap));

    return prod;
  }

  private static Pds4Metadata createMetadata(Map<String, Object> fieldMap) {
    Pds4Metadata meta = new Pds4Metadata();

    ArrayList<String> nodeNames = (ArrayList<String>) fieldMap.get(OPENSEARCH_NODE_NAME);
    String nodeName = nodeNames.get(0);
    meta.setNodeName(nodeName);
    meta.setOpsColonLabelFileInfo(createLabelFile(fieldMap));
    meta.setOpsColonDataFiles(createDataFiles(fieldMap));
    meta.setOpsColonTrackingMeta(createTrackingMeta(fieldMap));
    return meta;
  }

  @SuppressWarnings("unchecked")
  private static String getVal(Map<String, Object> fieldMap, String fieldName) {
    return ((ArrayList<String>) fieldMap.get(fieldName)).get(0);
  }

  private static Pds4MetadataOpsLabelFileInfo createLabelFile(Map<String, Object> fieldMap) {
    ArrayList<String> vals = (ArrayList<String>) fieldMap.get(OPENSEARCH_LABEL_FILE_NAME);

    if (vals == null)
      return null;

    Pds4MetadataOpsLabelFileInfo item = new Pds4MetadataOpsLabelFileInfo();

    String val = vals.get(0);

    item.setOpsColonFileName(val);

    val = getVal(fieldMap, OPENSEARCH_LABEL_FILE_CREATION);
    item.setOpsColonCreationDate(val);

    val = getVal(fieldMap, OPENSEARCH_LABEL_FILE_REF);
    item.setOpsColonFileRef(val);

    val = getVal(fieldMap, OPENSEARCH_LABEL_FILE_SIZE);
    item.setOpsColonFileSize(val);

    val = getVal(fieldMap, OPENSEARCH_LABEL_FILE_MD5);
    item.setOpsColonMd5Checksum(val);

    return item;
  }

  @SuppressWarnings("rawtypes")
  private static List<Pds4MetadataOpsDataFile> createDataFiles(Map<String, Object> fieldMap) {
    ArrayList<String> vals = (ArrayList<String>) fieldMap.get(OPENSEARCH_DATA_FILE_NAME);
    if (vals == null) {
      return new ArrayList<Pds4MetadataOpsDataFile>();
    }

    List<Pds4MetadataOpsDataFile> items = new ArrayList<Pds4MetadataOpsDataFile>();

    for (int i = 0; i < ((List) vals).size(); i++) {
      Pds4MetadataOpsDataFile item = new Pds4MetadataOpsDataFile();
      item.setOpsColonFileName(
          (String) ((List) fieldMap.get(OPENSEARCH_DATA_FILE_CREATION)).get(i));
      item.setOpsColonCreationDate(
          (String) ((List) fieldMap.get(OPENSEARCH_DATA_FILE_CREATION)).get(i));
      item.opsColonFileRef((String) ((List) fieldMap.get(OPENSEARCH_DATA_FILE_REF)).get(i));
      item.setOpsColonFileSize(
          (String) ((List) fieldMap.get(OPENSEARCH_DATA_FILE_SIZE)).get(i));
      item.setOpsColonMd5Checksum(
          (String) ((List) fieldMap.get(OPENSEARCH_DATA_FILE_MD5)).get(i));
      item.setOpsColonMimeType(
          (String) ((List) fieldMap.get(OPENSEARCH_DATA_FILE_MIME_TYPE)).get(i));
      items.add(item);
    }

    return items;
  }

  private static Pds4MetadataOpsTrackingMeta createTrackingMeta(Map<String, Object> fieldMap) {
    Pds4MetadataOpsTrackingMeta item = new Pds4MetadataOpsTrackingMeta();

    item.setOpsColonArchiveStatus(
        (String) ((List<?>) fieldMap.get(OPENSEARCH_TRACK_META_ARCHIVE_STATUS)).get(0));
    return item;
  }
}
