package gov.nasa.pds.api.model.xml;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.nasa.pds.model.Pds4Product;
import io.swagger.annotations.ApiModelProperty;

public class Pds4ProductWithXmlLabel extends Pds4Product {
	
	 @JsonIgnore
	 private String labelXml = null;
	
	 public Pds4ProductWithXmlLabel labelXml(String labelXml) {
		    this.labelXml = labelXml;
		    return this;
	 }
	 
	 /**
	   * Get labelXml
	   * @return labelXml
	 **/
	 @ApiModelProperty(value = "")	  
	 public String getLabelXml() {
		    return labelXml;
	 }

	  public void setLabelXml(String labelXml) {
	    this.labelXml = labelXml;
	  }

}
