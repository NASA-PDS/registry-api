package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.model.Pds4Metadata;
import gov.nasa.pds.model.Pds4MetadataOpsDataFiles;
import gov.nasa.pds.model.Pds4MetadataOpsLabelFileInfo;
import gov.nasa.pds.model.Pds4MetadataOpsTrackingMeta;
import gov.nasa.pds.model.Pds4Product;


/**
 * Creates Pds4Product object from opensearch key-value field map.
 * @author karpenko
 */
public class Pds4ProductFactory
{
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
     * @param lidvid product LIDVID
     * @param fieldMap key-value field map
     * @return new Pds4Product object
     */
    public static Pds4Product createProduct(String lidvid, Map<String, Object> fieldMap, boolean isJSON)
    {
        Pds4Product prod = new Pds4Product();
        prod.setId(lidvid);
        
        if(fieldMap == null) return prod;
                
        // Pds4 JSON BLOB
        String decoded_blob = null;
        try
        { 
        	if (isJSON) decoded_blob = BlobUtil.blobToString(String.valueOf(fieldMap.get(FLD_JSON_BLOB)));
        	else
        	{
        		int first,last;
        		decoded_blob = BlobUtil.blobToString(String.valueOf(fieldMap.get(FLD_XML_BLOB)));
        		decoded_blob = decoded_blob.replaceAll("\r", "");
        		first = decoded_blob.indexOf("<?");
        		while (0 <= first)
        		{
        			last = decoded_blob.indexOf("?>", first+2);
        			decoded_blob = decoded_blob.replace (decoded_blob.substring(first, last+2), "");
        			first = decoded_blob.indexOf("<?");
        		}
        		decoded_blob = decoded_blob.strip();
        	}
        }
        catch (Exception e)
        {
        	log.error("Could not convert the given blob", e);
        	decoded_blob = "Could not decode blob. See logs for error details.";
        }
        prod.setPds4(decoded_blob);
        // Metadata
        prod.setMetadata(createMetadata(fieldMap));

        return prod;
    }

    
    private static Pds4Metadata createMetadata(Map<String, Object> fieldMap)
    {
        Pds4Metadata meta = new Pds4Metadata();

        meta.setNodeName((String)fieldMap.get(FLD_NODE_NAME));
        meta.setOpsLabelFileInfo(createLabelFile(fieldMap));
        meta.setOpsDataFiles(createDataFiles(fieldMap));
        meta.setOpsTrackingMeta(createTrackingMeta(fieldMap));
        return meta;
    }
    
    
    private static Pds4MetadataOpsLabelFileInfo createLabelFile(Map<String, Object> fieldMap)
    {
        Object obj = fieldMap.get(FLD_LABEL_FILE_NAME);
        if(obj == null) return null;

        Pds4MetadataOpsLabelFileInfo item = new Pds4MetadataOpsLabelFileInfo();

        String val = (String)obj;
        item.setOpsfileName(val);
        val = (String)fieldMap.get(FLD_LABEL_FILE_CREATION);
        item.setOpscreationDate(val);
        val = (String)fieldMap.get(FLD_LABEL_FILE_REF);
        item.setOpsfileRef(val);
        val = (String)fieldMap.get(FLD_LABEL_FILE_SIZE);
        item.setOpsfileSize(val);
        val = (String)fieldMap.get(FLD_LABEL_FILE_MD5);
        item.setOpsmd5Checksum(val);
        return item;
    }

    @SuppressWarnings("rawtypes")
	private static List<Pds4MetadataOpsDataFiles> createDataFiles(Map<String, Object> fieldMap)
    {
        List<Pds4MetadataOpsDataFiles> items = new ArrayList<Pds4MetadataOpsDataFiles>();
        Object val = fieldMap.get(FLD_DATA_FILE_NAME);
        Pds4MetadataOpsDataFiles item = new Pds4MetadataOpsDataFiles();
        
        if (val instanceof List)
        {
        	for (int i=0 ; i < ((List)val).size() ; i++)
        	{
            	item.setOpsfileName((String)((List)fieldMap.get(FLD_DATA_FILE_CREATION)).get(i));
            	item.setOpscreationDate((String)((List)fieldMap.get(FLD_DATA_FILE_CREATION)).get(i));
            	item.opsfileRef((String)((List)fieldMap.get(FLD_DATA_FILE_REF)).get(i));
            	item.setOpsfileSize((String)((List)fieldMap.get(FLD_DATA_FILE_SIZE)).get(i));
            	item.setOpsmd5Checksum((String)((List)fieldMap.get(FLD_DATA_FILE_MD5)).get(i));
            	item.setOpsmimeType((String)((List)fieldMap.get(FLD_DATA_FILE_MIME_TYPE)).get(i));
            	items.add(item);
            	item = new Pds4MetadataOpsDataFiles();
        	}
        }
        else
        {
        	item.setOpsfileName((String)val);
        	item.setOpscreationDate((String)fieldMap.get(FLD_DATA_FILE_CREATION));
        	item.opsfileRef((String)fieldMap.get(FLD_DATA_FILE_REF));
        	item.setOpsfileSize((String)fieldMap.get(FLD_DATA_FILE_SIZE));
        	item.setOpsmd5Checksum((String)fieldMap.get(FLD_DATA_FILE_MD5));
        	item.setOpsmimeType((String)fieldMap.get(FLD_DATA_FILE_MIME_TYPE));
        	items.add(item);
        }
        return items;
    }
    
    private static Pds4MetadataOpsTrackingMeta createTrackingMeta (Map<String, Object> fieldMap)
    {
    	Pds4MetadataOpsTrackingMeta item = new Pds4MetadataOpsTrackingMeta();

    	item.setOpsarchiveStatus((String)fieldMap.get(FLD_TRACK_META_ARCHIVE_STATUS));
    	return item;
    }
}
