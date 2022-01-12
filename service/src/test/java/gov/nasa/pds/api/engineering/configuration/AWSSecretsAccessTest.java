package gov.nasa.pds.api.engineering.configuration;


import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import gov.nasa.pds.api.engineering.configuration.AWSSecretsAccess;

public class AWSSecretsAccessTest {

	private static final Logger log = LoggerFactory.getLogger(AWSSecretsAccessTest.class);
	private static final AWSSecretsAccess secretAccess = new AWSSecretsAccess();
	
	static DefaultKeyValue<String, String>  testGetSecret(String secretName) {
		DefaultKeyValue<String, String> result = null;
		
		try {
			
			result = secretAccess.getSecret(secretName);
		}
		catch(Exception e) {
			log.error("Exception accessing secret %s", e);
			throw e;
		}
		
		return result;
	}
	
	public static final void main(String args[]) {
        for (String secretName : args) {
        	DefaultKeyValue<String, String> secret = testGetSecret(secretName);
        	if (secret != null ) {
            	log.info(String.format("Secret name : %s, Secret Id : %s / Secret Value : %s", secretName, secret.getKey(), secret.getValue()));        		
        	} else {
        		log.error(String.format("Lookup for secrete %s returned null.", secretName));
        	}
        }
	}
}
