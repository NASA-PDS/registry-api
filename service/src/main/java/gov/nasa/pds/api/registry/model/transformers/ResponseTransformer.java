package gov.nasa.pds.api.registry.model.transformers;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.model.api_responses.util.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.exceptions.AcceptFormatNotSupportedException;

public interface ResponseTransformer {

  public void setObjectMapper(ObjectMapper om);

  public List<String> getRequestedFields(List<String> userRequestFields);
  
  Object transformMultiple(RawMultipleProductResponse rawResponse, List<String> fields);

  Object transformSingle(Map<String, Object> kvp, List<String> fields);

}
