package gov.nasa.pds.api.registry.model.api_responses;

import java.util.List;
import java.util.Map;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.model.Summary;

public interface ProductBusinessLogic {
  public String[] getMinimallyRequiredFields();

  public String[] getMaximallyRequiredFields();

  public Object getResponse();

  public void setObjectMapper(ObjectMapper om);

  public void setResponse(Map<String, Object> hit, List<String> fields);

  public void setResponse(List<Map<String, Object>> hits, Summary summary, List<String> fields);

}
