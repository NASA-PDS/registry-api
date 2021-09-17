package gov.nasa.pds.api.engineering;

public class SystemConstants {

	/* 
	 * Environment variables - values are retrieved using System.getEnv() under specific
	 * configuration states.
	 */
	
	// Those in this section are expected to be value-only.
	// For AWS deployments they are set through the Systems Manager Parameter Store
	
	public static final String NODE_NAME_ENV_VAR = "node_name";            // node name or abbr
    public static final String ES_HOSTS_ENV_VAR = "es_endpoint";        // es URL
    
    // Those in this section are expected to have value in the key:value format.
    // For AWS deployments they are set through the Secrets Manager
    
    public static final String ES_CREDENTIALS_ENV_VAR = "es_credentials";  // es user:pwd
	
	private SystemConstants() {
	}

}
