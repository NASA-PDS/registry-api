package gov.nasa.pds.api.registry.model.api_responses;

import gov.nasa.pds.api.registry.model.exceptions.UnauthorizedForwardedHostException;

public class Pds4XmlProductBusinessObject extends Pds4ProductBusinessObject {

  public Pds4XmlProductBusinessObject() throws UnauthorizedForwardedHostException {
    super(false);
  }

}
