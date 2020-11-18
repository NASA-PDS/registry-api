package gov.nasa.pds.api.engineering.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ElasticSearchRegistryConnectionImpl implements ElasticSearchRegistryConnection {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchRegistryConnectionImpl.class);
	
	private RestHighLevelClient restHighLevelClient;
	private String registryIndex;
	private int timeOutSeconds;
	

	public ElasticSearchRegistryConnectionImpl(String host, 
			int port, 
			String registryIndex,
			int timeOutSeconds) {
		
		this.log.info("Connecting elasticSearch db " + host + ":" + Integer.toString(port));
		
    	this.restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, 
                        		port, 
                        		"http")));
    	
    	this.registryIndex = registryIndex;
    	this.timeOutSeconds = timeOutSeconds;
   
		
	}

	public RestHighLevelClient getRestHighLevelClient() {
		return restHighLevelClient;
	}

	public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}
	
	public String getRegistryIndex() {
		return registryIndex;
	}

	public void setRegistryIndex(String registryIndex) {
		this.registryIndex = registryIndex;
	}

	public int getTimeOutSeconds() {
		return timeOutSeconds;
	}

	public void setTimeOutSeconds(int timeOutSeconds) {
		this.timeOutSeconds = timeOutSeconds;
	}
}
