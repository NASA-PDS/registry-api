package gov.nasa.pds.api.registry.opensearch;

import java.util.List;

import javax.net.ssl.SSLContext;

import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.RequestOptions;
import org.opensearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.opensearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import gov.nasa.pds.api.registry.opensearch.OpenSearchRegistryConnectionImplBuilder;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchRegistryConnectionImpl implements OpenSearchRegistryConnection {
	
    // key for getting the remotes from cross cluster config
	public static String CLUSTER_REMOTE_KEY = "cluster.remote";

	private static final Logger log = LoggerFactory.getLogger(OpenSearchRegistryConnectionImpl.class);
	
	private RestHighLevelClient restHighLevelClient;
	private String registryIndex;
	private String registryRefIndex;
	private int timeOutSeconds;
	private ArrayList<String> crossClusterNodes;
	
	
	public OpenSearchRegistryConnectionImpl()
	{		
	    this(new OpenSearchRegistryConnectionImplBuilder());
	}
	
	public OpenSearchRegistryConnectionImpl(OpenSearchRegistryConnectionImplBuilder connectionBuilder) {
			
		List<HttpHost> httpHosts = new ArrayList<HttpHost>();
		
		OpenSearchRegistryConnectionImpl.log.info("Connection to open search");
		for (String host : connectionBuilder.getHosts()) {
			String hostPort[] = host.split(":");
			OpenSearchRegistryConnectionImpl.log.info("Host " + hostPort[0] + ":" + hostPort[1]);
			httpHosts.add(new HttpHost(hostPort[0], 
            		Integer.parseInt(hostPort[1]), 
            		connectionBuilder.isSsl()?"https":"http"));
	    	
			}
		
		RestClientBuilder clientBuilder;
		String username = connectionBuilder.getUsername();
		if ((username != null) && !username.equals(""))  {
		
			
			OpenSearchRegistryConnectionImpl.log.info("Set openSearch connection with username/password");
			final CredentialsProvider credentialsProvider =
				    new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY,
			    new UsernamePasswordCredentials(username, connectionBuilder.getPassword()));

			clientBuilder = RestClient.builder(
					httpHosts.toArray(new HttpHost[httpHosts.size()]))
			    .setHttpClientConfigCallback(new HttpClientConfigCallback() {
			        @Override
			        public HttpAsyncClientBuilder customizeHttpClient(
			                HttpAsyncClientBuilder httpClientBuilder) {
			        	
			        	try {
				        	
			        		if (connectionBuilder.isSsl()) {
			        			OpenSearchRegistryConnectionImpl.log.info("Connection over SSL");
					        	SSLContextBuilder sslBld = SSLContexts.custom(); 
						        sslBld.loadTrustMaterial(new TrustSelfSignedStrategy());
						        SSLContext sslContext = sslBld.build();
	
						        httpClientBuilder.setSSLContext(sslContext);
						        if (!connectionBuilder.isSslCertificateCNVerification()) {
						        	httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
						        }
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
			OpenSearchRegistryConnectionImpl.log.info("Set openSearch connection");
			clientBuilder = RestClient.builder(
            		httpHosts.toArray(new HttpHost[httpHosts.size()])); 
		}
		
		
		this.restHighLevelClient = new RestHighLevelClient(clientBuilder);
    	
		this.crossClusterNodes = checkCCSConfig();
		this.registryIndex = createCCSIndexString(connectionBuilder.getRegistryIndex());
		this.registryRefIndex = createCCSIndexString(connectionBuilder.getRegistryRefIndex());
    	this.timeOutSeconds = connectionBuilder.getTimeOutSeconds();
		
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
	
	private ArrayList<String> checkCCSConfig() {
		ArrayList<String> result = null;
		
		try {
			ClusterGetSettingsRequest request = new ClusterGetSettingsRequest();
			ClusterGetSettingsResponse response = restHighLevelClient.cluster().getSettings(request, RequestOptions.DEFAULT); 
		
			Set<String> clusters = response.getPersistentSettings().getGroups(CLUSTER_REMOTE_KEY).keySet();
			if (clusters.size() > 0) {
				result = new ArrayList<String>(clusters);
				OpenSearchRegistryConnectionImpl.log.info("Cross cluster search is active: (" + result.toString() + ")");
			} else {
				OpenSearchRegistryConnectionImpl.log.info("Cross cluster search is inactive");
			}
		}
		catch(Exception ex) {
		    log.warn("Could not get cluster information. Cross cluster search is inactive. " + ex.getMessage());
		}
		return result;
	}

	// if CCS configuration has been detected, use nodes in consolidated index names, otherwise just return the index
    private String createCCSIndexString(String indexName) {
        String result = indexName;
    	if (this.crossClusterNodes != null) {
    		// start with the local index
    		StringBuilder indexBuilder = new StringBuilder(indexName);
    		for(String cluster : this.crossClusterNodes) {
    			indexBuilder.append(",");
    			indexBuilder.append(cluster + ":" + indexName);
    		}
    		result = indexBuilder.toString();
    	}
    	
    	return result;
    }
    
    
    public void close()
    {
        try
        {
            restHighLevelClient.close();
        }
        catch(Exception ex)
        {
            // Ignore
        }
    }
}
