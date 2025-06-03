package gov.nasa.pds.api.registry.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

public class AWSSecretsAccess {


  public static final String REGISTRY_DEFAULT_AWS_REGION = "us-west-2";

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(AWSSecretsAccess.class);


  // Get the secret from the default region
  // This code is a slight modification of that provided by the AWS SecretsManager
  // console.
  public DefaultKeyValue<String, String> getSecret(String secretName) {

    // Create a Secrets Manager client
    SecretsManagerClient client = SecretsManagerClient.builder().region(Region.US_WEST_2).build();

    // In this sample we only handle the specific exceptions for the
    // 'GetSecretValue' API.
    // See
    // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
    // We rethrow the exception by default.

    GetSecretValueRequest getSecretValueRequest =
        GetSecretValueRequest.builder().secretId(secretName).build();
    GetSecretValueResponse response = null;

    try {
      log.debug("Submitting getSecretValueRequest.");
      response = client.getSecretValue(getSecretValueRequest);
    } catch (SecretsManagerException e) {
      log.error(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }

    return parseSecret(response.secretString());

  }

  // Given a String JSON representation, parse the secret key/value and return
  public static DefaultKeyValue<String, String> parseSecret(String secretString) {
    DefaultKeyValue<String, String> result = null;

    try {
      JsonNode jsonObj = objectMapper.readTree(secretString);
      String secretId = null;
      String secretValue = null;

      Iterator<String> fieldIter = jsonObj.fieldNames();
      while (fieldIter.hasNext()) {
        if (secretId != null) {
          // more than field name? This shouldn't happen
          throw new RuntimeException(
              String.format("Received multiple fields in secret value (%s)", secretString));
        }
        secretId = fieldIter.next();
        secretValue = jsonObj.get(secretId).asText();

        result = new DefaultKeyValue<String, String>(secretId, secretValue);
        log.debug("Secret string successfully parsed.");
      }
    } catch (JsonMappingException jmEx) {
      log.error("Could not parse secret JSON value (%s)", jmEx.getMessage());
      throw new RuntimeException(jmEx);
    } catch (JsonProcessingException jpEx) {
      log.error("Could not process secret JSON value (%s)", jpEx.getMessage());
      throw new RuntimeException(jpEx);
    }

    return result;
  }
}
