package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;

public class ProductsBusinessObject {

    private static final Logger log = LoggerFactory.getLogger(ProductsBusinessObject.class);
	 
	private ElasticSearchRegistryConnection elasticSearchConnection;
	private ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder;
	private ObjectMapper objectMapper;
	
	static final String LIDVID_SEPARATOR = "::";
	
	public ProductsBusinessObject(ElasticSearchRegistryConnection esRegistryConnection) {
		this.elasticSearchConnection = esRegistryConnection;
		
	  	this.searchRequestBuilder = new ElasticSearchRegistrySearchRequestBuilder(
    			this.elasticSearchConnection.getRegistryIndex(),
    			this.elasticSearchConnection.getRegistryRefIndex(),
    			this.elasticSearchConnection.getTimeOutSeconds());
	  	
	  	this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      
	}
	
	
	
	 public List<Product> getProductsFromLIDVIDs(List<String> lidvids, List<String> uniqueFields) throws IOException
	    {
	    	List<Product> products = new ArrayList<Product>();
	    	Product currenProduct;

	    	SearchRequest request = this.searchRequestBuilder.getQueryForLIDVIDs(
	    			lidvids, 
	    			uniqueFields
	    			);
	    	
	    	for (SearchHit hit : this.elasticSearchConnection.getRestHighLevelClient().search(request, RequestOptions.DEFAULT).getHits())
	    	{
	    		Map<String, Object> response = ElasticSearchUtil.elasticHashMapToJsonHashMap(hit.getSourceAsMap());
	    		uniqueFields.addAll(response.keySet());
	    		
	    		currenProduct = ElasticSearchUtil.ESentityProductToAPIProduct(
    					this.objectMapper.convertValue(response, EntityProduct.class));
	    		currenProduct.setProperties(response);
	    		products.add(currenProduct);
	    		
	    	}
	    	return products;
	    }
	
}
