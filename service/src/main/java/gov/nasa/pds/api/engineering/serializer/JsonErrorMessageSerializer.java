package gov.nasa.pds.api.engineering.serializer;

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
	public JsonErrorMessageSerializer()
	{ super(MediaType.APPLICATION_JSON,
			new MediaType("application","kvp+json"),
			new MediaType("application", "pds4+json")); }

	@Override
	protected boolean supports(Class<?> clazz) { return ErrorMessage.class.isAssignableFrom(clazz); }

	@Override
	protected ErrorMessage readInternal(Class<? extends ErrorMessage> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException { return new ErrorMessage(); }

	@Override
	protected void writeInternal(ErrorMessage t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException
	{
        OutputStreamWriter wr = new OutputStreamWriter(outputMessage.getBody());
        wr.write("{\"request\":\"");
        wr.write(t.getRequest());
        wr.write("\",\"message\":\"");
        wr.write(t.getMessage());
        wr.write("\"}");
        wr.close();
	}
}
