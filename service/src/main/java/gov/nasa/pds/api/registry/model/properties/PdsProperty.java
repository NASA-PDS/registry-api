package gov.nasa.pds.api.registry.model.properties;

import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import gov.nasa.pds.api.registry.model.exceptions.UnhandledException;

import static gov.nasa.pds.api.registry.model.SearchUtil.jsonPropertyToOpenProperty;
import static gov.nasa.pds.api.registry.model.SearchUtil.openPropertyToJsonProperty;

/**
 * Provides a unified interface for dealing with PDS product properties, which are named differently depending on the
 * context.  The canonical names used by OpenSearch use '/' as a separator, whereas users and the API URL interface
 * replace these with "."
 */
public class PdsProperty {
    private final String value;

    public static final String LIDVID = "lidvid";
    public static final String VID = "vid";
    public static final String TITLE = "title";
    public static final String PRODUCT_CLASS = "product_class";
    public static final String START_DATE_TIME = "pds:Time_Coordinates.pds:start_date_time";
    public static final String STOP_DATE_TIME = "pds:Time_Coordinates.pds:stop_date_time";
    public static final String MODIFICATION_DATE = "pds:Modification_Detail.pds:modification_date";
    public static final String CREATION_DATE_TIME = "pds:File.pds:creation_date_time";
    public static final String REF_LID_INSTRUMENT_HOST = "ref_lid_instrument_host";
    public static final String REF_LID_INSTRUMENT = "ref_lid_instrument";
    public static final String REF_LID_INVESTIGATION = "ref_lid_investigation";
    public static final String REF_LID_TARGET = "ref_lid_target";

 
      // Data File Info
  public static final String DATA_FILE_NAME = "ops:Data_File_Info.ops:file_name";
  public static final String DATA_FILE_CREATION = "ops:Data_File_Info.ops:creation_date_time";
  public static final String DATA_FILE_REF = "ops:Data_File_Info.ops:file_ref";
  public static final String DATA_FILE_SIZE = "ops:Data_File_Info.ops:file_size";
  public static final String DATA_FILE_MD5 = "ops:Data_File_Info.ops:md5_checksum";
  public static final String DATA_FILE_MIME_TYPE = "ops:Data_File_Info.ops:mime_type";

  // Label Info
  public static final String LABEL_FILE_NAME = "ops:Label_File_Info.ops:file_name";
  public static final String LABEL_FILE_CREATION = "ops:Label_File_Info.ops:creation_date_time";
  public static final String LABEL_FILE_REF = "ops:Label_File_Info.ops:file_ref";
  public static final String LABEL_FILE_SIZE = "ops:Label_File_Info.ops:file_size";
  public static final String LABEL_FILE_MD5 = "ops:Label_File_Info.ops:md5_checksum";

  // Tracking_Meta
  public static final String TRACK_META_ARCHIVE_STATUS = "ops:Tracking_Meta.ops:archive_status";

  // Node Name
  public static final String NODE_NAME = "ops:Harvest_Info.ops:node_name";
  public final static String JSON_BLOB = "ops:Label_File_Info.ops:json_blob";
  public final static String XML_BLOB = "ops:Label_File_Info.ops:blob";

    public PdsProperty(String value) {
        this.value = value;
    }

    /**
     * Returns the property, in the format used by OpenSearch
     */
    public static String toOpenPropertyString(String value) {
        // TODO: replace all uses of SearchUtil.jsonPropertyToOpenProperty with PdsProperty, move conversion logic here,
        //  and excise SearchUtil.jsonPropertyToOpenProperty
        return jsonPropertyToOpenProperty(value);
    }

    public String toOpenPropertyString() {
        return toOpenPropertyString(this.value);
    }

    /**
     * Returns the property, in the format used by the API interface (i.e. end-users)
     */
    public static String toJsonPropertyString(String value) {
        // TODO: replace all uses of SearchUtil.openPropertyToJsonProperty with PdsProperty, move conversion logic here,
        //  and excise SearchUtil.openPropertyToJsonProperty

        //TODO: Remove this stopgap once https://github.com/NASA-PDS/registry-api/issues/528 is resolved
        try{
            return openPropertyToJsonProperty(value);
        } catch (UnsupportedSearchProperty err) {
            return value.replace('/', '.');
        }
    }

    public String toJsonPropertyString() {
        return toJsonPropertyString(this.value);
    }


}
