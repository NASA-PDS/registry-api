package gov.nasa.pds.api.engineering.serializer;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import gov.nasa.pds.model.Pds4Product;
import gov.nasa.pds.model.Pds4Products;
import gov.nasa.pds.model.Summary;

public class Pds4XmlProductsSerializer  extends AbstractHttpMessageConverter<Pds4Products> {
	/***
	 * OBSOLETE since we don't want to use the label in blob anymore to provide the pds4 original label for a list of products
	 * 
	 */
	
   	  private static final Logger log = LoggerFactory.getLogger(Pds4XmlProductsSerializer.class);

	
	  static final private String NAMESPACE_PREFIX = "pds_api";
	  static final private String NAMESPACE_URL = "http://pds.nasa.gov/api";
	
	  public Pds4XmlProductsSerializer() {
	      super(new MediaType("application", "pds4+xml"));
	  }

	  @Override
	  protected boolean supports(Class<?> clazz) {
	      return Pds4Products.class.isAssignableFrom(clazz);
	  }

	  @Override
	  protected Pds4Products readInternal(Class<? extends Pds4Products> clazz, HttpInputMessage inputMessage)
	          throws IOException, HttpMessageNotReadableException {
	     // dummy method never used
	      return new Pds4Products();
	  }

	
	  
	  @Override
	  protected void writeInternal(Pds4Products products, HttpOutputMessage outputMessage)
	          throws IOException, HttpMessageNotWritableException {
	      try {
	          OutputStream outputStream = outputMessage.getBody();
	          
	          XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
	          outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
	          XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);
	        
	          writer.setPrefix(Pds4XmlProductsSerializer.NAMESPACE_PREFIX, 
		            		  Pds4XmlProductsSerializer.NAMESPACE_URL);
	          writer.writeStartElement(Pds4XmlProductsSerializer.NAMESPACE_URL, "products");
	          writer.writeDefaultNamespace("http://pds.nasa.gov/pds4/pds/v1");
	          writer.writeNamespace(Pds4XmlProductsSerializer.NAMESPACE_PREFIX, 
            		  Pds4XmlProductsSerializer.NAMESPACE_URL);
	       	          
	          Summary summary = products.getSummary();
	          XmlMapper xmlMapper = new XmlMapper();
	          xmlMapper.writeValue(writer, summary);
	       
	          
	          writer.writeStartElement(Pds4XmlProductsSerializer.NAMESPACE_URL, "data");
	          log.error("                     **************************");
	          log.error("                     *********      length: " + String.valueOf(products.getData().size()));
	          log.error("                     **************************");
	          for (Pds4Product product : products.getData()) {
	        	  writer.writeStartElement(Pds4XmlProductsSerializer.NAMESPACE_URL, "product");
		          xmlMapper.writeValue (writer, product);
		          writer.writeEndElement();
	          }
	          writer.writeEndElement(); // data
	          writer.writeEndElement(); // products
	          writer.close();     
	          outputStream.close();
	      } catch (ClassCastException e) {
	    	  this.logger.error("For XML serialization, Product object must be extended to ProductWithXmlLabel: " + e.getMessage());
	      } catch (Exception e) {
	    	  
	    	  Pds4XmlProductsSerializer.log.info("error while serializing products in xml " + e.getMessage());
	      }
	  }

}
