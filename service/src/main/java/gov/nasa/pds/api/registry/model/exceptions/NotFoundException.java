package gov.nasa.pds.api.registry.model.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotFoundException extends RegistryApiException {

  private static final long serialVersionUID = -6704894264788325051L;
  private static final Logger log = LoggerFactory.getLogger(NotFoundException.class);

  public NotFoundException(String msg) {
    super("NotFoundException: " + msg);
  }

}
