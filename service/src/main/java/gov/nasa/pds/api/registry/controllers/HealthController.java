package gov.nasa.pds.api.registry.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import gov.nasa.pds.api.base.HealthApi;

@Controller
public class HealthController implements HealthApi {

  @Override
  public ResponseEntity<Map<String, Object>> health() {
    Map<String, Object> response = Map.of("status", "ok");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
