package gov.nasa.pds.api.engineering;

import java.io.IOException;

import org.apache.http.HttpHost;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.elasticsearch.client.RestClient;

@Configuration 
public class ElasticSearchConfig { 
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchConfig.class);
	  
	@Value("elasticSearch.host:localhost")
	private String host;
	
	@Value("elasticSearch.port:9200")
	private int port;
	
	@Value("elasticSearch.registryIndex:registry")
	private String registryIndex;
   
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getRegistryIndex() {
		return registryIndex;
	}
	
	public void setRegistryIndex(String registryIndex) {
		this.registryIndex = registryIndex;
	}
		
	@Bean
    public RestHighLevelClient restHighLevelClient() {
     

    	this.log.info("Connecting elasticSearch db " + this.host + ":" + Integer.toString(this.port));
    	RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(this.host, 
                        		this.port, 
                        		"http")));
   
     
        return restHighLevelClient;
    }
    

}
