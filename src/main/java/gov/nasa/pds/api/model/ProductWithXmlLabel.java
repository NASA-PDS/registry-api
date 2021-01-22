package gov.nasa.pds.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import gov.nasa.pds.model.Product;
import io.swagger.annotations.ApiModelProperty;

public class ProductWithXmlLabel extends Product {
	
	 @JsonIgnore
	 private String labelXml = null;
	
	 public Product labelXml(String labelXml) {
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
