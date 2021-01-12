package main.java.gov.nasa.pds.api.engineering.serializer;

import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Metadata;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;


public class Pds4XmlProductSerializer extends AbstractHttpMessageConverter<Product> {

		  public Pds4XmlProductSerializer() {
		      super(new MediaType("application", "xml"),
		    		new MediaType("application", "pds4+xml"));
		  }

		  @Override
		  protected boolean supports(Class<?> clazz) {
		      return Product.class.isAssignableFrom(clazz);
		  }

		  @Override
		  protected Product readInternal(Class<? extends Product> clazz, HttpInputMessage inputMessage)
		          throws IOException, HttpMessageNotReadableException {
		     
		      return new Product();
		  }

		  @Override
		  protected void writeInternal(Product product, HttpOutputMessage outputMessage)
		          throws IOException, HttpMessageNotWritableException {
		      try {
		          OutputStream outputStream = outputMessage.getBody();
		          String body = product.getMetadata().getLabelXml();
		          outputStream.write(body.getBytes());
		          outputStream.close();
		      } catch (Exception e) {
		      }
		  }

	
}

