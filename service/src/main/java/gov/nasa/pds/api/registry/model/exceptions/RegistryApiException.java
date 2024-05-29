package gov.nasa.pds.api.registry.model.exceptions;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RegistryApiException extends Exception {
  private static final long serialVersionUID = 202822323640754980L;


  private static final Logger log = LoggerFactory.getLogger(RegistryApiException.class);


  private String uuid;

  public RegistryApiException(String msg) {

    super(msg);
    UUID uuidObj = UUID.randomUUID();
    this.uuid = uuidObj.toString();

    log.info(this.uuid + " " + msg);



  }

  public String getUuid() {
    return this.uuid;
  }

}
