package gov.nasa.pds.api.engineering.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnectionImpl;
import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

public class Pds4XmlProductsSerializer  extends AbstractHttpMessageConverter<Products> {
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
	      return Products.class.isAssignableFrom(clazz);
	  }

	  @Override
	  protected Products readInternal(Class<? extends Products> clazz, HttpInputMessage inputMessage)
	          throws IOException, HttpMessageNotReadableException {
	     // dummy method never used
	      return new Products();
	  }

	
	  
	  @Override
	  protected void writeInternal(Products products, HttpOutputMessage outputMessage)
	          throws IOException, HttpMessageNotWritableException {
	      try {
	          OutputStream outputStream = outputMessage.getBody();
	          
	          XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
	          //outputFactory.setProperty(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE, true);
	          outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
	          XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);
	        
	          //writer.setDefaultNamespace("http://pds.nasa.gov/pds4/pds/v1");
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
	          for (Product product : products.getData()) {
	        	  writer.writeStartElement(Pds4XmlProductsSerializer.NAMESPACE_URL, "product");
	        	  
	        	  String productBody = ((ProductWithXmlLabel)product).getLabelXml();
	        	  productBody = productBody.substring(productBody.lastIndexOf("?>")+2);
	        	  
	        	  writer.writeCharacters("");
	        	  writer.flush();
		          OutputStreamWriter osw = new OutputStreamWriter(outputStream);
		          osw.write(productBody);
		          osw.flush();
		          
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
