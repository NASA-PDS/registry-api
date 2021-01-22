package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.engineering.exceptions.UnsupportedElasticSearchProperty;
import gov.nasa.pds.model.Metadata;
import gov.nasa.pds.api.model.ProductWithXmlLabel;

public class ElasticSearchUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchUtil.class);
    
	static public String jsonPropertyToElasticProperty(String jsonProperty) {
		return jsonProperty.replace(":", "/");
	}
	
	static public String elasticPropertyToJsonProperty(String elasticProperty) throws UnsupportedElasticSearchProperty {
		   
		   String elasticPropertyTokens[] = elasticProperty.split("/");
		   ArrayList<String> jsonPropertyTokens = new ArrayList<String>();
		   
		   if (elasticPropertyTokens.length%2 == 0) {
			   int propertyNamespaceNameCouplesNumber = elasticPropertyTokens.length/2;
			   
			   
		   	   for (int i=0 ; i<propertyNamespaceNameCouplesNumber ; i++) {
		   		jsonPropertyTokens.add(elasticPropertyTokens[2*i] + ":" + elasticPropertyTokens[2*i+1]);
		   		   		
		   	   }
		   	   
		   	   return String.join("/", jsonPropertyTokens);
		   	   
		   }
		   else if  (elasticPropertyTokens.length == 1) {
			   
			   return elasticProperty;
			   
		   }
		   else {
		   
			   throw new UnsupportedElasticSearchProperty("Invalid elasticSearch data property " + elasticProperty);
		   }
		   
	   }
	
	

	static public Map<String, Object> elasticHashMapToJsonHashMap(Map<String, Object> sourceAsMap){
			 Map<String, Object> sourceAsMapJsonProperties = new HashMap<String, Object>();
			 Iterator<Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();
		     while (iterator.hasNext()) {
		    	 try {
	  	    	 Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
	  	    	 sourceAsMapJsonProperties.put(elasticPropertyToJsonProperty(entry.getKey()),
	                       entry.getValue());
		    	 } catch (UnsupportedElasticSearchProperty e) {
		    		 ElasticSearchUtil.log.warn(e.getMessage());
		    	 }
		     }
		     
		     return sourceAsMapJsonProperties;
	   }



	static public ProductWithXmlLabel ESentityProductToAPIProduct(EntityProduct ep) {
		ProductWithXmlLabel product = new ProductWithXmlLabel();
		product.setId(ep.getLidVid());
		product.setType(ep.getProductClass());
		
		String title = ep.getTitle();
		if (title != null) {
			product.setTitle(ep.getTitle());
		}
		
		String startDateTime = ep.getStartDateTime();
		if (startDateTime != null) {
			product.setStartDateTime(startDateTime);
		}
		
		String stopDateTime = ep.getStopDateTime();
		if (stopDateTime != null) {
			product.setStopDateTime(ep.getStopDateTime());
		}
		
		/*
		for (String reference_role: ep.PROCEDURE_REFERENCE_ROLES) {
			Reference observingSystemComponentRef = ep.geReference(reference_role);
			if (observingSystemComponentRef != null) {
				product.addObservingSystemComponentsItem(observingSystemComponentRef);
			}
			
		}
		
		for (String reference_role : ep.TARGET_ROLES) {
			Reference targetReference = ep.geReference(reference_role);
			if (targetReference != null) {
				product.addTargetsItem(targetReference);
			}
		}
		*/
		
		Metadata meta = new Metadata();
		
		
		String version = ep.getVersion();
		if (version != null) {
			meta.setVersion(ep.getVersion());
		}
		
		String creationDateTime = ep.getCreationDate();
		if (creationDateTime != null) {
			meta.setCreationDateTime(ep.getCreationDate());
		}
		
		String updateDateTime = ep.getModificationDate();
		if (updateDateTime != null) {
			meta.setUpdateDateTime(updateDateTime);
		}
		
		String labelUrl = ep.getPDS4FileRef();
		if (labelUrl != null) {		
			meta.setLabelUrl(labelUrl);
		}
		
		product.setLabelXml(ep.getPDS4XML()); // value is injected to be used as-is in XML serialization
		
		product.setMetadata(meta);
	
		return product;
	
		
	}

}
