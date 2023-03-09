package gov.nasa.pds.api.registry.view;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import gov.nasa.pds.model.ErrorMessage;

public class CsvErrorMessageSerializer extends AbstractHttpMessageConverter<ErrorMessage> {
	public CsvErrorMessageSerializer() {
		super(new MediaType("application", "csv"), new MediaType("text", "csv"));
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return ErrorMessage.class.isAssignableFrom(clazz);
	}

	@Override
	protected ErrorMessage readInternal(Class<? extends ErrorMessage> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return new ErrorMessage();
	}

	@Override
	protected void writeInternal(ErrorMessage t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getBody(), "UTF-8");
		try {
			osw.write("request,message\n");
			osw.write('"');
			osw.write(t.getRequest());
			osw.write("\",\"");
			osw.write(t.getMessage());
			osw.write('"');
			osw.write('\n');
		} finally {
			osw.close();
		}
	}
}
