package gov.nasa.pds.api.registry.elasticsearch.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.model.PdsProduct;
import gov.nasa.pds.model.PdsProducts;
import gov.nasa.pds.model.PropertyArrayValues;
import gov.nasa.pds.model.Summary;

public class PdsProductBusinessObject implements ProductBusinessLogic
{
    private ObjectMapper objectMapper;
    private PdsProduct product = null;
    private PdsProducts products = null;
    private URL baseURL;
    
	@Override
	public String[] getMaximallyRequiredFields()
	{ return new String[0]; }

	@Override
	public String[] getMinimallyRequiredFields()
	{ return EntityProduct.JSON_PROPERTIES; }

	@Override
	public Object getResponse()
	{ return this.product == null ? this.products : this.product; }

	@Override
	public void setBaseURL (URL baseURL) { this.baseURL = baseURL; }

	@Override
	public void setObjectMapper (ObjectMapper om) { this.objectMapper = om; }

	@Override
	@SuppressWarnings("unchecked")
	public void setResponse (SearchHit hit, List<String> fields)
	{
		Map<String,Object> kvp = hit.getSourceAsMap();;
		PdsProduct product;

		product = ElasticSearchUtil.ESentityProductToAPIProduct(objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL);
        product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)ProductBusinessObject.getFilteredProperties(kvp, null, null));
        this.product = product;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int setResponse(ElasticSearchHitIterator hits, Summary summary, List<String> fields, boolean onlySummary)
	{
		PdsProducts products = new PdsProducts();
		Set<String> uniqueProperties = new TreeSet<String>();

		for (Map<String,Object> kvp : hits)
        {
            uniqueProperties.addAll(ProductBusinessObject.getFilteredProperties(kvp, fields, null).keySet());

            if (!onlySummary)
            {
            	products.addDataItem(ElasticSearchUtil.ESentityProductToAPIProduct(objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
                products.getData().get(products.getData().size()-1).setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)ProductBusinessObject.getFilteredProperties(kvp, null, null));
            }
        }
		
		if (onlySummary) products.setData(null);

		summary.setProperties(new ArrayList<String>(uniqueProperties));
		products.setSummary(summary);
		this.products = products;
		return products.getData().size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public int setResponse(SearchHits hits, Summary summary, List<String> fields, boolean onlySummary)
	{
		Map<String,Object> kvp;
		PdsProducts products = new PdsProducts();
		Set<String> uniqueProperties = new TreeSet<String>();

		for (SearchHit hit : hits)
        {
			kvp = hit.getSourceAsMap();
            uniqueProperties.addAll(ProductBusinessObject.getFilteredProperties(kvp, fields, null).keySet());

            if (!onlySummary)
            {
            	products.addDataItem(ElasticSearchUtil.ESentityProductToAPIProduct(objectMapper.convertValue(kvp, EntityProduct.class), this.baseURL));
                products.getData().get(products.getData().size()-1).setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)ProductBusinessObject.getFilteredProperties(kvp, null, null));
            }
        }
		
		if (onlySummary) products.setData(null);

		summary.setProperties(new ArrayList<String>(uniqueProperties));
		products.setSummary(summary);
		this.products = products;
		return (int)hits.getTotalHits().value;
	}
}
