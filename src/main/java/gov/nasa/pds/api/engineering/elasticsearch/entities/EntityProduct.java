package gov.nasa.pds.api.engineering.elasticsearch.entities;

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
	
	public final String PROCEDURE_INSTRUMENT_TYPE = "Instrument";
	public final String PROCEDURE_INSTRUMENT_HOST_TYPE = "Spacecraft";
	
	public final String PROCEDURE_INSTRUMENT_ROLE = "is_instrument"; 
	public final String PROCEDURE_INSTRUMENT_HOST_ROLE = "is_instrument_host"; 
	
    public final List<String> PROCEDURE_REFERENCE_ROLES = new ArrayList<>(Arrays.asList(
    	    this.PROCEDURE_INSTRUMENT_ROLE,
    	    this.PROCEDURE_INSTRUMENT_HOST_ROLE
    ));
    
    public final String TARGET_ROLE = "data_to_target";
    public final List<String> TARGET_ROLES = new ArrayList<>(Arrays.asList(
    	    this.TARGET_ROLE));
	
	
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

	/* 	// TO TO read it manually
	@JsonProperty("pds/Modification_Detail/pds/modification_date")
    private String modification_date;
    */
	

	@JsonProperty("pds:File/pds:creation_date_time")
    private String creation_date;
	
	
	// TO DO read it from xml
	//@JsonProperty("pds/Internal_Reference/pds/reference_type")
	//private List<String> referenceRoles;
	
	//@JsonProperty("pds/Internal_Reference/pds/lid_reference")
	//private List<String> referenceLidVid;
	
	//@JsonProperty("pds/Observing_System_Component/pds/name")
	//private List<String> observingSystemNames;
	
	//@JsonProperty("pds/Observing_System_Component/pds/type")
	//private List<String> observingSystemTypes;
	
	//@JsonProperty("pds/Target_Identification/pds/type")
	//private String targetType;
	
	//@JsonProperty("pds/Target_Identification/pds/name")
	//private String targetName;
	
	@JsonProperty("vid")
	private String version; 
	
	@JsonProperty("ops:Data_File_Info/ops:file_ref")
	private String pds4FileReference;
	
	@JsonProperty("ops:Label_File_Info/ops:blob")
	private String fileBlob;
	

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
	
	/*
	public List<String> getReferenceRoles() {
		return this.referenceRoles;
	}
	
	
	public String getReferenceLidVid(String role) {
		int i=0;
		for (String t : EntityProduct.emptyIfNull(this.referenceRoles)) {
			if (t.equalsIgnoreCase(role))
				return this.referenceLidVid.get(i);
			i+=1;
		}
		return null;
	}
	
	
	public String getReferenceType(String role) {
		
		if (this.PROCEDURE_REFERENCE_ROLES.contains(role)) {
			if (this.PROCEDURE_INSTRUMENT_ROLE.equalsIgnoreCase(role)){
				return this.PROCEDURE_INSTRUMENT_TYPE;
			}
			else if (this.PROCEDURE_INSTRUMENT_HOST_ROLE.equalsIgnoreCase(role)) {
				return this.PROCEDURE_INSTRUMENT_HOST_TYPE;
			}
			else if (this.TARGET_ROLE.equalsIgnoreCase(role)) {
				return this.targetType;
			}
		}
		
		return null;
	}
	
	public String getReferenceName(String role) {
		
		if (this.PROCEDURE_REFERENCE_ROLES.contains(role)) {
			
			int i=0;
			String type = this.getReferenceType(role);
			
			for (String t : EntityProduct.emptyIfNull(this.observingSystemTypes)) {
				if (t.equalsIgnoreCase(type)){
					return this.observingSystemNames.get(i);
				}
				i+=1;
			}	
			return null;
		}
		else if (this.TARGET_ROLE.equalsIgnoreCase(role)) {
			return this.targetName;
		}
		
		return null;
		
	}
	
	public Reference geReference(String reference_role) {
    	Reference ref = null;
    	String name;
    	if ((name = this.getReferenceName(reference_role)) !=  null) {
	    	ref = new Reference();
	    
			ref.setTitle(name);
			
			String referenceType = this.getReferenceType(reference_role);
			if (referenceType != null) {
				ref.setType(referenceType);
			}
			
			String refUri = this.getReferenceLidVid(reference_role);
			if (refUri != null) {
				ref.setRef(refUri);
			}
			
			// TO DO: add description
    	}
    	
    	return ref;
    }

    */
	
	public String getPDS4FileRef() {
		return this.pds4FileReference;
	}
	
	public String getStartDateTime() {
		return start_date_time;
	}

	public String getStopDateTime() {
		return stop_date_time;
	}

	/* TO DO: reactivate 
	public String getModificationDate() {
		return modification_date;
	}
	*/
	
	public String getCreationDate() {
		return creation_date;
	}
	
	
	public String getVersion() {
		return version;
	}
	
	
	public String getPDS4XML() {
		
		if (this.fileBlob == null) {
    		return "no xml available";
    	}
		
    	Inflater iflr = new Inflater();
    	ByteArrayOutputStream baos = null;
    	
    	byte[] decodedCompressedBytes = Base64.getDecoder().decode(this.fileBlob);
    	
    	iflr.setInput(decodedCompressedBytes);
    	baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
      
        try{
        	
       
            while(!iflr.finished()){
                int size = iflr.inflate(tmp);
                baos.write(tmp, 0, size);
            }
            
            return baos.toString("utf-8");
            
        } catch (Exception ex){
             
        } finally {
            try{
                if(baos != null) baos.close();
            } catch(Exception ex){}
        }
        
        return "no xml available";
    	
    }

	
}
