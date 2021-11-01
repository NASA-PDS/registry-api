package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.net.URL;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.model.Summary;

public interface ProductBusinessLogic
{
	public String[] getRequiredFields();
	public Object getResponse();
	public void setBaseURL (URL baseURL);
	public void setObjectMapper (ObjectMapper om);
	public int setResponse (ElasticSearchHitIterator hits, Summary summary, boolean onlySummary);
	public void setResponse (GetResponse hit, String lidvid);
	public int setResponse (SearchHits hits, Summary summary, boolean onlySummary);
}
