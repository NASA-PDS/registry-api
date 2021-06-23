package gov.nasa.pds.api.engineering.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.model.Product;



public class Pds4JsonProductSerializer extends MappingJackson2HttpMessageConverter {
	
	private static final Logger log = LoggerFactory.getLogger(Pds4JsonProductSerializer.class);
	
	public Pds4JsonProductSerializer() {
		
		super();
		
		List<MediaType> supportMediaTypes = new ArrayList<MediaType>();
		supportMediaTypes.add(MediaType.APPLICATION_JSON);
		this.setSupportedMediaTypes(supportMediaTypes);
		
		ObjectMapper mapper = new ObjectMapper();
	    mapper.setSerializationInclusion(Include.NON_NULL);
	    this.setObjectMapper(mapper);
	     
	}
	

}
