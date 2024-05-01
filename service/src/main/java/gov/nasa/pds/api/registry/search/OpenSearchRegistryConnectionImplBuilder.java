package gov.nasa.pds.api.registry.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import gov.nasa.pds.api.registry.SystemConstants;
import gov.nasa.pds.api.registry.util.AWSCredentialsFetcher;

@Component
class OpenSearchRegistryConnectionImplBuilder {

  private static final String DEFAULT_ES_HOST = "localhost:9200";

  private static final Logger log =
      LoggerFactory.getLogger(OpenSearchRegistryConnectionImplBuilder.class);

  private List<String> hosts;


  private final String registryIndex;
  private final String registryRefIndex;
  private final int timeOutSeconds;
  private final boolean CCSEnabled;
  private final List<String> disciplineNodes;
  private final boolean ssl;
  private final boolean sslCertificateCNVerification;
  private String username;
  private char[] password;
  private final List<String> archiveStatus;

  public List<String> getArchiveStatus() {
    return archiveStatus;
  }

  public List<String> getHosts() {
    return hosts;
  }

  public void setHosts(List<String> hosts) {
    this.hosts = hosts;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public char[] getPassword() {
    return password;
  }

  public void setPassword(char[] password) {
    this.password = password;
  }

  public String getRegistryIndex() {
    return registryIndex;
  }

  public String getRegistryRefIndex() {
    return registryRefIndex;
  }

  public List<String> getDisciplineNodes() {
    return disciplineNodes;
  }

  public int getTimeOutSeconds() {
    return timeOutSeconds;
  }


  public boolean getCCSEnabled() {
    return CCSEnabled;
  }

  public boolean isSsl() {
    return ssl;
  }

  public boolean isSslCertificateCNVerification() {
    return sslCertificateCNVerification;
  }



  public OpenSearchRegistryConnectionImplBuilder() {
    // Default builder
    this.hosts = Arrays.asList("localhost:9200");
    this.registryIndex = "registry";
    this.registryRefIndex = "registry-refs";
    this.timeOutSeconds = 5;
    this.CCSEnabled = true;
    this.disciplineNodes = Arrays.asList(new String[] {""});
    this.username = null;
    this.password = null;
    this.ssl = false;
    this.sslCertificateCNVerification = true;
    this.archiveStatus = null;

  }

  @Autowired
  public OpenSearchRegistryConnectionImplBuilder(OpenSearchConfig openSearchConfig) {

    this.hosts = openSearchConfig.getHosts();

    if (this.hosts == null || this.hosts.size() == 0 || this.hosts.get(0) == null
        || "".equals(this.hosts.get(0))) {
      this.setESHostsFromEnvOrDefault();
    }

    this.registryIndex = openSearchConfig.getRegistryIndex();
    this.registryRefIndex = openSearchConfig.getRegistryRefIndex();
    this.timeOutSeconds = openSearchConfig.getTimeOutSeconds();
    this.CCSEnabled = openSearchConfig.getCCSEnabled();
    this.disciplineNodes = openSearchConfig.getDisciplineNodes();
    this.ssl = openSearchConfig.isSsl();
    this.sslCertificateCNVerification = openSearchConfig.doesSslCertificateVCNerification();
    this.username = openSearchConfig.getUsername();
    this.password = openSearchConfig.getPassword();
    this.archiveStatus = openSearchConfig.getArchiveStatus();

    this.awsCredentialsFetcher().fetchCredentials();


  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public AWSCredentialsFetcher awsCredentialsFetcher() {
    return new AWSCredentialsFetcher();
  }

  public void setESHostsFromEnvOrDefault() {

    String esHosts = System.getenv(SystemConstants.ES_HOSTS_ENV_VAR);

    if (esHosts != null && !"".equals(esHosts)) {
      log.info("Received ES hosts from environment");
    } else {
      log.info(String.format("ES hosts not set in config or environment, defaulting to %s",
          DEFAULT_ES_HOST));
      esHosts = DEFAULT_ES_HOST;
    }

    log.debug(String.format("esHosts : %s", esHosts));

    this.hosts = List.of(esHosts.split(","));
  }

}
