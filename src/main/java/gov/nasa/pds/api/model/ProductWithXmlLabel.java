package gov.nasa.pds.api.model;

import java.util.Map;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import gov.nasa.pds.model.Product;
import io.swagger.annotations.ApiModelProperty;

public class ProductWithXmlLabel extends Product {
	
	 @JsonIgnore
	 private String labelXml = null;
	 
	 @JsonProperty("properties")
	 @JacksonXmlProperty(localName = "properties")
	 @Valid
	 @JsonInclude(JsonInclude.Include.ALWAYS)
	 private Map<String, Object> properties = null;
	
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
