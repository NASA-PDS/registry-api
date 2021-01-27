package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.List;
import java.util.ArrayList;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
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
			int timeOutSeconds,
			String username,
			String password) {
		
		List<HttpHost> httpHosts = new ArrayList<HttpHost>();
		
		for (String host : hosts) {
			String hostPort[] = host.split(":");
			this.log.info("Connecting elasticSearch db " + hostPort[0] + ":" + hostPort[1]);
			httpHosts.add(new HttpHost(hostPort[0], 
            		Integer.parseInt(hostPort[1]), 
            		"http"));
	    	
			}
		
		RestClientBuilder builder;
		
		if ((username != null) && (username != ""))  {
		
			final CredentialsProvider credentialsProvider =
				    new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY,
				    new UsernamePasswordCredentials(username, password));
	
				builder = RestClient.builder(
						httpHosts.toArray(new HttpHost[httpHosts.size()]))
				    .setHttpClientConfigCallback(new HttpClientConfigCallback() {
				        @Override
				        public HttpAsyncClientBuilder customizeHttpClient(
				                HttpAsyncClientBuilder httpClientBuilder) {
				            return httpClientBuilder
				                .setDefaultCredentialsProvider(credentialsProvider);
				        }
				    });
		}
		else {
			builder = RestClient.builder(
            		httpHosts.toArray(new HttpHost[httpHosts.size()])); 
		}
		
		
		
		
		this.restHighLevelClient = new RestHighLevelClient(builder);
    	
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
