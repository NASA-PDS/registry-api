package gov.nasa.pds.api.engineering.serializer;

import gov.nasa.pds.model.Pds4Product;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;


public class Pds4XmlProductSerializer extends AbstractHttpMessageConverter<Pds4Product> {

		  public Pds4XmlProductSerializer() {
		      super(new MediaType("application", "pds4+xml"));
		  }

		  @Override
		  protected boolean supports(Class<?> clazz) {
		      return Pds4Product.class.isAssignableFrom(clazz);
		  }

		  
		  @Override
		  protected Pds4Product readInternal(Class<? extends Pds4Product> clazz, HttpInputMessage inputMessage)
		          throws IOException, HttpMessageNotReadableException {
		     
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
		          (new XmlMapper()).writeValue (writer, product);
		      } catch (ClassCastException e) {
		    	  this.logger.error("For XML serialization, the Product object must be extended as ProductWithXmlLabel: " + e.getMessage());
		      }
		        catch (Exception e) {
		      }
		  }

	
}

