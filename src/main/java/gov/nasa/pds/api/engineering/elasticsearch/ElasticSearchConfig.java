package gov.nasa.pds.api.engineering.elasticsearch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnectionImpl;

@Configuration 
public class ElasticSearchConfig { 
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchConfig.class);
	  
	@Value("${elasticSearch.host:localhost}")
	private String host;
	
	@Value("${elasticSearch.port:9200}")
	private int port;
	
	@Value("${elasticSearch.registryIndex:registry}")
	private String registryIndex;
	
	@Value("${elasticSearch.timeOutSeconds:60}")
	private int timeOutSeconds;
   
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
    public ElasticSearchRegistryConnection ElasticSearchRegistryConnection() {
     
		return new ElasticSearchRegistryConnectionImpl(this.host,
				this.port,
				this.registryIndex,
				this.timeOutSeconds);

    }
    

}
