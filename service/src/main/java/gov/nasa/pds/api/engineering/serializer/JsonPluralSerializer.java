package gov.nasa.pds.api.engineering.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.model.WyriwygProducts;

public class JsonPluralSerializer extends AbstractHttpMessageConverter<WyriwygProducts>
{
	public JsonPluralSerializer()
	{
        super(new MediaType("application","kvp+json"));
	}

	@Override
	protected boolean supports(Class<?> clazz) { return WyriwygProducts.class.isAssignableFrom(clazz); }

	@Override
	protected WyriwygProducts readInternal(Class<? extends WyriwygProducts> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException { return new WyriwygProducts(); }

	@Override
	protected void writeInternal(WyriwygProducts t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException
	{		
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        
        OutputStream os = outputMessage.getBody();
        OutputStreamWriter wr = new OutputStreamWriter(os);
        WyriwygSerializer.writeJSON(t, wr, mapper);
        wr.close();
	}
}
