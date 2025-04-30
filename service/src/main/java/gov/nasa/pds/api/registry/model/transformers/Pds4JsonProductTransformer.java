package gov.nasa.pds.api.registry.model.transformers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;

public class Pds4JsonProductTransformer extends Pds4ProductTransformer {
  private static final Logger log = LoggerFactory.getLogger(Pds4JsonProductTransformer.class);

  public Pds4JsonProductTransformer() {
    super(true);
  }

  @Override
  public List<String> getRequestedFields(List<String> userRequestFields) {
    log.info("User Requested Fields are ignored with this transformer");
    List<String> fields = new ArrayList<>(REQUIRED_FIELDS);
    fields.add(PdsProperty.JSON_BLOB);
    return fields;

  }

} 