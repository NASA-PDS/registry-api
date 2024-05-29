package gov.nasa.pds.api.registry.model.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class UnparsableQParamException extends RegistryApiException {

  private static final long serialVersionUID = -6704894264788325051L;
  private static final Logger log = LoggerFactory.getLogger(UnparsableQParamException.class);

  public UnparsableQParamException(String msg) {
    super("UnparsableQParamException: Unable to parse the q parameter:" + msg);
  }

}
