package gov.nasa.pds.api.registry.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;

import gov.nasa.pds.api.base.HealthcheckApi;

abstract class SwaggerJavaHealthcheckTransmuter extends SwaggerJavaDeprecatedTransmuter 
    implements HealthcheckApi {

  private static final Logger log = LoggerFactory.getLogger(SwaggerJavaHealthcheckTransmuter.class);

  @Override
  public ResponseEntity<Map<String, Object>> healthcheck() {
    return this.processHealthcheck();
  }
}
