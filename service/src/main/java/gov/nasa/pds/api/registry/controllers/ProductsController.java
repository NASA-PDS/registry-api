package gov.nasa.pds.api.registry.controllers;

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.model.ErrorMessageFactory;
import gov.nasa.pds.api.registry.model.exceptions.AcceptFormatNotSupportedException;
import gov.nasa.pds.api.registry.model.exceptions.MissSortWithSearchAfterException;
import gov.nasa.pds.api.registry.model.exceptions.NotFoundException;
import gov.nasa.pds.api.registry.model.exceptions.UnhandledException;
import gov.nasa.pds.api.registry.model.api_responses.PdsProductBusinessObject;
import gov.nasa.pds.api.registry.model.api_responses.ProductBusinessLogic;
import gov.nasa.pds.api.registry.model.api_responses.ProductBusinessLogicImpl;
import gov.nasa.pds.api.registry.model.api_responses.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.api_responses.WyriwygBusinessObject;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.RegistrySearchRequestBuilder;
import gov.nasa.pds.model.Summary;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@Controller
public class ProductsController implements ProductsApi {

  private static final Logger log = LoggerFactory.getLogger(ProductsController.class);

  private final ConnectionContext connectionContext;
  private final RegistrySearchRequestBuilder registrySearchRequestBuilder;
  private final ErrorMessageFactory errorMessageFactory;
  private final ObjectMapper objectMapper;
  private OpenSearchClient openSearchClient;
  private SearchRequest presetSearchRequest;

  // TODO move that at a better place, it is not specific to this controller
  private static Map<String, Class<? extends ProductBusinessLogic>> formatters =
      new HashMap<String, Class<? extends ProductBusinessLogic>>();

  static Map<String, Class<? extends ProductBusinessLogic>> getFormatters() {
    return formatters;
  }

  static Integer DEFAULT_LIMIT = 100;

  static {
    // TODO move that at a better place, it is not specific to this controller
    formatters.put("*", PdsProductBusinessObject.class);
    formatters.put("*/*", PdsProductBusinessObject.class);
    formatters.put("application/csv", WyriwygBusinessObject.class);
    formatters.put("application/json", PdsProductBusinessObject.class);
    formatters.put("application/kvp+json", WyriwygBusinessObject.class);
    // this.formatters.put("application/vnd.nasa.pds.pds4+json", new
    // Pds4ProductBusinessObject(true));
    // this.formatters.put("application/vnd.nasa.pds.pds4+xml", new
    // Pds4ProductBusinessObject(false));
    formatters.put("application/xml", PdsProductBusinessObject.class);
    formatters.put("text/csv", WyriwygBusinessObject.class);
    formatters.put("text/html", PdsProductBusinessObject.class);
    formatters.put("text/xml", PdsProductBusinessObject.class);
  }


  @Autowired
  public ProductsController(ConnectionContext connectionContext,
      ErrorMessageFactory errorMessageFactory, ObjectMapper objectMapper) {

    this.connectionContext = connectionContext;
    this.errorMessageFactory = errorMessageFactory;
    this.objectMapper = objectMapper;


    this.registrySearchRequestBuilder = new RegistrySearchRequestBuilder(connectionContext);


    this.openSearchClient = this.connectionContext.getOpenSearchClient();

  }

