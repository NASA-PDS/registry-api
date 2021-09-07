package gov.nasa.pds.api.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.nasa.pds.model.Product;
import io.swagger.annotations.ApiModelProperty;


@XmlRootElement( name = "product")
public class ProductWithXmlLabel extends Product {
	
	 @JsonIgnore
	 private String labelXml = null;
	
	 public ProductWithXmlLabel labelXml(String labelXml) {
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
