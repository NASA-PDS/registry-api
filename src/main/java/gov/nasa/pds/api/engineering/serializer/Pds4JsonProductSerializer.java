package gov.nasa.pds.api.engineering.serializer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

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
