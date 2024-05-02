package gov.nasa.pds.api.registry.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;


class AWSCredentials {
  @JsonProperty("RoleArn")
  String roleArn;

  @JsonProperty("AccessKeyId")
  String accessKeyId;

  @JsonProperty("SecretAccessKey")
  String secretAccessKey;

  @JsonProperty("Token")
  String token;

  @JsonProperty("Expiration")
  String expiration;

  public String getRoleArn() {
    return roleArn;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public String getSecretAccessKey() {
    return secretAccessKey;
  }

  public String getToken() {
    return token;
  }

  public String getExpiration() {
    return expiration;
  }


}


@Configuration
@EnableScheduling
public class AWSCredentialsFetcher {

  private static final Logger log = LoggerFactory.getLogger(AWSCredentialsFetcher.class);

  private final String AWS_CONTAINER_CREDENTIALS_RELATIVE_URI =
      "AWS_CONTAINER_CREDENTIALS_RELATIVE_URI";
  private final String CREDENTIAL_SERVER_IP = "169.254.170.2";


  @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.HOURS)
  public void fetchCredentials() {

    String credentialRelativeURI = System.getenv(this.AWS_CONTAINER_CREDENTIALS_RELATIVE_URI);

    if (credentialRelativeURI != null) {
      try {
        log.info("Getting/Renewing AWS Credentials");
        String credentialURL = "http://" + this.CREDENTIAL_SERVER_IP + credentialRelativeURI;
        URL url = new URL(credentialURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream credentialStream = con.getInputStream();
        AWSCredentials awsCredentials =
            new ObjectMapper().readValue(credentialStream, AWSCredentials.class);
        Properties awsCredProperties = new Properties(System.getProperties());
        awsCredProperties.setProperty("aws.accessKeyId", awsCredentials.getAccessKeyId());
        awsCredProperties.setProperty("aws.secretAccessKey", awsCredentials.getSecretAccessKey());
        awsCredProperties.setProperty("aws.sessionToken", awsCredentials.getToken());
        System.setProperties(awsCredProperties);
      } catch (IOException e) {
        log.error("Unable to get or renew AWS Credentials", e);
      }

    } else {
      log.info("Nothing to do, we don't need AWS Credentials");
    }


  }


}
