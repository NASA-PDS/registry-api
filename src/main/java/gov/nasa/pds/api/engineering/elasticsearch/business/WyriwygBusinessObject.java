package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.model.WyriwygProduct;
import gov.nasa.pds.model.WyriwygProductKeyValuePairs;
import gov.nasa.pds.model.WyriwygProducts;

public class WyriwygBusinessObject implements ProductBusinessLogic
{
	@SuppressWarnings("unused")
	private ObjectMapper om;
	@SuppressWarnings("unused")
	private URL baseURL;
	private WyriwygProduct product = null;
	private WyriwygProducts products = null;
	
	@Override
	public String[] getRequiredFields() { return new String[0]; }

	@Override
	public Object getResponse() { return this.product == null ? this.products : this.product; }

	@Override
	public void setBaseURL(URL baseURL) { this.baseURL = baseURL; }

	@Override
	public void setObjectMapper(ObjectMapper om) { this.om = om; }

	@Override
	public int setResponse(ElasticSearchHitIterator hits, Summary summary, boolean onlySummary)
	{
		Set<String> uniqueProperties = new TreeSet<String>();
		WyriwygProducts products = new WyriwygProducts();

		for (Map<String,Object> kvps : hits)
        {
            uniqueProperties.addAll(kvps.keySet());

            if (!onlySummary)
            {
            	WyriwygProduct product = new WyriwygProduct();
            	for (Entry<String, Object> pair : kvps.entrySet())
            	{
            		WyriwygProductKeyValuePairs kvp = new WyriwygProductKeyValuePairs();
            		kvp.setKey(pair.getKey());
            		kvp.setValue(String.valueOf(pair.getValue()));
            		product.addKeyValuePairsItem(kvp);
            	}
            }
        }
		summary.setProperties(new ArrayList<String>(uniqueProperties));
		products.setSummary(summary);
		this.products = products;
		return products.getData().size();
	}

	@Override
	public void setResponse(GetResponse hit, String lidvid)
	{
    	WyriwygProduct product = new WyriwygProduct();
    	for (Entry<String, Object> pair : hit.getSourceAsMap().entrySet())
    	{
    		WyriwygProductKeyValuePairs kvp = new WyriwygProductKeyValuePairs();
    		kvp.setKey(pair.getKey());
    		kvp.setValue(String.valueOf(pair.getValue()));
    		product.addKeyValuePairsItem(kvp);
    	}
    	this.product = product;
	}

	@Override
	public int setResponse(SearchHits hits, Summary summary, boolean onlySummary)
	{
		Set<String> uniqueProperties = new TreeSet<String>();
		WyriwygProducts products = new WyriwygProducts();

		for (SearchHit hit : hits.getHits())
        {
			Map<String, Object> kvps = hit.getSourceAsMap();
            uniqueProperties.addAll(kvps.keySet());

            if (!onlySummary)
            {
            	WyriwygProduct product = new WyriwygProduct();
            	for (Entry<String, Object> pair : kvps.entrySet())
            	{
            		WyriwygProductKeyValuePairs kvp = new WyriwygProductKeyValuePairs();
            		kvp.setKey(pair.getKey());
            		kvp.setValue(String.valueOf(pair.getValue()));
            		product.addKeyValuePairsItem(kvp);
            	}
            }
        }
		summary.setProperties(new ArrayList<String>(uniqueProperties));
		products.setSummary(summary);
		this.products = products;
		return (int)(hits.getTotalHits().value);
	}

}
