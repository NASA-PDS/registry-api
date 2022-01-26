package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
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

    public final boolean isJSON;
    public final String[] PDS4_PRODUCT_FIELDS;

    Pds4ProductBusinessObject (boolean isJSON)
    {
    	String temp[] = {
    		// BLOB
    		(isJSON ? Pds4ProductFactory.FLD_JSON_BLOB : Pds4ProductFactory.FLD_XML_BLOB),

    		// Data File Info
    		Pds4ProductFactory.FLD_DATA_FILE_NAME,
    		Pds4ProductFactory.FLD_DATA_FILE_CREATION,
    		Pds4ProductFactory.FLD_DATA_FILE_REF,
    		Pds4ProductFactory.FLD_DATA_FILE_SIZE,
    		Pds4ProductFactory.FLD_DATA_FILE_MD5,
    		Pds4ProductFactory.FLD_DATA_FILE_MIME_TYPE,

    	    // Label Info
    	    Pds4ProductFactory.FLD_LABEL_FILE_NAME,
    	    Pds4ProductFactory.FLD_LABEL_FILE_CREATION,
    	    Pds4ProductFactory.FLD_LABEL_FILE_REF,
    	    Pds4ProductFactory.FLD_LABEL_FILE_SIZE,
    	    Pds4ProductFactory.FLD_LABEL_FILE_MD5,

    	    // Node Name
    	    Pds4ProductFactory.FLD_NODE_NAME};

    	this.PDS4_PRODUCT_FIELDS = temp;
    	this.isJSON = isJSON;
    }

	@Override
	public String[] getMaximallyRequiredFields()
	{ return this.PDS4_PRODUCT_FIELDS; }

	@Override
	public String[] getMinimallyRequiredFields()
	{ return this.PDS4_PRODUCT_FIELDS; }

	@Override
	public Object getResponse()
	{ return this.product == null ? this.products : this.product; }

	@Override
	public void setBaseURL (URL baseURL) { this.baseURL = baseURL; }

	@Override
	public void setObjectMapper (ObjectMapper om) { this.objectMapper = om; }

	@Override
	public void setResponse (SearchHit hit, List<String> fields)
	{ this.product = Pds4ProductFactory.createProduct(hit.getId(), hit.getSourceAsMap(), this.isJSON); }

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
            	Pds4Product prod = Pds4ProductFactory.createProduct(hits.getCurrentId(), kvp, this.isJSON);
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
            	Pds4Product prod = Pds4ProductFactory.createProduct(id, fieldMap, this.isJSON);
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
