package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.util.StringTokenizer;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.engineering.exceptions.NothingFoundException;
import gov.nasa.pds.model.Summary;

public class RequestAndResponseContext
{
    private static final Logger log = LoggerFactory.getLogger(RequestAndResponseContext.class);

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
    		Map<String,String> preset, // default criteria when nothing else is defined
    		String output_format // the accept statement of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, null, null, lidvid, 0, 10, new ArrayList<String>(), new ArrayList<String>(), false, ProductVersionSelector.ORIGINAL, preset, output_format); }

    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String lidvid,
    		int start, int limit, // page information
    		Map<String,String> preset, // default criteria when nothing else is defined
    		String output_format // the accept statement of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, null, null, lidvid, start, limit, new ArrayList<String>(), new ArrayList<String>(), false, ProductVersionSelector.ORIGINAL, preset, output_format); }

    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String lidvid,
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ingore all the data and just return keywords found
    		Map<String,String> preset, // default criteria when nothing else is defined
    		String output_format // the accept statement of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, null, null, lidvid, start, limit, fields, sort, summaryOnly, ProductVersionSelector.ORIGINAL, preset, output_format); }

    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String lidvid,
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ingore all the data and just return keywords found
    		ProductVersionSelector selector, // all, latest, orginal
    		Map<String,String> preset, // default criteria when nothing else is defined
    		String output_format // the accept statement of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, null, null, lidvid, start, limit, fields, sort, summaryOnly, selector, preset, output_format); }
    
