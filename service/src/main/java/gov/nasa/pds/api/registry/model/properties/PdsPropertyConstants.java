package gov.nasa.pds.api.registry.model.properties;

public class PdsPropertyConstants {

  public static final PdsProperty LIDVID = new PdsProperty("lidvid");
  public static final PdsProperty VID = new PdsProperty("vid");
  public static final PdsProperty TITLE = new PdsProperty("title");
  public static final PdsProperty PRODUCT_CLASS = new PdsProperty("product_class");
  public static final PdsProperty START_DATE_TIME =
      new PdsProperty("pds:Time_Coordinates.pds:start_date_time");
  public static final PdsProperty STOP_DATE_TIME =
      new PdsProperty("pds:Time_Coordinates.pds:stop_date_time");
  public static final PdsProperty MODIFICATION_DATE =
      new PdsProperty("pds:Modification_Detail.pds:modification_date");
  public static final PdsProperty CREATION_DATE_TIME =
      new PdsProperty("pds:File.pds:creation_date_time");
  public static final PdsProperty REF_LID_INSTRUMENT_HOST =
      new PdsProperty("ref_lid_instrument_host");
  public static final PdsProperty REF_LID_INSTRUMENT = new PdsProperty("ref_lid_instrument");
  public static final PdsProperty REF_LID_INVESTIGATION = new PdsProperty("ref_lid_investigation");
  public static final PdsProperty REF_LID_TARGET = new PdsProperty("ref_lid_target");


  // Data File Info
  public static class DATA_FILE {
    private static final String OBJECT_PREFIX = "ops:Data_File_Info.";

    public static final PdsProperty NAME = new PdsProperty(OBJECT_PREFIX + "ops:file_name");
    public static final PdsProperty CREATION =
        new PdsProperty(OBJECT_PREFIX + "ops:creation_date_time");
    public static final PdsProperty REF = new PdsProperty(OBJECT_PREFIX + "ops:file_ref");
    public static final PdsProperty SIZE = new PdsProperty(OBJECT_PREFIX + "ops:file_size");
    public static final PdsProperty MD5 = new PdsProperty(OBJECT_PREFIX + "ops:md5_checksum");
    public static final PdsProperty MIME_TYPE = new PdsProperty(OBJECT_PREFIX + "ops:mime_type");

    private DATA_FILE() {
      throw new AssertionError("Cannot instantiate DATA_FILE");
    }
  }

  public static class LABEL_FILE {
    private static final String OBJECT_PREFIX = "ops:Label_File_Info.";

    public static final PdsProperty NAME = new PdsProperty(OBJECT_PREFIX + "ops:file_name");
    public static final PdsProperty CREATION =
        new PdsProperty(OBJECT_PREFIX + "ops:creation_date_time");
    public static final PdsProperty REF = new PdsProperty(OBJECT_PREFIX + "ops:file_ref");
    public static final PdsProperty SIZE = new PdsProperty(OBJECT_PREFIX + "ops:file_size");
    public static final PdsProperty MD5 = new PdsProperty(OBJECT_PREFIX + "ops:md5_checksum");

    private LABEL_FILE() {
      throw new AssertionError("Cannot instantiate LABEL_FILE");
    }
  }


  // Tracking_Meta
  public static final PdsProperty TRACK_META_ARCHIVE_STATUS =
      new PdsProperty("ops:Tracking_Meta.ops:archive_status");

  // Node Name
  public static final PdsProperty NODE_NAME = new PdsProperty("ops:Harvest_Info.ops:node_name");
  public static final PdsProperty JSON_BLOB = new PdsProperty("ops:Label_File_Info.ops:json_blob");
  public static final PdsProperty XML_BLOB = new PdsProperty("ops:Label_File_Info.ops:blob");

  private PdsPropertyConstants() {
    throw new AssertionError("Cannot instantiate PdsPropertyConstants");
  }


}
