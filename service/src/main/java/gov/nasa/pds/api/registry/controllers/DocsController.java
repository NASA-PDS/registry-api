package gov.nasa.pds.api.registry.controllers;

import java.lang.Object;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.ArrayList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.base.DocsApi;
import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.model.ErrorMessageFactory;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.opensearch.client.opensearch.generic.OpenSearchGenericClient;
import org.opensearch.client.opensearch.generic.Requests;
import org.opensearch.client.opensearch.generic.Request;
import org.opensearch.client.opensearch.generic.Bodies;
import org.opensearch.client.opensearch.generic.Response;


@Controller
public class DocsController implements DocsApi {

  private static final Logger log = LoggerFactory.getLogger(DocsController.class);

  private final ConnectionContext connectionContext;
  private final String host;

  @Autowired
  public DocsController(ConnectionContext connectionContext) {

    this.connectionContext = connectionContext;

    this.host = connectionContext.getHost();


  }

  @Override
  public ResponseEntity<Object> docs(
      @NotNull @Parameter(name = "indices",
          description = "syntax: indices=index1,index2... notes: OpenSearch indices ",
          required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "indices",
              required = true) List<String> indices,
      @Parameter(name = "body", description = "OpenSearch DSL query",
          required = true) @Valid @RequestBody String body)
      throws Exception {

    String endPoint = this.host + "/" + String.join(",", indices) + "/_search";

    Collection<Map.Entry<String, String>> headers = new ArrayList<Map.Entry<String, String>>();
    Map.Entry<String, String> acceptHeader =
        new AbstractMap.SimpleEntry<String, String>("Accept", "application/json");
    headers.add(acceptHeader);
    // TODO develop this part again, this never worked by the way
    /*
     * Request request = Requests.create("POST", endPoint, headers, new HashMap<String, String>(),
     * Bodies.json(body));
     * 
     * 
     * OpenSearchGenericClient openSearchGenericClient =
     * this.connectionContext.getOpenSearchGenericClient(); // TODO make that work, it does not now,
     * // but I have to keep that development aside for now Response response =
     * openSearchGenericClient.execute(request);
     * 
     * 
     * log.info("Request status response is " + response.getStatus());
     * log.debug("Request response body is " + response.getBody());
     * 
     */


    return new ResponseEntity<Object>(HttpStatus.OK);

  }
}