    static public RequestAndResponseContext buildRequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String q, String keyword, // search criteria
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ignore all the data and just return keywords found
    		Map<String,String> preset, // default criteria when nothing else is defined
    		String output_format // the accept statement of the request that informs the output type
    		) throws ApplicationTypeException
    { return new RequestAndResponseContext(om, base, q, keyword, null, start, limit, fields, sort, summaryOnly, ProductVersionSelector.ORIGINAL, preset, output_format); }

    private RequestAndResponseContext(
    		ObjectMapper om, URL base, // webby criteria
    		String q, String keyword, // search criteria
    		String lidvid, // specific lidvid to find
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ignore all the data and just return keywords found
    		ProductVersionSelector selector, // all, latest, original
    		Map<String,String> preset, // default criteria when nothing else is defined
    		String output_format // the accept statement of the request that informs the output type
    		) throws ApplicationTypeException
    {
    	Map<String, ProductBusinessLogic> formatters = new HashMap<String, ProductBusinessLogic>();
    	formatters.put("*/*", new PdsProductBusinessObject());
    	formatters.put("application/csv", new WyriwygBusinessObject());
    	formatters.put("application/json", new PdsProductBusinessObject());
    	formatters.put("application/kvp+json", new WyriwygBusinessObject());
    	formatters.put("application/pds4+json", new Pds4ProductBusinessObject());
    	formatters.put("application/pds4+xml", new Pds4ProductBusinessObject());
    	formatters.put("application/xml", new PdsProductBusinessObject());
    	formatters.put("text/csv", new WyriwygBusinessObject());
    	formatters.put("text/html", new PdsProductBusinessObject());
    	this.formatters = formatters;
    	this.format = this.find_match(output_format);
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
    	this.presetCriteria = preset;
    	this.selector = selector;
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
		List<String> complete = new ArrayList<String>();
		String max_needs[] = {}, min_needs[] = {};
		given = ElasticSearchUtil.jsonPropertyToElasticProperty(given);

		if (this.formatters.containsKey(this.format))
		{ 
			this.formatters.get(this.format).setBaseURL(this.baseURL);
			this.formatters.get(this.format).setObjectMapper(this.om);
			max_needs = ElasticSearchUtil.jsonPropertyToElasticProperty(this.formatters.get(this.format).getMaximallyRequiredFields());
			min_needs = ElasticSearchUtil.jsonPropertyToElasticProperty(this.formatters.get(this.format).getMinimallyRequiredFields());
		}
		else
		{
			log.warn("Could not find a matach for application type: " + String.valueOf(this.format));
			log.warn("   Known types: " + String.valueOf(this.formatters.keySet().size()));
			for (String key : this.formatters.keySet()) log.warn("      key: " + String.valueOf(key));
			throw new ApplicationTypeException("The given application type, " + String.valueOf(this.format) + ", is not known by RquestAndResponseContext.");
		}

		/* if the URL contains fields, then make sure the minimum was included too OR there is maximum set. */
		if ((given != null && 0 < given.size()) || 0 < max_needs.length)
		{
			if (given != null) complete.addAll(given);

			for (int index=0 ; index < min_needs.length ; index++)
			{ if (!complete.contains(min_needs[index])) complete.add(min_needs[index]); }
		}
		
		if (0 < max_needs.length)
		{
			List<String> allowed = Arrays.asList(max_needs);
			List<String> filtered = new ArrayList<String>();

			for (String field : complete)
			{
				if (allowed.contains(field)) { filtered.add(field); }
			}
			complete = filtered;
		}
		return complete;
	}
	
	private String find_match (String from_user)
	{
		String match=from_user;
		StringTokenizer mimes = new StringTokenizer(from_user, ",");
		
		while (mimes.hasMoreTokens())
		{
			/* separate the mime_type/mime_subtype from ;* stuff */
			String mime = mimes.nextToken();
			if (mime.contains(";")) mime = mime.substring(0, mime.indexOf(";"));
			
			if (this.formatters.keySet().contains(mime))
			{
				match = mime;
				break;
			}
		}
		log.info("Matched output type as '" + match + "' from '" + from_user + "'.");
		return match;
	}

	public Object getResponse() throws NothingFoundException
	{
		Object response = this.formatters.get(this.format).getResponse();
		
		if (response == null)
		{
			log.warn("Could not find any data given these conditions");
			log.warn("   fields: " + String.valueOf(this.getFields()));
			for (String field : this.getFields()) log.warn("      " + field);
			log.warn("   keyword: " + this.getKeyword());
			log.warn("   lidvid: " + this.getLIDVID());
			log.warn("   limit: " + String.valueOf(this.getLimit()));
			log.warn("   query string: " + String.valueOf(this.getQueryString()));
			log.warn("   selector: " + String.valueOf(this.getSelector()));
			log.warn("   sorting: " + String.valueOf(this.getSort().size()));
			for (String sort : this.getSort()) log.warn("      " + sort);
			log.warn("   start: " + String.valueOf(this.getStart()));
			log.warn("   summary: " + String.valueOf(this.isOnlySummary()));
			throw new NothingFoundException();
		}
		return response;
	}
	
	public void setResponse(ElasticSearchHitIterator hits, int real_total)
	{ 
		Summary summary = new Summary();
		summary.setQ(this.getQueryString());
		summary.setStart(this.getStart());
		summary.setLimit(this.getLimit());
		summary.setSort(this.getSort());
		summary.setHits(this.formatters.get(this.format).setResponse(hits, summary, this.fields, this.onlySummary));
		summary.setProperties(new ArrayList<String>());
		
		if (0 < real_total) summary.setHits(real_total);

		summary.setTook((int)(System.currentTimeMillis() - this.begin_processing));
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

			if (uniqueProperties != null) summary.setProperties(uniqueProperties);
			this.formatters.get(this.format).setResponse(hits, summary, this.fields, this.onlySummary);

			summary.setTook((int)(System.currentTimeMillis() - this.begin_processing));
		}
	}
	
	public void setResponse(RestHighLevelClient client, SearchRequest request) throws IOException
	{
        request.source().size(this.getLimit());
        request.source().from(this.getStart());
        this.setResponse(client.search(request, RequestOptions.DEFAULT).getHits());
	}
}
