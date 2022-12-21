package gov.nasa.pds.api.registry.view;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonProductSerializer extends MappingJackson2HttpMessageConverter
{		
	public JsonProductSerializer()
	{
		super();

		List<MediaType> supportMediaTypes = new ArrayList<MediaType>();
		supportMediaTypes.add(MediaType.APPLICATION_JSON);
		this.setSupportedMediaTypes(supportMediaTypes);

		ObjectMapper mapper = new ObjectMapper();
	    mapper.setSerializationInclusion(Include.NON_NULL);
	    this.setObjectMapper(mapper);
	}
}

