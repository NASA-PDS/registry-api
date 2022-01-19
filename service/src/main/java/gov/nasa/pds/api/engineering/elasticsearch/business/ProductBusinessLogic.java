package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.net.URL;
import java.util.List;

import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.model.Summary;

public interface ProductBusinessLogic
{
	public String[] getMinimallyRequiredFields();
	public String[] getMaximallyRequiredFields();
	public Object getResponse();
	public void setBaseURL (URL baseURL);
	public void setObjectMapper (ObjectMapper om);
	public int setResponse (ElasticSearchHitIterator hits, Summary summary, List<String> fields, boolean onlySummary);
	public int setResponse (SearchHits hits, Summary summary, List<String> fields, boolean onlySummary);
}
