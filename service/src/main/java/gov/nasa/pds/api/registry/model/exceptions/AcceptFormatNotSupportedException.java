package gov.nasa.pds.api.registry.model.exceptions;

public class AcceptFormatNotSupportedException extends RegistryApiException {
  private static final long serialVersionUID = -2118295915069330607L;

  public AcceptFormatNotSupportedException(String msg) {
    super("AcceptFormatNotSupportedException: " + msg);
  }

}
