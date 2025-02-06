package gov.nasa.pds.api.registry.model.exceptions;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.ServletException;



public class RegistryApiException extends ServletException {
  private static final long serialVersionUID = 2533946352056320649L;


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
