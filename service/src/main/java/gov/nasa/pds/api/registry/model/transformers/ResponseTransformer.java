package gov.nasa.pds.api.registry.model.transformers;

import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.properties.FieldSelection;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;

public interface ResponseTransformer {

  public void setObjectMapper(ObjectMapper om);

  /**
   * @param userRequestedFields the fields requested by the user, null or empty if none
   * @return the properties to fetch from, and not to fetch from, OpenSearch for this transformer
   */
  public FieldSelection getFieldSelection(List<PdsProperty> userRequestedFields);

  Object transform(RawMultipleProductResponse input, List<PdsProperty> fields);

  Object transform(Map<String, Object> input, List<PdsProperty> fields);

}
