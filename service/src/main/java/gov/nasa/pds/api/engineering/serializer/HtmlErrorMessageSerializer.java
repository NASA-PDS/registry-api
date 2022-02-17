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

public class HtmlErrorMessageSerializer extends AbstractHttpMessageConverter<ErrorMessage>
{
	public HtmlErrorMessageSerializer() { super (MediaType.TEXT_HTML); }

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
			osw.write("<html><body><h1>Error Message</h1><h2>From Request</h2><p>");
			osw.write(t.getRequest());
			osw.write("</p><h2>Message</h2><p>");
			osw.write(t.getMessage());
			osw.write("</p></body></html>");
		}
		finally { osw.close(); }
	}
}

	
