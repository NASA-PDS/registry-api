package gov.nasa.pds.api.registry.opensearch;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nasa.pds.api.registry.SystemConstants;
import gov.nasa.pds.api.registry.business.ProductBusinessObject;
import gov.nasa.pds.api.registry.configuration.AWSSecretsAccess;
import gov.nasa.pds.api.registry.search.RegistrySearchRequestBuilder;

/* Keep this eventhough not directly referenced
 * 
 * Seems to found and used via reflection rather than direct reference. When removed
 * it causes spring to fail with an opensearch failure requiring EleasticSearchRegistryConnection.
 * Not sure why the error indicates an interface that is already there when this class is missing
 * but that is what happened.
 */

@Configuration 
public class OpenSearchConfig { 
	
	private static final Logger log = LoggerFactory.getLogger(OpenSearchConfig.class);

	// This default for ES hosts is set in the constructor since we first want to check
	// the environment if not set in the application properties. This preserves the
	// original behavior when the default was specified in the Value annotation.
	private static final String DEFAULT_ES_HOST = "localhost:9200";
	
	@Value("#{'${openSearch.host:}'.split(',')}") 
	private List<String> hosts;
	
	@Value("${openSearch.registryIndex:registry}")
	private String registryIndex;
	
	@Value("${openSearch.registryRefIndex:registry-refs}")
	private String registryRefIndex;
	
	@Value("${openSearch.timeOutSeconds:60}")
	private int timeOutSeconds;
	
	@Value("${openSearch.username:}")
	private String username;
	
	@Value("${openSearch.password:}")
	private String password;
	
	@Value("${openSearch.ssl:false}")
	private boolean ssl;
    
	@Value("${openSearch.sslCertificateVerification:true}")
	private boolean sslCertificateVerification;
	
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
	
	public String getRegistryRefIndex() {
		return registryRefIndex;
	}
	
	public void setRegistryRefIndex(String registryRefIndex) {
		this.registryRefIndex = registryRefIndex;
	}
	
		
	public boolean isSsl() {
		return ssl;
	}

	public boolean doesSslCertificateVerification() {
		return sslCertificateVerification;
	}

	public void setSslCertificateVerification(boolean sslCertificateVerification) {
		this.sslCertificateVerification = sslCertificateVerification;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	private OpenSearchRegistryConnection esRegistryConnection = null;

	@Bean("esRegistryConnection")
	public OpenSearchRegistryConnection openSearchRegistryConnection() {
		
		if (esRegistryConnection == null) {

			// see if ES user name is not set - if not, try to get from environment
			if (this.username == null || "".equals(this.username)) {
				this.trySetESCredsFromEnv();
			}
			
			// do the same for ES hosts - the defaulting mechanism causes a rather elaborate
			// check
			log.debug(String.format("this.hosts : %s (%d)", this.hosts, this.hosts.size()));
            if (this.hosts == null || this.hosts.size() == 0 
             || this.hosts.get(0) == null || "".equals(this.hosts.get(0))) {
            	setESHostsFromEnvOrDefault();
            }
			
			this.esRegistryConnection = new OpenSearchRegistryConnectionImpl(this.hosts,
					this.registryIndex,
					this.registryRefIndex,
					this.timeOutSeconds,
					this.username,
					this.password,
					this.ssl,
					this.sslCertificateVerification);
		}
		return this.esRegistryConnection;
	}

	
	@Bean("productBO")
	public ProductBusinessObject ProductBusinessObject() {
		return new ProductBusinessObject(this.openSearchRegistryConnection());
	}

    
	@Bean("searchRequestBuilder")
	public RegistrySearchRequestBuilder RegistrySearchRequestBuilder() {
		
		OpenSearchRegistryConnection esRegistryConnection = this.openSearchRegistryConnection();
		
		return new RegistrySearchRequestBuilder(
     			esRegistryConnection.getRegistryIndex(),
     			esRegistryConnection.getRegistryRefIndex(),
    			esRegistryConnection.getTimeOutSeconds());
	}
    

	private void trySetESCredsFromEnv() {

		String esCredsFromEnv = System.getenv(SystemConstants.ES_CREDENTIALS_ENV_VAR);

		if (esCredsFromEnv != null && !"".equals(esCredsFromEnv)) {
			log.info("Received ES login from environment");
			DefaultKeyValue<String, String> esCreds = AWSSecretsAccess.parseSecret(esCredsFromEnv);
			if (esCreds == null) {
				String message = String.format("Value of %s environment variable is not in appropriate JSON format",
				                               SystemConstants.ES_CREDENTIALS_ENV_VAR);
				log.error(message);
				throw new RuntimeException(message);
			}

			this.username = esCreds.getKey();
			this.password = esCreds.getValue();
			log.debug(String.format("ES Username from environment : [%s]", this.username));
		}
	}

	
	private void setESHostsFromEnvOrDefault() {

		String esHosts = System.getenv(SystemConstants.ES_HOSTS_ENV_VAR);

		if (esHosts != null && !"".equals(esHosts)) {
			log.info("Received ES hosts from environment");
		} else {
			log.info(String.format("ES hosts not set in config or environment, defaulting to %s", DEFAULT_ES_HOST));
			esHosts = DEFAULT_ES_HOST;
		}
		
		log.debug(String.format("esHosts : %s", esHosts));
			
		this.hosts = List.of(esHosts.split(","));
	}

}
