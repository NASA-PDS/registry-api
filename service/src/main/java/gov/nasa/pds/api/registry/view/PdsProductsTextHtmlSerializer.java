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

import gov.nasa.pds.model.PdsProducts;

public class PdsProductsTextHtmlSerializer extends AbstractHttpMessageConverter<PdsProducts> {
  public PdsProductsTextHtmlSerializer() {
    super(MediaType.TEXT_HTML);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return PdsProducts.class.isAssignableFrom(clazz);
  }

  @Override
  protected PdsProducts readInternal(Class<? extends PdsProducts> clazz,
      HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return new PdsProducts();
  }

  @Override
  protected void writeInternal(PdsProducts t, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    ObjectMapper mapper = new ObjectMapper();
    outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON); // must be before body is fetched
    OutputStream os = outputMessage.getBody();
    OutputStreamWriter wr = new OutputStreamWriter(os, Charset.defaultCharset());
    mapper.setSerializationInclusion(Include.NON_NULL);
    Utilities.fix(t.getSummary());
    wr.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t));
    wr.close();
  }
}
