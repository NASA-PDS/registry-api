package gov.nasa.pds.api.registry.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.model.PdsProduct;

public class PdsProductTextHtmlSerializer extends AbstractHttpMessageConverter<PdsProduct> {
  public PdsProductTextHtmlSerializer() {
    super(MediaType.TEXT_HTML);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return PdsProduct.class.isAssignableFrom(clazz);
  }

  @Override
  protected PdsProduct readInternal(Class<? extends PdsProduct> clazz,
      HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return new PdsProduct();
  }

  @Override
  protected void writeInternal(PdsProduct t, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    ObjectMapper mapper = new ObjectMapper();
    outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON); // must be before body is fetched
    OutputStream os = outputMessage.getBody();
    OutputStreamWriter wr = new OutputStreamWriter(os, Charset.defaultCharset());
    mapper.setSerializationInclusion(Include.NON_NULL);
    wr.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t));
    wr.close();
  }
}
