package gov.nasa.pds.api.registry.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
abstract class SwaggerJavaBaseTransmuter {
  protected static final Logger log = LoggerFactory.getLogger(SwaggerJavaBaseTransmuter.class);

  @Autowired
  URIParametersBuilder uriParametersBuilder;

  abstract protected ResponseEntity<Object> processs(EndpointHandler handler,
      URIParameters parameters);

  abstract protected ResponseEntity<Map<String,Object>> processHealthcheck();
}
