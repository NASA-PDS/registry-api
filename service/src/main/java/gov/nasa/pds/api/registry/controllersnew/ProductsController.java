package gov.nasa.pds.api.registry.controllersnew;

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.ConnectionContextNew;
import gov.nasa.pds.api.registry.model.ErrorMessageFactory;
import gov.nasa.pds.api.registry.model.api_responses.PdsProductBusinessObject;
import gov.nasa.pds.api.registry.model.api_responses.ProductBusinessLogic;
import gov.nasa.pds.api.registry.model.api_responses.ProductBusinessLogicImpl;
import gov.nasa.pds.api.registry.model.api_responses.WyriwygBusinessObject;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;

@Controller
public class ProductsController implements ProductsApi {

  private static final Logger log = LoggerFactory.getLogger(ProductsController.class);

  private final ConnectionContextNew connectionContext;
  private final ErrorMessageFactory errorMessageFactory;
  private final ObjectMapper objectMapper;
  private SearchRequest presetSearchRequest;

  // TODO move that at a better place, it is not specific to this controller
  private Map<String, Class<? extends ProductBusinessLogic>> formatters =
      new HashMap<String, Class<? extends ProductBusinessLogic>>();


  @Autowired
  public ProductsController(ConnectionContextNew connectionContext,
      ErrorMessageFactory errorMessageFactory, ObjectMapper objectMapper) {

    this.connectionContext = connectionContext;
    this.errorMessageFactory = errorMessageFactory;
    this.objectMapper = objectMapper;

    List<String> registryIndices = this.connectionContext.getRegistryIndices();
    log.info("Use indices: " + String.join(",", registryIndices) + "End indices");
    SearchRequest.Builder searchRequestConstantBuilder =
        new SearchRequest.Builder().index(registryIndices);


    // complete with other preset criteria for the current controller
    this.presetSearchRequest = searchRequestConstantBuilder.build();

    // TODO move that at a better place, it is not specific to this controller
    // this.formatters.put("*", new PdsProductBusinessObject());
    // this.formatters.put("*/*", new PdsProductBusinessObject());
    this.formatters.put("application/csv", WyriwygBusinessObject.class);
    this.formatters.put("application/json", PdsProductBusinessObject.class);
    // this.formatters.put("application/kvp+json", new WyriwygBusinessObject());
    // this.formatters.put("application/vnd.nasa.pds.pds4+json", new
    // Pds4ProductBusinessObject(true));
    // this.formatters.put("application/vnd.nasa.pds.pds4+xml", new
    // Pds4ProductBusinessObject(false));
    // this.formatters.put("application/xml", new PdsProductBusinessObject());
    // this.formatters.put("text/csv", new WyriwygBusinessObject());
    // this.formatters.put("text/html", new PdsProductBusinessObject());
    // this.formatters.put("text/xml", new PdsProductBusinessObject());

  }

  private ResponseEntity<Object> formatSingleProduct(HashMap<String, Object> product,
      List<String> fields) {
    // TODO add case when Accept is not available, default application/json
    HttpServletRequest curRequest =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String acceptHeaderValue = curRequest.getHeader("Accept");

    Class<? extends ProductBusinessLogic> formatterClass = this.formatters.get(acceptHeaderValue);

    try {
      // TODO replace URLs from the request path
      ProductBusinessLogicImpl formatter =
          (ProductBusinessLogicImpl) formatterClass.getConstructor().newInstance();
      // TODO check if that is applicable to all formatters.
      // Would there be a better place to assign the object mapper ? I don't understand why we have
      // only one assigned at the controller level.
      formatter.setObjectMapper(this.objectMapper);
      formatter.setResponse(product, fields);

      return new ResponseEntity<Object>(formatter.getResponse(), HttpStatus.OK);

    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException
        | InstantiationException e) {
      log.error("The class " + formatterClass.getName()
          + " somehow, does not fullfill the requirements of the interface ProductBusinessLogic");
      return new ResponseEntity<Object>("Something went wrong, contact pds-operator@jpl.nasa.gov",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }


  }


  @Override
  public ResponseEntity<Object> selectByLidvid(String identifier, @Valid List<String> fields) {

    HashMap<String, Object> product;

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);


      if (pdsIdentifier.isLidvid()) {
        product = this.getLidVid(pdsIdentifier, fields);
      } else {
        product = this.getLatestLidVid(pdsIdentifier, fields);
      }
    } catch (IOException | OpenSearchException e) {
      log.warn("Retrieve content from the database", e);
      return new ResponseEntity<Object>(this.errorMessageFactory.get(e),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return formatSingleProduct(product, fields);



  }

  /*
   * @Override public ResponseEntity<Object> selectByLidvidLatest(String identifier, List<String>
   * fields) { return this.getLatestLidVid(PdsProductIdentifier.fromString(identifier), fields);
   * 
   * }
   * 
   * @Override public ResponseEntity<Object> selectByLidvidAll(String identifier, List<String>
   * fields, Integer limit, List<String> sort, List<String> searchAfter) { return new
   * ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
   * 
   * }
   */

  @SuppressWarnings("unchecked")
  private HashMap<String, Object> getLidVid(PdsProductIdentifier identifier, List<String> fields)
      throws OpenSearchException, IOException {

    // copy the preset searchRequest for this controller.
    SearchRequest.Builder searchRequestBuilder = this.presetSearchRequest.toBuilder();


    FieldValue lidvidFieldValue =
        new FieldValue.Builder().stringValue(identifier.toString()).build();

    MatchQuery lidvidMatch = new MatchQuery.Builder().field("_id").query(lidvidFieldValue).build();

    SearchRequest searchRequest = searchRequestBuilder.query(qb -> qb.match(lidvidMatch)).build();


    OpenSearchClient client = this.connectionContext.getOpenSearchClient();

    // useless to detail here that the HashMap is parameterized <String, Object>
    // because of compilation features, see
    // https://stackoverflow.com/questions/2390662/java-how-do-i-get-a-class-literal-from-a-generic-type
    SearchResponse<HashMap> searchResponse = client.search(searchRequest, HashMap.class);

    return (HashMap<String, Object>) searchResponse.hits().hits().get(0).source();

  }


  private HashMap<String, Object> getLatestLidVid(PdsProductIdentifier identifier,
      List<String> fields) {
    return null;
  }

}
