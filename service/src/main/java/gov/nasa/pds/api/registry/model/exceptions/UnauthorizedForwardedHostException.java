package gov.nasa.pds.api.registry.model.exceptions;


public class UnauthorizedForwardedHostException extends RegistryApiException {

  private static final long serialVersionUID = -6738509477244243920L;

  public UnauthorizedForwardedHostException(String message) {
    super(message);
  }
}
