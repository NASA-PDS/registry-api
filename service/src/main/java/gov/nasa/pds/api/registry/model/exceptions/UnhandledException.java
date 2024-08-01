package gov.nasa.pds.api.registry.model.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnhandledException extends RegistryApiException {

  private static final long serialVersionUID = -2108508968768896105L;
  private static final Logger log = LoggerFactory.getLogger(UnhandledException.class);


  public UnhandledException(String msg) {
    super("Unhandled Exception:" + msg);
  }

  public UnhandledException(Exception e) {
    super("Unhandled Exception: " + e.getMessage());

  }


}
