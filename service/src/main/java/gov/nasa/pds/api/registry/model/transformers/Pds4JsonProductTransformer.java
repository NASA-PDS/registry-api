package gov.nasa.pds.api.registry.model.transformers;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import gov.nasa.pds.api.registry.model.properties.PdsPropertyConstants;

public class Pds4JsonProductTransformer extends Pds4ProductTransformer {
  private static final Logger log = LoggerFactory.getLogger(Pds4JsonProductTransformer.class);

  public Pds4JsonProductTransformer() {
    super(true);
  }

  @Override
  public List<PdsProperty> getRequestedFields(List<PdsProperty> userRequestFields) {
    log.info("User Requested Fields are ignored with this transformer");
    List<PdsProperty> fields = new ArrayList<PdsProperty>(REQUIRED_FIELDS);
    fields.add(PdsPropertyConstants.JSON_BLOB);
    return fields;

  }

}
