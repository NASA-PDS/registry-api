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

public class CsvPluralSerializer extends AbstractHttpMessageConverter<WyriwygProducts>
{
	public CsvPluralSerializer()
	{
        super(new MediaType("application","csv"), new MediaType("text","csv"));
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
        WyriwygSerializer.writeCSV(t, wr, mapper);
        wr.close();
	}
}
