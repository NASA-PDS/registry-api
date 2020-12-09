package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.List;
import java.util.ArrayList;
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
	

	public ElasticSearchRegistryConnectionImpl(List<String> hosts, 
			String registryIndex,
			int timeOutSeconds) {
		
		List<HttpHost> httpHosts = new ArrayList<HttpHost>();
		
		for (String host : hosts) {
			String hostPort[] = host.split(":");
			this.log.info("Connecting elasticSearch db " + hostPort[0] + ":" + hostPort[1]);
			httpHosts.add(new HttpHost(hostPort[0], 
            		Integer.parseInt(hostPort[1]), 
            		"http"));
	    	
			}
		
		this.restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                		httpHosts.toArray(new HttpHost[httpHosts.size()])));
    	
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
