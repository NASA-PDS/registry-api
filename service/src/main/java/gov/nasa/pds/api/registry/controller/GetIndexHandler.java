package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.util.Arrays;import java.util.HashMap;import java.util.List;import java.util.Map;import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;import org.opensearch.client.RequestOptions;import org.opensearch.client.RestHighLevelClient;import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.client.indices.GetIndexResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.UserContext;

class GetIndexHandler implements EndpointHandler {
  private String indexName;

  public GetIndexHandler(String indexName) {
    if (indexName == null || indexName.strip().equals("")) {
      throw new IllegalArgumentException("Cannot initialize GetIndexHandler with null/empty index name");
    }
    this.indexName = indexName;
  }

  @Override
  public ResponseEntity<Object> transmute(ControlContext control, UserContext _ignored)
      throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    GetIndexRequest req = new GetIndexRequest(indexName);
    RestHighLevelClient client = control.getConnection().getRestHighLevelClient();;
    GetIndexResponse response = client.indices().get(req, RequestOptions.DEFAULT);
    JsonNode content = mapper.valueToTree(response.getMappings().get(indexName).getSourceAsMap()).get("properties");

    String propertiesAsString = mapper.writeValueAsString(content);

    return new ResponseEntity<>(propertiesAsString, HttpStatus.OK);
  }

}
