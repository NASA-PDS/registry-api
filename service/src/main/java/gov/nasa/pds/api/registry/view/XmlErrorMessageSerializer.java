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

public class XmlErrorMessageSerializer extends AbstractHttpMessageConverter<ErrorMessage> {
  public XmlErrorMessageSerializer() {
    super(MediaType.APPLICATION_XML, MediaType.TEXT_XML,
        new MediaType("application", "vnd.nasa.pds.pds4+xml"));
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return ErrorMessage.class.isAssignableFrom(clazz);
  }

  @Override
  protected ErrorMessage readInternal(Class<? extends ErrorMessage> clazz,
      HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return new ErrorMessage();
  }

  @Override
  protected void writeInternal(ErrorMessage t, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    outputMessage.getHeaders().setContentType(MediaType.TEXT_XML); // must be before body is fetched
    OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getBody(), "UTF-8");
    try {
      osw.write("<error><request>");
      osw.write(t.getRequest());
      osw.write("</request><message>");
      osw.write(t.getMessage());
      osw.write("</message></error>");
    } finally {
      osw.close();
    }
  }
}
