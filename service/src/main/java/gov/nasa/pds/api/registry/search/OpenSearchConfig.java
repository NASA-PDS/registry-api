package gov.nasa.pds.api.registry.search;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/*
 * Keep this eventhough not directly referenced
 * 
 * Seems to found and used via reflection rather than direct reference. When removed it causes
 * spring to fail with an opensearch failure requiring EleasticSearchRegistryConnection. Not sure
 * why the error indicates an interface that is already there when this class is missing but that is
 * what happened.
 */

@Configuration
public class OpenSearchConfig {
  private static final Logger log = LoggerFactory.getLogger(OpenSearchConfig.class);

  // This default for ES hosts is set in the constructor since we first want to
  // check
  // the environment if not set in the application properties. This preserves the
  // original behavior when the default was specified in the Value annotation.

  @Value("#{'${openSearch.host:}'.split(',')}")
  private List<String> hosts;

  @Value("${openSearch.registryIndex:registry}")
  private String registryIndex;

  @Value("${openSearch.registryRefIndex:registry-refs}")
  private String registryRefIndex;

  @Value("#{'${openSearch.disciplineNodes:}'.split(',')}")
  private List<String> disciplineNodes;

  @Value("${openSearch.timeOutSeconds:60}")
  private int timeOutSeconds;

  public int getTimeOutSeconds() {
    return timeOutSeconds;
  }

  public void setTimeOutSeconds(int timeOutSeconds) {
    this.timeOutSeconds = timeOutSeconds;
  }

  @Value("${openSearch.CCSEnabled:true}")
  private boolean CCSEnabled;

  public boolean getCCSEnabled() {
    return CCSEnabled;
  }

  @Value("${openSearch.username:}")
  private String username;

  @Value("#{'${filter.archiveStatus:}'.split(',')}")
  private List<String> archiveStatus;

  public List<String> getArchiveStatus() {
    return archiveStatus;
  }

  public char[] getPassword() {
    return password;
  }

  public void setPassword(char[] password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Value("${openSearch.password:}")
  private char[] password;

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

  public List<String> getDisciplineNodes() {
    return disciplineNodes;
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



}
