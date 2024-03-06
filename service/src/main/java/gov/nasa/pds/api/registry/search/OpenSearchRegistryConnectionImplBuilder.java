package gov.nasa.pds.api.registry.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.SystemConstants;
import gov.nasa.pds.api.registry.configuration.AWSSecretsAccess;

class OpenSearchRegistryConnectionImplBuilder {

  private static final String DEFAULT_ES_HOST = "localhost:9200";

  private static final Logger log =
      LoggerFactory.getLogger(OpenSearchRegistryConnectionImplBuilder.class);

  private List<String> hosts;

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRegistryIndex() {
    return registryIndex;
  }

  public String getRegistryRefIndex() {
    return registryRefIndex;
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

  private final String registryIndex;
  private final String registryRefIndex;
  private final int timeOutSeconds;
  private final boolean CCSEnabled;
  private final boolean ssl;
  private final boolean sslCertificateCNVerification;

  private String username;
  private String password;

  public OpenSearchRegistryConnectionImplBuilder() {
    // Default builder
    this.hosts = Arrays.asList("localhost:9200");
    this.registryIndex = "registry";
    this.registryRefIndex = "registry-refs";
    this.timeOutSeconds = 5;
    this.CCSEnabled = true;
    this.username = null;
    this.password = null;
    this.ssl = false;
    this.sslCertificateCNVerification = true;

  }

  public OpenSearchRegistryConnectionImplBuilder(OpenSearchConfig openSearchConfig) {

    this.hosts = openSearchConfig.getHosts();
    this.registryIndex = openSearchConfig.getRegistryIndex();
    this.registryRefIndex = openSearchConfig.getRegistryRefIndex();
    this.timeOutSeconds = openSearchConfig.getTimeOutSeconds();
    this.CCSEnabled = openSearchConfig.getCCSEnabled();
    this.ssl = openSearchConfig.isSsl();
    this.sslCertificateCNVerification = openSearchConfig.doesSslCertificateVCNerification();
    this.username = openSearchConfig.getUsername();
    this.password = openSearchConfig.getPassword();

  }

  public void trySetESCredsFromEnv() {

    String esCredsFromEnv = System.getenv(SystemConstants.ES_CREDENTIALS_ENV_VAR);

    if (esCredsFromEnv != null && !"".equals(esCredsFromEnv)) {
      log.info("Received ES login from environment");
      DefaultKeyValue<String, String> esCreds = AWSSecretsAccess.parseSecret(esCredsFromEnv);
      if (esCreds == null) {
        String message =
            String.format("Value of %s environment variable is not in appropriate JSON format",
                SystemConstants.ES_CREDENTIALS_ENV_VAR);
        log.error(message);
        throw new RuntimeException(message);
      }

      this.username = esCreds.getKey();
      this.password = esCreds.getValue();
    }
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
