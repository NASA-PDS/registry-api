package gov.nasa.pds.api.registry.business;

import java.net.URL;
import java.util.List;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.search.ElasticSearchHitIterator;
import gov.nasa.pds.model.Summary;

public interface ProductBusinessLogic
{
	public String[] getMinimallyRequiredFields();
	public String[] getMaximallyRequiredFields();
	public Object getResponse();
	public void setBaseURL (URL baseURL);
	public void setObjectMapper (ObjectMapper om);
	public void setResponse (SearchHit hit, List<String> fields);
	public int setResponse (ElasticSearchHitIterator hits, Summary summary, List<String> fields, boolean onlySummary);
	public int setResponse (SearchHits hits, Summary summary, List<String> fields, boolean onlySummary);
}
