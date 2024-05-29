package gov.nasa.pds.api.registry.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import gov.nasa.pds.api.base.HealthcheckApi;

@Controller
public class HealthCheckController implements HealthcheckApi {

  @Override
  public ResponseEntity<Map<String, Object>> healthcheck() {
    // To Be Completed
    return new ResponseEntity<>(HttpStatus.OK);

  }

}
