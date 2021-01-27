package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.List;
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
	 
	@Value("#{'${elasticSearch.host}'.split(',')}:localhost:9200") 
	private List<String> hosts;
	
	@Value("${elasticSearch.registryIndex:registry}")
	private String registryIndex;
	
	@Value("${elasticSearch.timeOutSeconds:60}")
	private int timeOutSeconds;
	
	@Value("${elasticSearch.username}")
	private String username;
	
	@Value("${elasticSearch.password}")
	private String password;
	
	@Value("${elaticSearch.ssl:false}")
	private Boolean ssl;
    
	public List<String> getHosts() {
		return hosts;
	}

	public void setHost(List<String> hosts) {
		this.hosts = hosts;
	}
		
	public String getRegistryIndex() {
		return registryIndex;
	}
	
	public void setRegistryIndex(String registryIndex) {
		this.registryIndex = registryIndex;
	}
	
		
	public Boolean isSsl() {
		return ssl;
	}

	public void setSsl(Boolean ssl) {
		this.ssl = ssl;
	}

	@Bean
    public ElasticSearchRegistryConnection ElasticSearchRegistryConnection() {
     
		return new ElasticSearchRegistryConnectionImpl(this.hosts,
				this.registryIndex,
				this.timeOutSeconds,
				this.username,
				this.password,
				this.ssl);

    }
    

}
