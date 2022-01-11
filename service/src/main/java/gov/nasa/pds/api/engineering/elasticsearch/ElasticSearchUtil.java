package gov.nasa.pds.api.engineering.elasticsearch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntitytProductWithBlob;
import gov.nasa.pds.api.engineering.exceptions.UnsupportedElasticSearchProperty;
import gov.nasa.pds.model.Metadata;
import gov.nasa.pds.model.PdsProduct;
import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.model.Reference;
import gov.nasa.pds.model.Summary;

public class ElasticSearchUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchUtil.class);
    
	static public String jsonPropertyToElasticProperty(String jsonProperty)
	{ return jsonProperty.replace(".", "/"); }
	
	static public String[] jsonPropertyToElasticProperty(String[] jsonProperties)
	{ 
		if (jsonProperties != null && jsonProperties.length > 0)
		{
			for (int i=0 ; i < jsonProperties.length ; i++)
			{ jsonProperties[i] = jsonPropertyToElasticProperty(jsonProperties[i]); }
		}
		return jsonProperties;
	}

	static public List<String> jsonPropertyToElasticProperty(List<String> jsonProperties)
	{
		if (jsonProperties != null && jsonProperties.size() > 0)
		{
			for (int i=0 ; i < jsonProperties.size() ; i++)
			{ jsonProperties.set(i, jsonPropertyToElasticProperty(jsonProperties.get(i))); }
		}
		return jsonProperties;
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
	
	
	static private PdsProduct addPropertiesFromESEntity(
			PdsProduct product, 
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
		
		List<String> creationDateTime = ep.getCreationDate();
		if (creationDateTime != null && !creationDateTime.isEmpty()) {
			meta.setCreationDateTime(creationDateTime.get(0));
		}
		
		List<String> updateDateTime = ep.getModificationDate();
		if (updateDateTime != null && !updateDateTime.isEmpty()) {
		    // TODO check which modification time to use when there are more than one
			meta.setUpdateDateTime(updateDateTime.get(0));
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
		log.debug("convert ES object to API object with XML label");
		ProductWithXmlLabel product = new ProductWithXmlLabel();
		product.setLabelXml(ep.getPDS4XML());
		return (ProductWithXmlLabel)addPropertiesFromESEntity(product, ep, baseURL);
	}
	

	static public PdsProduct ESentityProductToAPIProduct(EntityProduct ep, URL baseURL) {
		log.debug("convert ES object to API object without XML label");
		
		PdsProduct product = new PdsProduct();
		
		return addPropertiesFromESEntity(product, ep, baseURL);
	}
	
	static public List<Map<String,Object>> collate (RestHighLevelClient client, SearchRequest request, Summary summary) throws IOException
	{
    	List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
    	SearchHits findings = client.search(request, RequestOptions.DEFAULT).getHits(); 
    	
    	summary.hits((int)findings.getTotalHits().value);
    	for (SearchHit hit : findings)
    	{
    		results.add(hit.getSourceAsMap());
    	}
    	return results;
	}
}
