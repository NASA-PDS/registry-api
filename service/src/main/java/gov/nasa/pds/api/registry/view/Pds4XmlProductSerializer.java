package gov.nasa.pds.api.registry.view;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.nasa.pds.model.Pds4Product;


public class Pds4XmlProductSerializer extends AbstractHttpMessageConverter<Pds4Product> {
  static final public String NAMESPACE_PREFIX = "pds_api";
  static final public String NAMESPACE_PREFIX_OPS = "ops";
  static final public String NAMESPACE_URL = "http://pds.nasa.gov/api";
  static final public String NAMESPACE_URL_OPS = "https://pds.nasa.gov/pds4/ops/v1";

  public Pds4XmlProductSerializer() {
    super(new MediaType("application", "vnd.nasa.pds.pds4+xml"));
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return Pds4Product.class.isAssignableFrom(clazz);
  }


  @Override
  protected Pds4Product readInternal(Class<? extends Pds4Product> clazz,
      HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return new Pds4Product();
  }


  @Override
  protected void writeInternal(Pds4Product product, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    try {
      OutputStream outputStream = outputMessage.getBody();
      XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
      outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
      XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);
      writer.setPrefix(Pds4XmlProductSerializer.NAMESPACE_PREFIX,
          Pds4XmlProductSerializer.NAMESPACE_URL);
      writer.setPrefix(NAMESPACE_PREFIX_OPS, NAMESPACE_URL_OPS);
      writer.writeStartElement(Pds4XmlProductSerializer.NAMESPACE_URL, "product");
      writer.writeNamespace(Pds4XmlProductSerializer.NAMESPACE_PREFIX,
          Pds4XmlProductSerializer.NAMESPACE_URL);
      writer.writeNamespace(NAMESPACE_PREFIX_OPS, NAMESPACE_URL_OPS);
      Pds4XmlProductSerializer.serialize(outputStream, writer, new XmlMapper(), product);
      writer.writeEndElement();
      writer.close();
    } catch (ClassCastException e) {
      this.logger.error(
          "For XML serialization, the Product object must be extended as ProductWithXmlLabel: "
              + e.getMessage());
    } catch (Exception e) {
      this.logger.error("Unexpected for no known reason.", e);
    }
  }

  static public void serialize(OutputStream stream, XMLStreamWriter writer, XmlMapper mapper,
      Pds4Product product) throws IOException, XMLStreamException {
    writer.writeStartElement(Pds4XmlProductSerializer.NAMESPACE_URL, "id");
    writer.writeCharacters(product.getId());
    writer.writeEndElement();
    writer.writeStartElement(Pds4XmlProductSerializer.NAMESPACE_URL, "meta");
    writer.writeCharacters("");
    writer.flush();
    stream.write(mapper.writeValueAsString(product.getMetadata()).replace("<Pds4Metadata>", "")
        .replace("</Pds4Metadata>", "").getBytes("UTF-8"));
    stream.flush();
    writer.writeEndElement();
    writer.writeStartElement(Pds4XmlProductSerializer.NAMESPACE_URL, "pds4");
    writer.writeCharacters("");
    writer.flush();
    stream.write(String.valueOf(product.getPds4()).getBytes("UTF8"));
    stream.flush();
    writer.writeEndElement();
    writer.flush();
  }
}

