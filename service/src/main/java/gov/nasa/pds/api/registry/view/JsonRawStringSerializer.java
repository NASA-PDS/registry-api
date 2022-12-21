package gov.nasa.pds.api.registry.view;

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


public class JsonRawStringSerializer  extends AbstractHttpMessageConverter<String>
{
	
	public JsonRawStringSerializer()
	{
        super(new MediaType("application","json"));
	}

	@Override
	protected boolean supports(Class<?> clazz) { return String.class.isAssignableFrom(clazz); }

	@Override
	protected String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException { return new String(); }

	@Override
	protected void writeInternal(String t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException
	{		
		
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        
        OutputStream os = outputMessage.getBody();
        OutputStreamWriter wr = new OutputStreamWriter(os);
        wr.write(t); 
        wr.close();
	}

}
