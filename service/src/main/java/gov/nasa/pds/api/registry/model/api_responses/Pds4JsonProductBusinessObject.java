package gov.nasa.pds.api.registry.model.api_responses;

import gov.nasa.pds.api.registry.model.exceptions.UnauthorizedForwardedHostException;

public class Pds4JsonProductBusinessObject extends Pds4ProductBusinessObject {
  public Pds4JsonProductBusinessObject() throws UnauthorizedForwardedHostException {
    super(true);
  }
}
