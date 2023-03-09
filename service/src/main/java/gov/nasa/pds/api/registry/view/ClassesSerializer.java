package gov.nasa.pds.api.registry.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class ClassesSerializer extends AbstractHttpMessageConverter<List<String>> {

  @Override
  protected boolean supports(Class<?> clazz) {
    return List.class.isAssignableFrom(clazz);
  }

  @Override
  protected List<String> readInternal(Class<? extends List<String>> clazz,
      HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return new ArrayList<String>();
  }

  @Override
  protected void writeInternal(List<String> t, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    List<String> quoted = new ArrayList<String>(t.size());
    OutputStream os = outputMessage.getBody();
    OutputStreamWriter wr = new OutputStreamWriter(os, Charset.defaultCharset());

    for (String name : t)
      quoted.add("\"" + name + "\"");
    wr.write("[");
    wr.write(String.join(",", quoted));
    wr.write("]");
    wr.close();
  }
}
