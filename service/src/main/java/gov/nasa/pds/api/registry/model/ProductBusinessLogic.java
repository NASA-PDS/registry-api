package gov.nasa.pds.api.registry.model;

import java.util.List;
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

  public void setResponse(SearchHit hit, List<String> fields);

  public int setResponse(HitIterator hits, Summary summary, List<String> fields);

  public int setResponse(SearchHits hits, Summary summary, List<String> fields);
}
