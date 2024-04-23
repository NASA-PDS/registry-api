package gov.nasa.pds.api.registry.search;

import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

import java.util.stream.Collectors;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
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
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.InfoRequest;
import org.opensearch.client.opensearch.core.InfoResponse;
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
  public static List<String> SINGLE_EMPTY_STRING = Arrays.asList("");

  private static final Logger log =
      LoggerFactory.getLogger(OpenSearchRegistryConnectionNewImpl.class);

  private PoolingAsyncClientConnectionManager connectionManager = null;
  private OpenSearchClient openSearchClient;
  private List<String> registryIndices = new ArrayList<String>();
  private List<String> registryRefIndices = new ArrayList<String>();
  private int timeOutSeconds;
  private ArrayList<String> crossClusterNodes;

  public OpenSearchRegistryConnectionNewImpl() throws java.security.NoSuchAlgorithmException,
      java.security.KeyStoreException, java.security.KeyManagementException {
    this(new OpenSearchRegistryConnectionImplBuilder());
  }

  private HttpAsyncClientBuilder clientBuilderForHttpTransport(HttpAsyncClientBuilder builder,
      String userName, char[] password, HttpHost host, boolean ssl,
      boolean sslCertificateCNVerification) {

    try {
      PoolingAsyncClientConnectionManagerBuilder connectionManagerBuilder =
          PoolingAsyncClientConnectionManagerBuilder.create();

      final SSLContext sslContext =
          SSLContextBuilder.create().loadTrustMaterial(null, (chains, authType) -> true).build();

      if (ssl) {
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

        if (!sslCertificateCNVerification) {
          clientTlsStrategyBuilder.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }
        final TlsStrategy tlsStrategy = clientTlsStrategyBuilder.build();
        connectionManagerBuilder = connectionManagerBuilder.setTlsStrategy(tlsStrategy);

      }

      this.connectionManager = connectionManagerBuilder.build();

      if ((userName != null) && !userName.equals("")) {
        OpenSearchRegistryConnectionNewImpl.log
            .info("Set openSearch connection with username/password");
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();


        credentialsProvider.setCredentials(new AuthScope(host),
            new UsernamePasswordCredentials(userName, password));

        builder = builder.setDefaultCredentialsProvider(credentialsProvider);
      }

      return builder.setConnectionManager(connectionManager);

    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
      log.error("Ssl issue while connecting to openSearch" + e.getMessage());
      return null;
    }
  }

  private void setIndices(List<String> disciplineNodes, String registrySuffix,
      String registryRefsSuffix) {
    // create indices strings from discipline nodes and index suffixes
    log.info("Use disipline nodes: " + String.join(",", disciplineNodes) + "End discipline nodes");
    String prefix;
    for (String disciplineNode : disciplineNodes) {
      prefix = (disciplineNode.length() != 0) ? disciplineNode + "-" : "";
      this.registryIndices.add(prefix + registrySuffix);
      this.registryRefIndices.add(prefix + registryRefsSuffix);
    }
    log.debug("Use registry indices:" + String.join(",", this.registryIndices) + "End indices");
    log.debug(
        "Use registryRef indices:" + String.join(",", this.registryRefIndices) + "End indices");
  }

  private OpenSearchTransport getLocalTransport(List<HttpHost> httpHosts, Boolean ssl,
      Boolean sslCertificateCNVerification, String username, char[] password) {



    // TODO we only take the first of the hosts to create the AuthScope
    // we should either take them all or make httpHosts a single element
    // I have no idea not why httpHosts is a list in the first place, maybe because we are
    // supposed to query a cluster.

    final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder
        .builder(httpHosts.toArray(new HttpHost[httpHosts.size()]));


    builder.setHttpClientConfigCallback(httpClientBuilder -> {
      return this.clientBuilderForHttpTransport(httpClientBuilder, username, password,
          httpHosts.get(0), ssl, sslCertificateCNVerification);
    });

    return builder.build();
  }


  private OpenSearchTransport getAWSTransport(String host) {
    SdkHttpClient httpClient = ApacheHttpClient.builder().build();
    log.info("trying to connect to host:" + host);
    return new AwsSdk2Transport(httpClient, host, "aoss", Region.US_WEST_2,
        AwsSdk2TransportOptions.builder().build());

  }

  @Autowired
  public OpenSearchRegistryConnectionNewImpl(
      OpenSearchRegistryConnectionImplBuilder connectionBuilder)
      throws java.security.NoSuchAlgorithmException, java.security.KeyStoreException,
      java.security.KeyManagementException {


    List<HttpHost> httpHosts = new ArrayList<HttpHost>();

    OpenSearchRegistryConnectionNewImpl.log.info("Connection to open search");

    final OpenSearchTransport transport;

    // test if we are running on ECS,
    String env_value = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
    if (env_value == null) {
      // if environment variable does not exist,
      // means we are trying to reach a regular OpenSearch
      for (String host : connectionBuilder.getHosts()) {

        List<String> hostAndPort = Splitter.on(':').splitToList(host);
        OpenSearchRegistryConnectionNewImpl.log
            .info("Host " + hostAndPort.get(0) + ":" + hostAndPort.get(1));
        log.info("Connection to host" + host);
        httpHosts.add(new HttpHost((connectionBuilder.isSsl() ? "https" : "http"),
            hostAndPort.get(0), Integer.parseInt(hostAndPort.get(1))));

      }

      transport = getLocalTransport(httpHosts, connectionBuilder.isSsl(),
          connectionBuilder.isSslCertificateCNVerification(), connectionBuilder.getUsername(),
          connectionBuilder.getPassword());
    } else {
      // otherwise, we try to connect to AWS opensearch serverless.
      String host = connectionBuilder.getHosts().get(0);
      transport = this.getAWSTransport(host);
    }



    this.openSearchClient = new OpenSearchClient(transport);

    /*
     * fails with error 404 InfoResponse info = this.openSearchClient.info();
     * log.info("OpenSearch client info: " + info.version().distribution() + ": " +
     * info.version().number()); log.info("OpenSearch client info, cluster name: " +
     * info.clusterName()); log.info("OpenSearch client info, cluster uuid: " + info.clusterUuid());
     * log.info("OpenSearch client info, api name: " + info.name());
     * log.info("OpenSearch client info, tagline: " + info.tagline());
     */

    this.setIndices(connectionBuilder.getDisciplineNodes(), connectionBuilder.getRegistryIndex(),
        connectionBuilder.getRegistryRefIndex());

    this.timeOutSeconds = connectionBuilder.getTimeOutSeconds();


  }


  public OpenSearchClient getOpenSearchClient() {
    return this.openSearchClient;
  }

  public List<String> getRegistryIndices() {
    return registryIndices;
  }

  public void setRegistryIndices(List<String> registryRefIndices) {
    this.registryRefIndices = registryRefIndices;
  }

  public List<String> getRegistryRefIndices() {
    return registryRefIndices;
  }

  public void setRegistryRefIndices(List<String> registryRefIndices) {
    this.registryRefIndices = registryRefIndices;
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
