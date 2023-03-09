package gov.nasa.pds.api.registry.view;

import gov.nasa.pds.model.PdsProduct;

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

import com.ctc.wstx.api.WstxInputProperties;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class PdsProductXMLSerializer extends AbstractHttpMessageConverter<PdsProduct> {
  static final public String NAMESPACE_PREFIX = "pds_api";
  static final public String NAMESPACE_URL = "http://pds.nasa.gov/api";

  public PdsProductXMLSerializer() {
    super(MediaType.APPLICATION_XML, MediaType.TEXT_XML);
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
  protected void writeInternal(PdsProduct product, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    try {
      OutputStream outputStream = outputMessage.getBody();
      XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
      outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
      outputFactory.setProperty(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE, true);
      XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);
      new XmlMapper().writeValue(writer, product);
    } catch (ClassCastException e) {
      this.logger.error(
          "For XML serialization, the Product object must be extended as ProductWithXmlLabel: "
              + e.getMessage());
    } catch (XMLStreamException e) {
      this.logger.error("XML serialization problem: " + e.getMessage());
    }
  }
}