  private ResponseEntity<Object> formatSingleProduct(HashMap<String, Object> product,
      List<String> fields) throws AcceptFormatNotSupportedException, UnhandledException {
    // TODO add case when Accept is not available, default application/json
    HttpServletRequest curRequest =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String acceptHeaderValue = curRequest.getHeader("Accept");

    if (!ProductsController.formatters.containsKey(acceptHeaderValue)) {
      throw new AcceptFormatNotSupportedException(
          "format " + acceptHeaderValue + "is not supported.");
    }

    Class<? extends ProductBusinessLogic> formatterClass =
        ProductsController.formatters.get(acceptHeaderValue);

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
      throw new UnhandledException(e);
    }
  }

  private ResponseEntity<Object> formatMultipleProducts(RawMultipleProductResponse response,
      List<String> fields) throws AcceptFormatNotSupportedException, UnhandledException {
    // TODO add case when Accept is not available, default application/json
    HttpServletRequest curRequest =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String acceptHeaderValue = curRequest.getHeader("Accept");

    if (!ProductsController.formatters.containsKey(acceptHeaderValue)) {
      throw new AcceptFormatNotSupportedException(
          "format " + acceptHeaderValue + " is not supported.");
    }

    Class<? extends ProductBusinessLogic> formatterClass =
        ProductsController.formatters.get(acceptHeaderValue);

    try {
      // TODO replace URLs from the request path
      ProductBusinessLogicImpl formatter =
          (ProductBusinessLogicImpl) formatterClass.getConstructor().newInstance();
      // TODO check if that is applicable to all formatters.
      // Would there be a better place to assign the object mapper ? I don't understand why we have
      // only one assigned at the controller level.
      formatter.setObjectMapper(this.objectMapper);
      formatter.setResponse(response.getProducts(), response.getSummary(), fields);

      return new ResponseEntity<Object>(formatter.getResponse(), HttpStatus.OK);

    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException
        | InstantiationException e) {
      throw new UnhandledException(e);
    }
  }



  // 6 cases:
  // lidvid, no suffix we want the exact match with the lidvid (case exact)
  // lid, no suffix we want the latest lidvid which lid matches (case latest)
  // lidvid, suffix latest, we want the latest lidvid which lid matches (case latest)
  // lid, suffix latest, we want the latest lidvid which lid matches (case latest)
  // lid suffix all, we want all the lidvid which lid matches (case all)
  // lidvid, suffix all, we want the exact match with the lidvid (case exact)

  @Override
  public ResponseEntity<Object> selectByLidvid(String identifier, @Valid List<String> fields)
      throws UnhandledException, NotFoundException, AcceptFormatNotSupportedException {

    HashMap<String, Object> product;

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);

      if (pdsIdentifier.isLidvid()) {
        product = this.getLidVid(pdsIdentifier, fields);
      } else {
        product = this.getLatestLidVid(pdsIdentifier, fields);
      }
    } catch (IOException | OpenSearchException e) {
      throw new UnhandledException(e);
    }

    return formatSingleProduct(product, fields);



  }


  @Override
  public ResponseEntity<Object> selectByLidvidLatest(String identifier, List<String> fields)
      throws UnhandledException, NotFoundException, AcceptFormatNotSupportedException {

    HashMap<String, Object> product;

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);
      product = this.getLatestLidVid(pdsIdentifier, fields);
    } catch (IOException | OpenSearchException e) {
      throw new UnhandledException(e);
    }

    return formatSingleProduct(product, fields);

  }

  @Override
  public ResponseEntity<Object> selectByLidvidAll(String identifier, List<String> fields,
      Integer limit, List<String> sort, List<String> searchAfter) throws UnhandledException,
      NotFoundException, AcceptFormatNotSupportedException, MissSortWithSearchAfterException {

    RawMultipleProductResponse response;

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);

      if (pdsIdentifier.isLidvid()) {
        response = new RawMultipleProductResponse(this.getLidVid(pdsIdentifier, fields));

      } else {
        limit = (limit == null) ? DEFAULT_LIMIT : limit;
        response = this.getAllLidVid(pdsIdentifier, fields, limit, sort, searchAfter);
      }



    } catch (IOException | OpenSearchException e) {
      throw new UnhandledException(e);
    }

    return formatMultipleProducts(response, fields);


  }

  @Override
  public ResponseEntity<Object> productList(List<String> fields, List<String> keywords,
      Integer limit, String q, List<String> sort, List<String> searchAfter) throws Exception {
    RawMultipleProductResponse response;


    RegistrySearchRequestBuilder registrySearchRequestBuilder =
        new RegistrySearchRequestBuilder(this.registrySearchRequestBuilder);


    SearchRequest searchRequest =
        registrySearchRequestBuilder.addQParam(q).addKeywordsParam(keywords).build();

    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    RawMultipleProductResponse products = new RawMultipleProductResponse(searchResponse);

    return formatMultipleProducts(products, fields);


  }



  @SuppressWarnings("unchecked")
  private HashMap<String, Object> getLidVid(PdsProductIdentifier identifier, List<String> fields)
      throws OpenSearchException, IOException, NotFoundException {

    RegistrySearchRequestBuilder registrySearchRequestBuilder =
        new RegistrySearchRequestBuilder(this.registrySearchRequestBuilder);


    SearchRequest searchRequest = registrySearchRequestBuilder.addLidvidMatch(identifier).build();

    // useless to detail here that the HashMap is parameterized <String, Object>
    // because of compilation features, see
    // https://stackoverflow.com/questions/2390662/java-how-do-i-get-a-class-literal-from-a-generic-type
    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);
    if (searchResponse.hits().total().value() == 0) {
      throw new NotFoundException("No product found with identifier " + identifier.toString());
    }
    HashMap<String, Object> product = searchResponse.hits().hits().get(0).source();
    ProductsController.log.debug("Found product with lid=" + product.get("lid"));
    return product;

  }


  @SuppressWarnings("unchecked")
  private HashMap<String, Object> getLatestLidVid(PdsProductIdentifier identifier,
      List<String> fields) throws OpenSearchException, IOException, NotFoundException {

    RegistrySearchRequestBuilder registrySearchRequestBuilder =
        new RegistrySearchRequestBuilder(this.registrySearchRequestBuilder);

    SearchRequest searchRequest =
        registrySearchRequestBuilder.addLidMatch(identifier).onlyLatest().build();

    // useless to detail here that the HashMap is parameterized <String, Object>
    // because of compilation features, see
    // https://stackoverflow.com/questions/2390662/java-how-do-i-get-a-class-literal-from-a-generic-type
    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    if (searchResponse.hits().total().value() == 0) {
      throw new NotFoundException("No product found with identifier " + identifier.toString());
    }

    HashMap<String, Object> product = searchResponse.hits().hits().get(0).source();
    ProductsController.log.debug("Found product with lid=" + product.get("lid"));
    return (HashMap<String, Object>) searchResponse.hits().hits().get(0).source();

  }

  private RawMultipleProductResponse getAllLidVid(PdsProductIdentifier identifier,
      List<String> fields, Integer limit, List<String> sort, List<String> searchAfter)
      throws OpenSearchException, IOException, NotFoundException, MissSortWithSearchAfterException {

    RegistrySearchRequestBuilder registrySearchRequestBuilder =
        new RegistrySearchRequestBuilder(this.registrySearchRequestBuilder);

    registrySearchRequestBuilder = registrySearchRequestBuilder.addLidMatch(identifier);

    if ((sort != null) && (!sort.isEmpty())) {
      registrySearchRequestBuilder.sort(sort);
    }

    registrySearchRequestBuilder.size(limit);

    if ((searchAfter != null) && (!searchAfter.isEmpty())) {
      if ((sort == null) || (sort.isEmpty())) {
        throw new MissSortWithSearchAfterException();
      }
      registrySearchRequestBuilder.searchAfter(searchAfter);
    }



    SearchRequest searchRequest = registrySearchRequestBuilder.build();

    // useless to detail here that the HashMap is parameterized <String, Object>
    // because of compilation features, see
    // https://stackoverflow.com/questions/2390662/java-how-do-i-get-a-class-literal-from-a-generic-type
    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    if (searchResponse.hits().total().value() == 0) {
      throw new NotFoundException("No product found with identifier " + identifier.toString());
    }

    return new RawMultipleProductResponse(searchResponse);

  }



}
