package gov.nasa.pds.api.model.xml;

import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import gov.nasa.pds.api.engineering.serializer.XmlProductSerializer;
import gov.nasa.pds.model.PropertyArrayValues;


@XmlRootElement(namespace="http://pds.nasa.gov/api", name = "values")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLMashallableProperyValue extends PropertyArrayValues {
	
	private static final Logger log = LoggerFactory.getLogger(XMLMashallableProperyValue.class);

	@JacksonXmlProperty(namespace="http://pds.nasa.gov/api", localName = "value")
	@Valid
    private List<String> list = this;
 
    public List<String> getList() {
    	XMLMashallableProperyValue.log.info("found list");
        return (List<String>)this;
    }
 
 

}
