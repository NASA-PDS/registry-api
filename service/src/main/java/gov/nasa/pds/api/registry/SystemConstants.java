package gov.nasa.pds.api.registry;

public class SystemConstants {

  /*
   * Environment variables - values are retrieved using System.getEnv() under specific configuration
   * states.
   */

  // Those in this section are expected to be value-only.
  // For AWS deployments they are set through the Systems Manager Parameter Store
  public static final String ES_HOSTS_ENV_VAR = "ES_HOSTS"; // es URLs

  private SystemConstants() {
    throw new IllegalStateException("Objects of this class cannot be instantiated.");
  }

}
