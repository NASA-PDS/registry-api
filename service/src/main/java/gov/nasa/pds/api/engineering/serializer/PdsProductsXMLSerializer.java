package gov.nasa.pds.api.engineering.serializer;

import gov.nasa.pds.model.PdsProducts;

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


public class PdsProductsXMLSerializer extends AbstractHttpMessageConverter<PdsProducts> {

		  public PdsProductsXMLSerializer() {
		      super(MediaType.APPLICATION_XML, MediaType.TEXT_XML);
		  }

		  @Override
		  protected boolean supports(Class<?> clazz) {
		      return PdsProducts.class.isAssignableFrom(clazz);
		  }

		  
		  @Override
		  protected PdsProducts readInternal(Class<? extends PdsProducts> clazz, HttpInputMessage inputMessage)
		          throws IOException, HttpMessageNotReadableException {
		     
		      return new PdsProducts();
		  }
		  

		  @Override
		  protected void writeInternal(PdsProducts product, HttpOutputMessage outputMessage)
		          throws IOException, HttpMessageNotWritableException
		  {
			  try
			  {
				  OutputStream outputStream = outputMessage.getBody();
		          XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		          outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
		          XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);
		          new XmlMapper().writeValue (writer, product);
		      }
			  catch (ClassCastException e)
			  {
		    	  this.logger.error("For XML serialization, the Product object must be extended as ProductWithXmlLabel: " + e.getMessage());
		      }
			  catch (XMLStreamException e) 
			  {
				this.logger.error("XML serialization problem: " + e.getMessage());
		      }
		  }
}

