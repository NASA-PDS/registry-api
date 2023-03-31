package gov.nasa.pds.api.registry;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ControlContext {
  public ObjectMapper getObjectMapper();

  public ConnectionContext getConnection();
}
