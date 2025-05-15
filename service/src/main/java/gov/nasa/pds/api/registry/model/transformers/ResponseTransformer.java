package gov.nasa.pds.api.registry.model.transformers;

import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;

public interface ResponseTransformer {

  public void setObjectMapper(ObjectMapper om);

  public List<PdsProperty> getRequestedFields(List<PdsProperty> userRequestFields);

  Object transform(RawMultipleProductResponse input, List<PdsProperty> fields);

  Object transform(Map<String, Object> input, List<PdsProperty> fields);

}
