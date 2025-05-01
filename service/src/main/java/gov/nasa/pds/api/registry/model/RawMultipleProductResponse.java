package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensearch.client.opensearch.core.SearchResponse;
import gov.nasa.pds.model.Summary;

public class RawMultipleProductResponse {
  private Summary summary;
  private List<Map<String, Object>> products;

  public RawMultipleProductResponse(SearchResponse<HashMap> searchResponse) {
    this.summary = new Summary();
    this.summary.setHits((int) searchResponse.hits().total().value());
    this.products = searchResponse.hits().hits().stream().map(p -> (Map<String, Object>) p.source())
        .collect(Collectors.toList());

  }

  public RawMultipleProductResponse(HashMap<String, Object> product) {
    this.summary = new Summary();
    this.summary.setHits(1);
    this.products = new ArrayList<Map<String, Object>>();
    this.products.add(product);

  }



  public Summary getSummary() {
    return summary;
  }

  public List<Map<String, Object>> getProducts() {
    return products;
  }


}
