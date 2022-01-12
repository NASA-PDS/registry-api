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

import gov.nasa.pds.model.PdsProducts;

public class PdsProductsTextHtmlSerializer extends AbstractHttpMessageConverter<PdsProducts>
{
	public PdsProductsTextHtmlSerializer()
	{
        super(MediaType.TEXT_HTML);
	}

	@Override
	protected boolean supports(Class<?> clazz) { return PdsProducts.class.isAssignableFrom(clazz); }

	@Override
	protected PdsProducts readInternal(Class<? extends PdsProducts> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException { return new PdsProducts(); }

	@Override
	protected void writeInternal(PdsProducts t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException
	{
        ObjectMapper mapper = new ObjectMapper();
        OutputStream os = outputMessage.getBody();
        OutputStreamWriter wr = new OutputStreamWriter(os);
        mapper.setSerializationInclusion(Include.NON_NULL);
        wr.write("<html><body><h1>JSON as text</h1><p><pre>");
        wr.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t));
        wr.write("</pre></p></body></html>");
        wr.close();
	}
}
