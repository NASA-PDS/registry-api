package gov.nasa.pds.api.registry.serializer;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import gov.nasa.pds.model.ErrorMessage;

public class JsonErrorMessageSerializer extends AbstractHttpMessageConverter<ErrorMessage>
{
	public JsonErrorMessageSerializer(){ 
		super(MediaType.APPLICATION_JSON,
			new MediaType("application","kvp+json"),
			new MediaType("application", "vnd.nasa.pds.pds4+json"),
			MediaType.ALL); 
	}


	@Override
	protected boolean supports(Class<?> clazz) { return ErrorMessage.class.isAssignableFrom(clazz); }

	@Override
	protected ErrorMessage readInternal(Class<? extends ErrorMessage> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException { return new ErrorMessage(); }

	@Override
	protected void writeInternal(ErrorMessage t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException
	{
        OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getBody(), "UTF-8");
        try
        {
        	osw.write("{\"request\":\"");
        	osw.write(t.getRequest());
        	osw.write("\",\"message\":\"");
        	osw.write(t.getMessage());
        	osw.write("\"}");
        }
        finally { osw.close(); }
	}
}
