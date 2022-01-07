package gov.nasa.pds.api.registry.elasticsearch.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.registry.elasticsearch.Pds4JsonSearchRequestBuilder;
import gov.nasa.pds.model.Pds4Product;
import gov.nasa.pds.model.Pds4Products;
import gov.nasa.pds.model.Summary;

public class Pds4ProductBusinessObject implements ProductBusinessLogic
{
    @SuppressWarnings("unused")
	private ObjectMapper objectMapper;
	private Pds4Product product = null;
	private Pds4Products products = null;
    @SuppressWarnings("unused")
	private URL baseURL;
	
	@Override
	public String[] getMaximallyRequiredFields()
	{ return Pds4JsonSearchRequestBuilder.PDS4_JSON_PRODUCT_FIELDS; }

	@Override
	public String[] getMinimallyRequiredFields()
	{ return Pds4JsonSearchRequestBuilder.PDS4_JSON_PRODUCT_FIELDS; }

	@Override
	public Object getResponse()
	{ return this.product == null ? this.products : this.product; }

	@Override
	public void setBaseURL (URL baseURL) { this.baseURL = baseURL; }

	@Override
	public void setObjectMapper (ObjectMapper om) { this.objectMapper = om; }

	@Override
	public int setResponse(ElasticSearchHitIterator hits, Summary summary, List<String> fields, boolean onlySummary)
	{
        List<Pds4Product> list = new ArrayList<Pds4Product>();
        Pds4Products products = new Pds4Products();
		Set<String> uniqueProperties = new TreeSet<String>();

		for (Map<String,Object> kvp : hits)
        {
            uniqueProperties.addAll(ProductBusinessObject.getFilteredProperties(kvp, fields, null).keySet());

            if (!onlySummary)
            {
            	Pds4Product prod = Pds4JsonProductFactory.createProduct(hits.getCurrentId(), kvp);
            	list.add(prod);
            }
        }

		products.setData(list);
		products.setSummary(summary);
		summary.setProperties(new ArrayList<String>(uniqueProperties));
		this.products = products;
		return list.size();
	}
	@Override
	public void setResponse(GetResponse hit, String lidvid)
	{
        Map<String, Object> fieldMap = hit.getSourceAsMap();
        this.product = Pds4JsonProductFactory.createProduct(lidvid, fieldMap);
	}

	@Override
	public int setResponse(SearchHits hits, Summary summary, List<String> fields, boolean onlySummary)
	{
        List<Pds4Product> list = new ArrayList<Pds4Product>();
        Pds4Products products = new Pds4Products();
		Set<String> uniqueProperties = new TreeSet<String>();

        // Products
        for(SearchHit hit : hits) 
        {
            String id = hit.getId();
            Map<String, Object> fieldMap = hit.getSourceAsMap();
            
            uniqueProperties.addAll(ProductBusinessObject.getFilteredProperties(fieldMap, fields, null).keySet());

            if (!onlySummary)
            {
            	Pds4Product prod = Pds4JsonProductFactory.createProduct(id, fieldMap);
            	list.add(prod);
            }
        }
        products.setData(list);
        products.setSummary(summary);
        summary.setProperties(new ArrayList<String>(uniqueProperties));
        this.products = products;
        return (int)hits.getTotalHits().value;
	}
}
