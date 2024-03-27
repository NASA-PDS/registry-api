package gov.nasa.pds.api.registry.search;

import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import java.util.Set;
import java.util.ArrayList;


import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.opensearch.action.admin.cluster.settings.ClusterGetSettingsResponse;


import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.function.Factory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.opensearch.OpenSearchClient;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

import gov.nasa.pds.api.registry.ConnectionContextNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenSearchRegistryConnectionNewImpl implements ConnectionContextNew {

  // key for getting the remotes from cross cluster config
  public static String CLUSTER_REMOTE_KEY = "cluster.remote";

  private static final Logger log =
      LoggerFactory.getLogger(OpenSearchRegistryConnectionNewImpl.class);

  private PoolingAsyncClientConnectionManager connectionManager = null;
  private OpenSearchClient openSearchClient;
  private String registryIndex;
  private String registryRefIndex;
  private int timeOutSeconds;
  private ArrayList<String> crossClusterNodes;

  public OpenSearchRegistryConnectionNewImpl() throws java.security.NoSuchAlgorithmException,
      java.security.KeyStoreException, java.security.KeyManagementException {
    this(new OpenSearchRegistryConnectionImplBuilder());
  }

  @Autowired
  public OpenSearchRegistryConnectionNewImpl(
      OpenSearchRegistryConnectionImplBuilder connectionBuilder)
      throws java.security.NoSuchAlgorithmException, java.security.KeyStoreException,
      java.security.KeyManagementException {

    List<HttpHost> httpHosts = new ArrayList<HttpHost>();

    OpenSearchRegistryConnectionNewImpl.log.info("Connection to open search");
    for (String host : connectionBuilder.getHosts()) {

      List<String> hostAndPort = Splitter.on(':').splitToList(host);
      OpenSearchRegistryConnectionNewImpl.log
          .info("Host " + hostAndPort.get(0) + ":" + hostAndPort.get(1));
      log.info("Connection to host" + host);
      httpHosts.add(new HttpHost((connectionBuilder.isSsl() ? "https" : "http"), hostAndPort.get(0),
          Integer.parseInt(hostAndPort.get(1))));

    }

    // TODO develop other cases for authentication
    String username = connectionBuilder.getUsername();
    // TODO reintroduce the multiple case as needed for the AWS deployment
    // if ((username != null) && !username.equals("")) {
    OpenSearchRegistryConnectionNewImpl.log
        .info("Set openSearch connection with username/password");
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    // TODO we only take the first of the hosts to create the AuthScope
    // we should either take them all or make httpHosts a single element
    // I have no idea not why httpHosts is a list in the first place, maybe because we are
    // supposed to query a cluster.
    /*
     * credentialsProvider.setCredentials(new AuthScope(httpHosts.get(0)), new
     * UsernamePasswordCredentials(username, connectionBuilder.getPassword()));
     */
    // hardcoded to test
    char[] password = "admin".toCharArray();
    credentialsProvider.setCredentials(new AuthScope(httpHosts.get(0)),
        new UsernamePasswordCredentials("admin", password));
    // }

    final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder
        .builder(httpHosts.toArray(new HttpHost[httpHosts.size()]));

    final SSLContext sslContext =
        SSLContextBuilder.create().loadTrustMaterial(null, (chains, authType) -> true).build();

    builder.setHttpClientConfigCallback(httpClientBuilder -> {

      PoolingAsyncClientConnectionManagerBuilder connectionManagerBuilder =
          PoolingAsyncClientConnectionManagerBuilder.create();

      if (connectionBuilder.isSsl()) {
        OpenSearchRegistryConnectionNewImpl.log.info("Connection over SSL");


        ClientTlsStrategyBuilder clientTlsStrategyBuilder =
            ClientTlsStrategyBuilder.create().setSslContext(sslContext)
                // See https://issues.apache.org/jira/browse/HTTPCLIENT-2219
                .setTlsDetailsFactory(new Factory<SSLEngine, TlsDetails>() {
                  @Override
                  public TlsDetails create(final SSLEngine sslEngine) {
                    return new TlsDetails(sslEngine.getSession(),
                        sslEngine.getApplicationProtocol());
                  }
                });

        if (!connectionBuilder.isSslCertificateCNVerification()) {
          clientTlsStrategyBuilder.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }
        final TlsStrategy tlsStrategy = clientTlsStrategyBuilder.build();
        connectionManagerBuilder = connectionManagerBuilder.setTlsStrategy(tlsStrategy);

      }

      this.connectionManager = connectionManagerBuilder.build();

      return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
          .setConnectionManager(connectionManager);
    });

    final OpenSearchTransport transport = builder.build();
    this.openSearchClient = new OpenSearchClient(transport);


    String registryIndex = connectionBuilder.getRegistryIndex();
    if (connectionBuilder.getCCSEnabled()) {
      // TODO something different need to be done to add all the indices for all the nodes hosted in
      // the multitenant OpenSearch
      this.crossClusterNodes = checkCCSConfig();
      this.registryIndex = createCCSIndexString(registryIndex);
    } else {
      this.registryIndex = registryIndex;
    }

    this.registryRefIndex =

        createCCSIndexString(connectionBuilder.getRegistryRefIndex());
    this.timeOutSeconds = connectionBuilder.getTimeOutSeconds();

  }


  public OpenSearchClient getOpenSearchClient() {
    return this.openSearchClient;
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
    // returns all the indices from the nodes
    ArrayList<String> result = null;

    // TODO: get all the indices which following a pattern related to the registry_index and
    // registry_ref_index value.
    return result;
  }

  // if CCS configuration has been detected, use nodes in consolidated index
  // names, otherwise just return the index
  private String createCCSIndexString(String indexName) {
    String result = indexName;
    if (this.crossClusterNodes != null) {
      // TODO create the names of the index
    }

    return result;
  }

  public void close() {
    try {
      // TODO verify if that is what we want to do here...
      this.connectionManager.close();
    } catch (Exception ex) {
      // Ignore
    }
  }
}
