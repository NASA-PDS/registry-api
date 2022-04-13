package gov.nasa.pds.api.registry.opensearch;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nasa.pds.api.registry.RegistryContext;
import gov.nasa.pds.api.registry.SystemConstants;
import gov.nasa.pds.api.registry.business.ProductBusinessObject;
import gov.nasa.pds.api.registry.configuration.AWSSecretsAccess;
import gov.nasa.pds.api.registry.search.RegistryContextImpl;
import gov.nasa.pds.api.registry.search.RegistrySearchRequestBuilder;
import gov.nasa.pds.api.registry.opensearch.OpenSearchRegistryConnectionImplBuilder;

/* Keep this eventhough not directly referenced
 * 
 * Seems to found and used via reflection rather than direct reference. When removed
 * it causes spring to fail with an opensearch failure requiring EleasticSearchRegistryConnection.
 * Not sure why the error indicates an interface that is already there when this class is missing
 * but that is what happened.
 */

@Configuration 
public class OpenSearchConfig
{ 	
	private static final Logger log = LoggerFactory.getLogger(OpenSearchConfig.class);

	// This default for ES hosts is set in the constructor since we first want to check
	// the environment if not set in the application properties. This preserves the
	// original behavior when the default was specified in the Value annotation.
	
	
	@Value("#{'${openSearch.host:}'.split(',')}") 
	private List<String> hosts;
	
	@Value("${openSearch.registryIndex:registry}")
	private String registryIndex;
	
	@Value("${openSearch.registryRefIndex:registry-refs}")
	private String registryRefIndex;
	
	@Value("${openSearch.timeOutSeconds:60}")
	private int timeOutSeconds;
	
	public int getTimeOutSeconds() {
		return timeOutSeconds;
	}

	public void setTimeOutSeconds(int timeOutSeconds) {
		this.timeOutSeconds = timeOutSeconds;
	}

	@Value("${openSearch.username:}")
	private String username;
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Value("${openSearch.password:}")
	private String password;
	
	@Value("${openSearch.ssl:false}")
	private boolean ssl;
    
	@Value("${openSearch.sslCertificateCNVerification:false}")
	private boolean sslCertificateCNVerification;
	
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

	public boolean doesSslCertificateVCNerification() {
		return sslCertificateCNVerification;
	}

	public void setSslCertificateCNVerification(boolean sslCertificateCNVerification) {
		this.sslCertificateCNVerification = sslCertificateCNVerification;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	private OpenSearchRegistryConnection esRegistryConnection = null;

	@Bean("esRegistryConnection")
	public OpenSearchRegistryConnection openSearchRegistryConnection() {
		
		if (esRegistryConnection == null) {
			
			OpenSearchRegistryConnectionImplBuilder connectionBuilder = new OpenSearchRegistryConnectionImplBuilder(this);

			// see if ES user name is not set - if not, try to get from environment
			if (this.username == null || "".equals(this.username)) {
				connectionBuilder.trySetESCredsFromEnv();
			}
			
			// do the same for ES hosts - the defaulting mechanism causes a rather elaborate
			// check
			log.debug(String.format("this.hosts : %s (%d)", this.hosts, this.hosts.size()));
            if (this.hosts == null || this.hosts.size() == 0 
             || this.hosts.get(0) == null || "".equals(this.hosts.get(0))) {
            	connectionBuilder.setESHostsFromEnvOrDefault();
            }
			
			this.esRegistryConnection = new OpenSearchRegistryConnectionImpl(connectionBuilder);

		}
		return this.esRegistryConnection;
	}

	@Bean("searchRequestBuilder")
	public RegistryContext RegistrySearchRequestBuilder() {
		
		OpenSearchRegistryConnection esRegistryConnection = this.openSearchRegistryConnection();
		
		return new RegistryContextImpl(
     			esRegistryConnection.getRegistryIndex(),
     			esRegistryConnection.getRegistryRefIndex(),
    			esRegistryConnection.getTimeOutSeconds());
	}
}
