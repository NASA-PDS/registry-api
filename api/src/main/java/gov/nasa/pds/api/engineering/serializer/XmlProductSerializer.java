package gov.nasa.pds.api.engineering.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.transform.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.api.model.xml.XMLMashallableProperyValue;
import gov.nasa.pds.model.PropertyArrayValues;
import gov.nasa.pds.model.Product;

public class XmlProductSerializer extends Jaxb2RootElementHttpMessageConverter {
	
	private static final Logger log = LoggerFactory.getLogger(XmlProductSerializer.class);

	
	public XmlProductSerializer() {
	      super();
	      
	      List<MediaType> supportMediaTypes = new ArrayList<MediaType>();
			supportMediaTypes.add(MediaType.APPLICATION_XML);
			this.setSupportedMediaTypes(supportMediaTypes);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		
	      return ProductWithXmlLabel.class.isAssignableFrom(clazz) 
	    		  || Product.class.isAssignableFrom(clazz);
	 }
	
	@Override
	public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
		log.info("how did we select that thing " + clazz.getCanonicalName());
		return super.canRead(clazz, mediaType) 
				&& this.supports(clazz);
	}
	
	
	
	@Override
	protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
		
		log.info(ClassUtils.getUserClass(o).getName());
		
		if (Product.class.isAssignableFrom(o.getClass())) {
			//HashMap<String, XMLMashallableProperyValue> props = (HashMap<String, XMLMashallableProperyValue>)(HashMap<String, ?>)((Product)o).getProperties();
			for (Entry<String, ?> e:  ((Product)o).getProperties().entrySet()) {
				XmlProductSerializer.log.info("Class in hashmap value is" + e.getValue().getClass().getCanonicalName());
			}
		}
		
		if (ProductWithXmlLabel.class.isAssignableFrom(o.getClass())) {
			o = ((ProductWithXmlLabel)o).labelXml(null);
		}
		
		
   		
		super.writeToResult(o, headers, result);
	}

}
