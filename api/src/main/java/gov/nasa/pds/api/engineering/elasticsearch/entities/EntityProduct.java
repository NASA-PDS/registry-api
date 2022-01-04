package gov.nasa.pds.api.engineering.elasticsearch.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import gov.nasa.pds.api.engineering.controllers.MyCollectionsApiController;
import gov.nasa.pds.model.Reference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class EntityProduct {
	private static final Logger log = LoggerFactory.getLogger(EntityProduct.class);
	public static final String[] JSON_PROPERTIES = new String[] {
			"lidvid", 
			"title",
			"product_class",
			"pds:Time_Coordinates/pds:start_date_time",
			"pds:Time_Coordinates/pds:stop_date_time",
			"pds:Modification_Detail/pds:modification_date",
			"pds:File/pds:creation_date_time",
			"ref_lid_instrument_host",
			"ref_lid_instrument",
			"ref_lid_investigation",
			"ref_lid_target",
			"vid",
			"ops:Label_File_Info/ops:file_ref"
	};
	
	@JsonProperty("lidvid")
	private String lidvid;
	
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("product_class")
	private String productClass;
	
	@JsonProperty("pds:Time_Coordinates/pds:start_date_time")
	private String start_date_time;
	
	@JsonProperty("pds:Time_Coordinates/pds:stop_date_time")
	private String stop_date_time;

	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonProperty("pds:Modification_Detail/pds:modification_date")
    private List<String> modification_date;
    
	@JsonProperty("pds:File/pds:creation_date_time")
    private String creation_date;

	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonProperty("ref_lid_instrument_host")
	private List<String> ref_lid_instrument_host = new ArrayList<String>();

	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonProperty("ref_lid_instrument")
	private List<String> ref_lid_instrument = new ArrayList<String>();
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonProperty("ref_lid_investigation")
	private List<String> ref_lid_investigation = new ArrayList<String>(); 
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonProperty("ref_lid_target")
	private List<String> ref_lid_target = new ArrayList<String>(); 

	@JsonProperty("vid")
	private String version; 
	
	@JsonProperty("ops:Label_File_Info/ops:file_ref")
	private String pds4FileReference;
	
	private Map<String, Object> properties;
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}

	public String getLidVid() {
		return this.lidvid;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getProductClass() {
		return this.productClass;
	}

	public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
	    return iterable == null ? Collections.<T>emptyList() : iterable;
	}
	
	public List<String> getRef_lid_instrument() {
		return ref_lid_instrument;
	}

	public List<String> getRef_lid_instrument_host() {
		return ref_lid_instrument_host;
	}

	public List<String> getRef_lid_investigation() {
		return ref_lid_investigation;
	}

	public List<String> getRef_lid_target() {
		return ref_lid_target;
	}

	
	public String getPDS4FileRef() {
		return this.pds4FileReference;
	}
	
	public String getStartDateTime() {
		return start_date_time;
	}

	public String getStopDateTime() {
		return stop_date_time;
	}


	public List<String> getModificationDate() {
		return modification_date;
	}
	
	public String getCreationDate() {
		return creation_date;
	}
	
	
	public String getVersion() {
		return version;
	}
	
	
	
}
