package gov.nasa.pds.api.registry.search;

import gov.nasa.pds.api.registry.model.BlobUtil;

/**
 * Constants for OpenSearch field names used in queries and responses.
 */
public class OpenSearchFields {

  // Core Product Identifiers
  public static final String LID = "lid";
  public static final String LIDVID = "lidvid";
  public static final String VID = "vid";

  // Product Metadata
  public static final String TITLE = "title";
  public static final String PRODUCT_CLASS = "product_class";

  // Time Coordinates
  public static final String STOP_DATE_TIME = "pds:Time_Coordinates/pds:stop_date_time";
  public static final String START_DATE_TIME = "pds:Time_Coordinates/pds:start_date_time";

  // Data File Info
  public static final String DATA_FILE_NAME = "ops:Data_File_Info/ops:file_name";
  public static final String DATA_FILE_CREATION = "ops:Data_File_Info/ops:creation_date_time";
  public static final String DATA_FILE_REF = "ops:Data_File_Info/ops:file_ref";
  public static final String DATA_FILE_SIZE = "ops:Data_File_Info/ops:file_size";
  public static final String DATA_FILE_MD5 = "ops:Data_File_Info/ops:md5_checksum";
  public static final String DATA_FILE_MIME_TYPE = "ops:Data_File_Info/ops:mime_type";

  // Label Info
  public static final String LABEL_FILE_NAME = "ops:Label_File_Info/ops:file_name";
  public static final String LABEL_FILE_CREATION = "ops:Label_File_Info/ops:creation_date_time";
  public static final String LABEL_FILE_REF = "ops:Label_File_Info/ops:file_ref";
  public static final String LABEL_FILE_SIZE = "ops:Label_File_Info/ops:file_size";
  public static final String LABEL_FILE_MD5 = "ops:Label_File_Info/ops:md5_checksum";

  // Tracking_Meta
  public static final String TRACK_META_ARCHIVE_STATUS = "ops:Tracking_Meta/ops:archive_status";

  // Node Name
  public static final String NODE_NAME = "ops:Harvest_Info/ops:node_name";

  // BLob
  public static final String XML_BLOB = BlobUtil.XML_BLOB_PROPERTY;
  public static final String JSON_BLOB = BlobUtil.JSON_BLOB_PROPERTY;



  // Common Fields
  public static final String ID = "_id";
  public static final String SCORE = "_score";

  // Field Groups
  public static final String[] REQUIRED_FIELDS = {LID, LIDVID, VID, TITLE, PRODUCT_CLASS};

  public static final String[] TIME_FIELDS = {STOP_DATE_TIME, START_DATE_TIME};

  private OpenSearchFields() {
    throw new IllegalStateException("Constants class");
  }
}
