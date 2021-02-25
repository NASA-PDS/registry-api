package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.List;

import javax.net.ssl.SSLContext;

import java.util.ArrayList;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
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
	private String registryRefIndex;
	private int timeOutSeconds;
	
	

	public ElasticSearchRegistryConnectionImpl(List<String> hosts, 
			String registryIndex,
			String registryRefIndex,
			int timeOutSeconds,
			String username,
			String password,
			boolean ssl) {
		
		List<HttpHost> httpHosts = new ArrayList<HttpHost>();
		
		ElasticSearchRegistryConnectionImpl.log.info("Connection to elastic search");
		for (String host : hosts) {
			String hostPort[] = host.split(":");
			ElasticSearchRegistryConnectionImpl.log.info("Host " + hostPort[0] + ":" + hostPort[1]);
			httpHosts.add(new HttpHost(hostPort[0], 
            		Integer.parseInt(hostPort[1]), 
            		ssl?"https":"http"));
	    	
			}
		
		RestClientBuilder builder;
		
		if ((username != null) && (username != ""))  {
		
			
			ElasticSearchRegistryConnectionImpl.log.info("Set elasticSearch connection with username/password");
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
			        	
			        	try {
				        	
			        		if (ssl) {
			        			ElasticSearchRegistryConnectionImpl.log.info("Connection over SSL");
					        	SSLContextBuilder sslBld = SSLContexts.custom(); 
						        sslBld.loadTrustMaterial(new TrustSelfSignedStrategy());
						        SSLContext sslContext = sslBld.build();
	
						        httpClientBuilder.setSSLContext(sslContext);
			        		}
				        	
				            return httpClientBuilder
				                .setDefaultCredentialsProvider(credentialsProvider);
			        	}
			            catch(Exception ex)
			            {
			                throw new RuntimeException(ex);
			            }
			        }
			    });
		}
		else {
			ElasticSearchRegistryConnectionImpl.log.info("Set elasticSearch connection");
			builder = RestClient.builder(
            		httpHosts.toArray(new HttpHost[httpHosts.size()])); 
		}
		
		
		
		
		this.restHighLevelClient = new RestHighLevelClient(builder);
    	
    	this.registryIndex = registryIndex;
    	this.registryRefIndex = registryRefIndex;
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

	public void setRegistryIndex(String registryRefIndex) {
		this.registryRefIndex = registryRefIndex;
	}
	
	public String getRegistryRefIndex() {
		return registryRefIndex;
	}

	public void setRegistryRefIndex(String registryRefIndex) {
		this.registryRefIndex = registryRefIndex;
	}

	public int getTimeOutSeconds() {
		return timeOutSeconds;
	}

	public void setTimeOutSeconds(int timeOutSeconds) {
		this.timeOutSeconds = timeOutSeconds;
	}
}
