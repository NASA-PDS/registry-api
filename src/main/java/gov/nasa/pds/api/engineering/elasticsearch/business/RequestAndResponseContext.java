package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.engineering.exceptions.ApplicationTypeException;
import gov.nasa.pds.model.Summary;

public class RequestAndResponseContext
{
	final private long begin_processing = System.currentTimeMillis();
	final private String queryString;
	final private String keyword;
	final private String lidvid;
    final private List<String> fields;
    final private List<String> sort;
    final private int start;
    final private int limit;
    final private Map<String, String> presetCriteria;
    final private boolean onlySummary;
    final private ProductVersionSelector selector;
    final private String format;
    final private Map<String, ProductBusinessLogic> formatters;
    final private ObjectMapper om;
    final private URL baseURL;

    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String lidvid,
    		String output_format // the accept statment of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, "","", lidvid, 0, 10, new ArrayList<String>(), new ArrayList<String>(), false, ProductVersionSelector.ORIGINAL, output_format); }

    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String lidvid,
    		int start, int limit, // page information
    		String output_format // the accept statment of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, "","", lidvid, start, limit, new ArrayList<String>(), new ArrayList<String>(), false, ProductVersionSelector.ORIGINAL, output_format); }

    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String lidvid,
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ingore all the data and just return keywords found
    		String output_format // the accept statment of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, "","", lidvid, start, limit, fields, sort, summaryOnly, ProductVersionSelector.ORIGINAL, output_format); }

    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String lidvid,
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ingore all the data and just return keywords found
    		ProductVersionSelector selector, // all, latest, orginal
    		String output_format // the accept statment of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, "","", lidvid, start, limit, fields, sort, summaryOnly, selector, output_format); }
    
    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String q, String keyword, // search criteria
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ingore all the data and just return keywords found
    		String output_format // the accept statment of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, q, keyword, "", start, limit, fields, sort, summaryOnly, ProductVersionSelector.ORIGINAL, output_format); }

    private RequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String q, String keyword, // search criteria
    		String lidvid, // specific lidvid to find
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ingore all the data and just return keywords found
    		ProductVersionSelector selector, // all, latest, orginal
    		String output_format // the accept statment of the request that informs the output type
    		) throws ApplicationTypeException
    {
    	Map<String, ProductBusinessLogic> formatters = new HashMap<String, ProductBusinessLogic>();
    	formatters.put("application/json", new PdsProductBusinessObject());
    	formatters.put("application/pds4+json", new Pds4ProductBusinessObject());
    	this.formatters = formatters;

    	this.baseURL = base;
    	this.om = om;
    	this.queryString = q;
    	this.keyword = keyword;
    	this.lidvid = lidvid;
    	this.fields = new ArrayList<String>();
    	this.fields.addAll(this.add_output_needs (fields));
    	this.sort = sort == null ? new ArrayList<String>() : sort;
    	this.start = start;
    	this.limit = limit;
    	this.onlySummary = summaryOnly;
    	this.presetCriteria = null;
    	this.selector = selector;
    	this.format = output_format;
    }
    
    public String getKeyword() { return this.keyword; }
    public String getLIDVID() { return this.lidvid; }
	public final List<String> getFields() { return this.fields; }
	public final List<String> getSort() { return this.sort; }
	public int getStart() { return this.start; }
	public int getLimit() { return this.limit; }
	public boolean isOnlySummary() { return this.onlySummary; }
	public String getQueryString() { return this.queryString; }
	public final Map<String, String> getPresetCriteria() { return this.presetCriteria; };
	public ProductVersionSelector getSelector() { return this.selector; }

	private List<String> add_output_needs (List<String> given) throws ApplicationTypeException
	{
		ArrayList<String> complete = new ArrayList<String>();
		String output_needs[] = {};
		
		if (this.formatters.containsKey(this.format))
		{ 
			this.formatters.get(this.format).setBaseURL(this.baseURL);
			this.formatters.get(this.format).setObjectMapper(this.om);
			output_needs = this.formatters.get(this.format).getRequiredFields();
		}
		else throw new ApplicationTypeException("The given application type, " + this.format + ", is not known by RquestAndResponseContext.");

		complete.addAll(given);
		for (int index=0 ; index < output_needs.length ; index++)
		{ if (!complete.contains(output_needs[index])) complete.add(output_needs[index]); }
		return complete;
	}
	
	public Object getResponse()
	{ return this.formatters.get(this.format).getResponse(); }
	
	public void setResponse(ElasticSearchHitIterator hits, int real_total)
	{ 
		Summary summary = new Summary();
		summary.setQ(this.getQueryString());
		summary.setStart(this.getStart());
		summary.setLimit(this.getLimit());
		summary.setSort(this.getSort());
		summary.setHits(this.formatters.get(this.format).setResponse(hits, summary, onlySummary));
		summary.setTook((int)(System.currentTimeMillis() - this.begin_processing));
		summary.setProperties(new ArrayList<String>());
		
		if (0 < real_total) summary.setHits(real_total);
	}

	public void setResponse(GetResponse hit)
	{ if (hit != null) this.formatters.get(this.format).setResponse(hit, this.getLIDVID()); }

	public void setResponse(SearchHits hits)
	{ this.setResponse(hits, null); }

	public void setResponse(SearchHits hits, List<String> uniqueProperties)
	{ if (hits != null) this.setResponse(hits, uniqueProperties, (int)hits.getTotalHits().value); }

	public void setResponse(SearchHits hits, List<String> uniqueProperties, int total_hits)
	{
		if (hits != null)
		{
			Summary summary = new Summary();
			summary.setQ(this.getQueryString());
			summary.setStart(this.getStart());
			summary.setLimit(this.getLimit());
			summary.setSort(this.getSort());
			summary.setHits(total_hits);
			summary.setTook((int)(System.currentTimeMillis() - this.begin_processing));

			if (uniqueProperties != null) summary.setProperties(uniqueProperties);
			this.formatters.get(this.format).setResponse(hits, summary, this.onlySummary);
		}
	}
	
	public void setResponse(RestHighLevelClient client, SearchRequest request) throws IOException
	{
        request.source().size(this.getLimit());
        request.source().from(this.getStart());
        this.setResponse(client.search(request, RequestOptions.DEFAULT).getHits());
	}
}
