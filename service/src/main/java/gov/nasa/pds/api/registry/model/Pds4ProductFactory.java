package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  // JSON BLOB
  public static final String FLD_JSON_BLOB = BlobUtil.JSON_BLOB_PROPERTY;
  public static final String FLD_XML_BLOB = BlobUtil.XML_BLOB_PROPERTY;

  // Data File Info
  public static final String FLD_DATA_FILE_NAME = "ops:Data_File_Info/ops:file_name";
  public static final String FLD_DATA_FILE_CREATION = "ops:Data_File_Info/ops:creation_date_time";
  public static final String FLD_DATA_FILE_REF = "ops:Data_File_Info/ops:file_ref";
  public static final String FLD_DATA_FILE_SIZE = "ops:Data_File_Info/ops:file_size";
  public static final String FLD_DATA_FILE_MD5 = "ops:Data_File_Info/ops:md5_checksum";
  public static final String FLD_DATA_FILE_MIME_TYPE = "ops:Data_File_Info/ops:mime_type";

  // Label Info
  public static final String FLD_LABEL_FILE_NAME = "ops:Label_File_Info/ops:file_name";
  public static final String FLD_LABEL_FILE_CREATION = "ops:Label_File_Info/ops:creation_date_time";
  public static final String FLD_LABEL_FILE_REF = "ops:Label_File_Info/ops:file_ref";
  public static final String FLD_LABEL_FILE_SIZE = "ops:Label_File_Info/ops:file_size";
  public static final String FLD_LABEL_FILE_MD5 = "ops:Label_File_Info/ops:md5_checksum";

  // Tracking_Meta
  public static final String FLD_TRACK_META_ARCHIVE_STATUS = "ops:Tracking_Meta/ops:archive_status";

  // Node Name
  public static final String FLD_NODE_NAME = "ops:Harvest_Info/ops:node_name";

  /**
   * Create Pds4Product object from opensearch key-value field map.
   * 
   * @param lidvid product LIDVID
   * @param fieldMap key-value field map
   * @return new Pds4Product object
   */
  public static Pds4Product createProduct(String lidvid, Map<String, Object> fieldMap,
      boolean isJSON) {
    Pds4Product prod = new Pds4Product();
    prod.setId(lidvid);

    if (fieldMap == null)
      return prod;

    // Pds4 JSON BLOB
    String blob = null;
    String decoded_blob = null;
    try {

      if (isJSON) {
        blob = getVal(fieldMap, FLD_JSON_BLOB);
        decoded_blob = BlobUtil.blobToString(String.valueOf(blob));
      } else {
        int first, last;
        blob = getVal(fieldMap, FLD_XML_BLOB);
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

    ArrayList<String> nodeNames = (ArrayList<String>) fieldMap.get(FLD_NODE_NAME);
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
    ArrayList<String> vals = (ArrayList<String>) fieldMap.get(FLD_LABEL_FILE_NAME);

    if (vals == null)
      return null;

    Pds4MetadataOpsLabelFileInfo item = new Pds4MetadataOpsLabelFileInfo();

    String val = vals.get(0);

    item.setOpsColonFileName(val);

    val = getVal(fieldMap, FLD_LABEL_FILE_CREATION);
    item.setOpsColonCreationDate(val);

    val = getVal(fieldMap, FLD_LABEL_FILE_REF);
    item.setOpsColonFileRef(val);

    val = getVal(fieldMap, FLD_LABEL_FILE_SIZE);
    item.setOpsColonFileSize(val);

    val = getVal(fieldMap, FLD_LABEL_FILE_MD5);
    item.setOpsColonMd5Checksum(val);

    return item;
  }

  @SuppressWarnings("rawtypes")
  private static List<Pds4MetadataOpsDataFile> createDataFiles(Map<String, Object> fieldMap) {
    List<Pds4MetadataOpsDataFile> items = new ArrayList<Pds4MetadataOpsDataFile>();
    ArrayList<String> vals = (ArrayList<String>) fieldMap.get(FLD_DATA_FILE_NAME);
    Pds4MetadataOpsDataFile item = new Pds4MetadataOpsDataFile();

    for (int i = 0; i < ((List) vals).size(); i++) {
      item.setOpsColonFileName((String) ((List) fieldMap.get(FLD_DATA_FILE_CREATION)).get(i));
      item.setOpsColonCreationDate((String) ((List) fieldMap.get(FLD_DATA_FILE_CREATION)).get(i));
      item.opsColonFileRef((String) ((List) fieldMap.get(FLD_DATA_FILE_REF)).get(i));
      item.setOpsColonFileSize((String) ((List) fieldMap.get(FLD_DATA_FILE_SIZE)).get(i));
      item.setOpsColonMd5Checksum((String) ((List) fieldMap.get(FLD_DATA_FILE_MD5)).get(i));
      item.setOpsColonMimeType((String) ((List) fieldMap.get(FLD_DATA_FILE_MIME_TYPE)).get(i));
      items.add(item);
      item = new Pds4MetadataOpsDataFile();
    }
    return items;
  }

  private static Pds4MetadataOpsTrackingMeta createTrackingMeta(Map<String, Object> fieldMap) {
    Pds4MetadataOpsTrackingMeta item = new Pds4MetadataOpsTrackingMeta();

    item.setOpsColonArchiveStatus((String) fieldMap.get(FLD_TRACK_META_ARCHIVE_STATUS));
    return item;
  }
}
