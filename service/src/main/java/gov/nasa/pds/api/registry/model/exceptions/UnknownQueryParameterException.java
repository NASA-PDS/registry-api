package gov.nasa.pds.api.registry.model.exceptions;


public class UnknownQueryParameterException extends RegistryApiException {
  private static final long serialVersionUID = -5000830504052942232L;

  public UnknownQueryParameterException(String message) {
    super(message);
  }
}
