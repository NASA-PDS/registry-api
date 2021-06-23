package gov.nasa.pds.api.engineering.elasticsearch;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.utils.URIBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntitytProductWithBlob;
import gov.nasa.pds.api.engineering.exceptions.UnsupportedElasticSearchProperty;
import gov.nasa.pds.model.Metadata;
import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Reference;

public class ElasticSearchUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchUtil.class);
    
	static public String jsonPropertyToElasticProperty(String jsonProperty) {
		return jsonProperty.replace(".", "/");
		
	}
	
	static public String elasticPropertyToJsonProperty(String elasticProperty) throws UnsupportedElasticSearchProperty {
		   		
			return elasticProperty.replace('/', '.');
	 }
	
	static private void addReference (ArrayList<Reference> to, String ID, URL baseURL)
	{
		Reference reference = new Reference();
		reference.setId(ID);

		String spec = "/products/" + reference.getId();
		
		try {
			
			URIBuilder uriBuilder = new URIBuilder(baseURL.toURI());
			URI uri = uriBuilder.setPath(uriBuilder.getPath() + spec)
			          .build()
			          .normalize();
			
			
			reference.setHref(uri.toString());
			
		} catch (URISyntaxException e) {
			log.warn("Unable to create external URL for reference ");
			e.printStackTrace();
			reference.setHref(spec);
		}
		
		to.add(reference);
	}
	
	
	static private Product addPropertiesFromESEntity(
			Product product, 
			EntityProduct ep,
			URL baseURL
			) {
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
		
		ArrayList<Reference> investigations = new ArrayList<Reference>();
		ArrayList<Reference> observationSystemComponent = new ArrayList<Reference>();
		ArrayList<Reference> targets = new ArrayList<Reference>();
		Metadata meta = new Metadata();
		//String baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

		String version = ep.getVersion();
		if (version != null) {
			meta.setVersion(ep.getVersion());
		}
		
		String creationDateTime = ep.getCreationDate();
		if (creationDateTime != null) {
			meta.setCreationDateTime(ep.getCreationDate());
		}
		
		List<String> updateDateTime = ep.getModificationDate();
		if (updateDateTime != null) {
			meta.setUpdateDateTime(updateDateTime.get(0)); // TODO check which modification time to use when there are more than one
		}
		
		
		String labelUrl = ep.getPDS4FileRef();
		if (labelUrl != null) {		
			meta.setLabelUrl(labelUrl);
		}

		for (String id : ep.getRef_lid_instrument_host()) { ElasticSearchUtil.addReference (observationSystemComponent, id, baseURL); }
		for (String id : ep.getRef_lid_instrument()) { ElasticSearchUtil.addReference (observationSystemComponent, id, baseURL); }
		for (String id : ep.getRef_lid_investigation()) { ElasticSearchUtil.addReference (investigations, id, baseURL); }
		for (String id : ep.getRef_lid_target()) { ElasticSearchUtil.addReference (targets, id, baseURL); }

		product.setInvestigations(investigations);
		product.setMetadata(meta);
		product.setObservingSystemComponents(observationSystemComponent);
		product.setTargets(targets);
		
		return product;
	

	}
	
	static public ProductWithXmlLabel ESentityProductToAPIProduct(EntitytProductWithBlob ep, URL baseURL) {
		ElasticSearchUtil.log.info("convert ES object to API object with XML label");
		ProductWithXmlLabel product = new ProductWithXmlLabel();
		product.setLabelXml(ep.getPDS4XML());
		return (ProductWithXmlLabel)addPropertiesFromESEntity(product, ep, baseURL);
		
	}
	

	static public Product ESentityProductToAPIProduct(EntityProduct ep, URL baseURL) {
		ElasticSearchUtil.log.info("convert ES object to API object without XML label");
		
		Product product = new Product();
		
		return addPropertiesFromESEntity(product, ep, baseURL);
		
				
	}

}
