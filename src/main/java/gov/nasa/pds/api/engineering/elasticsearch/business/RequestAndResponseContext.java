package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.util.List;
import java.util.Map;

public class RequestAndResponseContext
{
    final private String queryString;
	final private String keyword;
    final private List<String> fields;
    final private List<String> sort;
    final private int start;
    final private int limit;
    final private Map<String, String> presetCriteria;
    final private boolean onlySummary;
    final private String format;
    
    public RequestAndResponseContext(
    		String q, String keyword, // search criteria
    		int start, int limit, // page information
    		List<String> fields, List<String> sort, // fields
    		boolean summaryOnly, // ingore all the data and just return keywords found
    		String output_format // the accept statment of the request that informs the output type
    		)
    {
    	this.queryString = q;
    	this.keyword = keyword;
    	this.fields = fields;
    	this.sort = sort;
    	this.start = start;
    	this.limit = limit;
    	this.onlySummary = summaryOnly;
    	this.presetCriteria = null;
    	this.format = output_format;
    }

    public String getKeyword() { return keyword; }
	public List<String> getFields() { return fields; }
	public List<String> getSort() { return sort; }
	public int getStart() { return start; }
	public int getLimit() { return limit; }
	public boolean isOnlySummary() { return onlySummary; }
	public String getQueryString() { return queryString; }
}
