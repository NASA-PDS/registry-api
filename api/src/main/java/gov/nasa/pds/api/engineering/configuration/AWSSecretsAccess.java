package gov.nasa.pds.api.engineering.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;

public class AWSSecretsAccess {

	public static final String REGISTRY_DEFAULT_AWS_REGION = "us-west-2";

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log = LoggerFactory.getLogger(AWSSecretsAccess.class);

	// Get the secret using the default region
	public DefaultKeyValue<String,String> getSecret(String secretName) {
		return getSecret(secretName, null);
	}
	
	// Get the secret from an explicit region
	// This code is a slight modification of that provided by the AWS SecretsManager console.
	public DefaultKeyValue<String, String> getSecret(String secretName, String region) {

		if (region == null || "".equals(region)) {
			region = REGISTRY_DEFAULT_AWS_REGION;
		}
		
		log.debug(String.format("Looking up secret %s in region %s", secretName, region));
		
	    // Create a Secrets Manager client
	    AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
	                                    .withRegion(region)
	                                    .build();
	    
	    // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
	    // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
	    // We rethrow the exception by default.
	    
	    GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
	    GetSecretValueResult getSecretValueResult = null;

	    try {
		    log.debug("Submitting getSecretValueRequest.");
	        getSecretValueResult = client.getSecretValue(getSecretValueRequest);
	    }
	    catch (DecryptionFailureException e) {
	        // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
	        // Deal with the exception here, and/or rethrow at your discretion.
	    	log.error("DecryptionFailureException (%s)", e.getMessage());
	        throw e;
	    }
	    catch (InternalServiceErrorException e) {
	        // An error occurred on the server side.
	        // Deal with the exception here, and/or rethrow at your discretion.
	    	log.error("InternalServiceErrorException (%s)", e.getMessage());
	        throw e;
	    }
	    catch (InvalidParameterException e) {
	        // You provided an invalid value for a parameter.
	        // Deal with the exception here, and/or rethrow at your discretion.
	    	log.error("InvalidParameterException (%s)", e.getMessage());
	        throw e;
	    }
	    catch (InvalidRequestException e) {
	        // You provided a parameter value that is not valid for the current state of the resource.
	        // Deal with the exception here, and/or rethrow at your discretion.
	    	log.error("InvalidRequestException (%s)", e.getMessage());
	        throw e;
	    }
	    catch (ResourceNotFoundException e) {
	        // We can't find the resource that you asked for.
	        // Deal with the exception here, and/or rethrow at your discretion.
	    	log.error("ResourceNotFoundException (%s)", e.getMessage());
	    	throw e;
	    }

	    return parseSecret(getSecretValueResult.getSecretString());

	}
	
	// Given a String JSON representation, parse the secret key/value and return 
	public DefaultKeyValue<String, String> parseSecret(String secretString) 
	{
		DefaultKeyValue<String, String> result = null;
		
		try {
			JsonNode jsonObj = objectMapper.readTree(secretString);
			String secretId = null;
			String secretValue = null;
			
			Iterator<String> fieldIter = jsonObj.fieldNames();
			while (fieldIter.hasNext()) {
				if(secretId != null) {
					// more than field name? This shouldn't happen
					throw new RuntimeException(String.format("Received multiple fields in secret lookup request (%s)", secretString));
				}
				secretId = fieldIter.next();
				secretValue = jsonObj.get(secretId).asText();
				
				result = new DefaultKeyValue<String, String>(secretId, secretValue);
				log.debug("Secret string successfully parsed.");
			}
		}
		catch(JsonMappingException jmEx) {
			log.error("Could not parse return secret JSON value (%s)", jmEx.getMessage());
			throw new RuntimeException(jmEx);
		}
		catch(JsonProcessingException jpEx) {
			log.error("Could not process returned secret JSON value (%s)", jpEx.getMessage());
			throw new RuntimeException(jpEx);
		}
		
		return result;
	}
}
