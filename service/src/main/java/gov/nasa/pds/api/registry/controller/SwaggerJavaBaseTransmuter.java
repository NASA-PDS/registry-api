package gov.nasa.pds.api.registry.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Min;
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

  public ResponseEntity<Object> groupReferencingId(String group, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new GroupReferencingId(),
        this.uriParametersBuilder.setGroup(group).setIdentifier(identifier).setFields(fields)
            .setLimit(limit).setSort(sort).setStart(start).build());
  }

  public ResponseEntity<Object> groupReferencingIdVers(String group, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new GroupReferencingId(),
        this.uriParametersBuilder.setGroup(group).setIdentifier(identifier).setVersion(versions)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start).build());
  }

  public ResponseEntity<Object> idReferencingGroup(String group, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new IdReferencingGroup(),
        this.uriParametersBuilder.setGroup(group).setIdentifier(identifier).setFields(fields)
            .setLimit(limit).setSort(sort).setStart(start).build());
  }

  public ResponseEntity<Object> idReferencingGroupVers(String group, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new IdReferencingGroup(),
        this.uriParametersBuilder.setGroup(group).setIdentifier(identifier).setVersion(versions)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start).build());
  }
}
