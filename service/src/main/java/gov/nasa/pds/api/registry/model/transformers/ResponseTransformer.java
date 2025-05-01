package gov.nasa.pds.api.registry.model.transformers;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.registry.model.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.exceptions.AcceptFormatNotSupportedException;

public interface ResponseTransformer {

  public void setObjectMapper(ObjectMapper om);

  public List<String> getRequestedFields(List<String> userRequestFields);
  
  Object transform(RawMultipleProductResponse input, List<String> fields);
  Object transform(Map<String, Object> input, List<String> fields);

}
