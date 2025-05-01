package gov.nasa.pds.api.registry.controllers;

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import gov.nasa.pds.api.base.ClassesApi;
import gov.nasa.pds.api.base.PropertiesApi;
import gov.nasa.pds.api.registry.model.exceptions.*;
import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductClasses;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.GetMappingRequest;
import org.opensearch.client.opensearch.indices.GetMappingResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.configuration.WebMVCConfig;
import gov.nasa.pds.api.registry.model.ErrorMessageFactory;
import gov.nasa.pds.api.registry.model.api_responses.PdsProductBusinessObject;
import gov.nasa.pds.api.registry.model.api_responses.ProductBusinessLogic;
import gov.nasa.pds.api.registry.model.api_responses.ProductBusinessLogicImpl;
import gov.nasa.pds.api.registry.model.api_responses.RawMultipleProductResponse;
import gov.nasa.pds.api.registry.model.api_responses.WyriwygBusinessObject;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.RegistrySearchRequestBuilder;
import gov.nasa.pds.api.registry.util.LogExecutionTime;
import gov.nasa.pds.model.PropertiesListInner;



@Controller
// TODO: Refactor common controller code out of ProductsController and split the additional API
// implementations out into
// corresponding controllers
public class ProductsController implements ProductsApi, ClassesApi, PropertiesApi {

  @Override
  // TODO: Remove this when the common controller code is refactored out - it is only necessary
  // because additional
  // interfaces have been implemented as a stopgap
  public Optional<NativeWebRequest> getRequest() {
    return ProductsApi.super.getRequest();
  }

  private static final Logger log = LoggerFactory.getLogger(ProductsController.class);

  private final ConnectionContext connectionContext;
  private final ErrorMessageFactory errorMessageFactory;
  private final ObjectMapper objectMapper;
  private OpenSearchClient openSearchClient;
  private SearchRequest presetSearchRequest;

  static Integer DEFAULT_LIMIT = 100;


  // TODO move that at a better place, it is not specific to this controller
  @Autowired
  public ProductsController(ConnectionContext connectionContext,
      ErrorMessageFactory errorMessageFactory, ObjectMapper objectMapper) {

    this.errorMessageFactory = errorMessageFactory;
    this.objectMapper = objectMapper;
    this.connectionContext = connectionContext;
    this.openSearchClient = this.connectionContext.getOpenSearchClient();

  }

  @LogExecutionTime
  private ResponseEntity<Object> formatSingleProduct(HashMap<String, Object> product,
      List<String> fields) throws AcceptFormatNotSupportedException, UnhandledException {
    // TODO add case when Accept is not available, default application/json
    HttpServletRequest curRequest =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String acceptHeaderValue = curRequest.getHeader("Accept");

    Class<? extends ProductBusinessLogic> formatterClass =
        WebMVCConfig.selectFormatterClass(acceptHeaderValue);

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

  @LogExecutionTime
  private ResponseEntity<Object> formatMultipleProducts(RawMultipleProductResponse response,
      List<String> fields) throws AcceptFormatNotSupportedException, UnhandledException {
    // TODO add case when Accept is not available, default application/json
    HttpServletRequest curRequest =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String acceptHeaderValue = curRequest.getHeader("Accept");

    Class<? extends ProductBusinessLogic> formatterClass =
        WebMVCConfig.selectFormatterClass(acceptHeaderValue);


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
  public ResponseEntity<Object> selectByLidvid(String identifier, List<String> fields)
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
      NotFoundException, AcceptFormatNotSupportedException, SortSearchAfterMismatchException {

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

    //todo: apply faceting to this route if applicable
    return formatMultipleProducts(response, fields);


  }

  @Override
  @LogExecutionTime
  public ResponseEntity<Object> productList(List<String> fields, List<String> keywords,
      Integer limit, String q, List<String> sort, List<String> searchAfter, List<String> facetFields, Integer facetLimit) throws Exception {


    SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
        .applyMultipleProductsDefaults(fields, q, keywords, limit, sort, searchAfter, facetFields, facetLimit, true).build();

    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    RawMultipleProductResponse products = new RawMultipleProductResponse(searchResponse);

    //todo: apply faceting to this route if applicable
    return formatMultipleProducts(products, fields);

  }



  @SuppressWarnings("unchecked")
  private HashMap<String, Object> getLidVid(PdsProductIdentifier identifier, List<String> fields)
      throws OpenSearchException, IOException, NotFoundException {

    SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
        .matchLidvid(identifier).fieldsFromStrings(fields).build();

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

    SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
        .matchLid(identifier).fieldsFromStrings(fields).excludeSupersededProducts().build();

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
      throws OpenSearchException, IOException, NotFoundException, SortSearchAfterMismatchException {

    SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
        .matchLid(identifier).fieldsFromStrings(fields).paginate(limit, sort, searchAfter).build();

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

  private PdsProductClasses resolveProductClass(PdsProductIdentifier identifier)
      throws OpenSearchException, IOException, NotFoundException {
    SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
        .matchLid(identifier).fieldsFromStrings(List.of(PdsProductClasses.getPropertyName()))
        .excludeSupersededProducts().build();

    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    if (searchResponse.hits().total().value() == 0) {
      throw new NotFoundException("No product found with identifier " + identifier.toString());
    }

    String productClassStr = searchResponse.hits().hits().get(0).source()
        .get(PdsProductClasses.getPropertyName()).toString();
    return PdsProductClasses.valueOf(productClassStr);
  }


  private PdsLidVid resolveLatestLidvid(PdsProductIdentifier identifier)
      throws OpenSearchException, IOException, NotFoundException {

    SearchRequest searchRequest =
        new RegistrySearchRequestBuilder(this.connectionContext).matchLid(identifier.getLid())
            .fieldsFromStrings(List.of()).excludeSupersededProducts().build();

    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    if (searchResponse.hits().total().value() == 0) {
      throw new NotFoundException("No lidvids found with lid " + identifier.getLid().toString());
    }

    // TODO: Determine how to handle multiple hits due to sweepers lag

    return PdsLidVid.fromString(searchResponse.hits().hits().get(0).id());
  }


  private List<PdsLidVid> resolveExtantLidvids(PdsLid lid)
      throws OpenSearchException, IOException, NotFoundException {

    String lidvidKey = "_id";

    SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
        .matchLid(lid).fieldsFromStrings(List.of(lidvidKey)).build();

    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    if (searchResponse.hits().total().value() == 0) {
      throw new NotFoundException("No lidvids found with lid " + lid.toString());
    }

    return searchResponse.hits().hits().stream().map(hit -> hit.source().get(lidvidKey).toString())
        .map(PdsLidVid::fromString).toList();
  }

  /**
   * Resolve a PdsProductIdentifier to a PdsLidVid according to the common rules of the API. The
   * rules are currently trivial, but may incorporate additional behaviour later
   *
   * @param identifier a LID or LIDVID
   * @return a LIDVID
   */
  private PdsLidVid resolveIdentifierToLidvid(PdsProductIdentifier identifier)
      throws NotFoundException, IOException {
    return identifier.isLidvid() ? (PdsLidVid) identifier : resolveLatestLidvid(identifier);
  }

  @Override
  public ResponseEntity<Object> productMembers(String identifier, List<String> fields,
      Integer limit, String q, List<String> sort, List<String> searchAfter, List<String> facetFields, Integer facetLimit)
      throws NotFoundException, UnhandledException, SortSearchAfterMismatchException,
      BadRequestException, AcceptFormatNotSupportedException {

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);
      PdsProductClasses productClass = resolveProductClass(pdsIdentifier);
      PdsLidVid lidvid = resolveIdentifierToLidvid(pdsIdentifier);

      RegistrySearchRequestBuilder searchRequestBuilder =
          new RegistrySearchRequestBuilder(this.connectionContext);

      if (productClass.isBundle()) {
        searchRequestBuilder.matchMembersOfBundle(lidvid);
        searchRequestBuilder.onlyCollections();
      } else if (productClass.isCollection()) {
        searchRequestBuilder.matchMembersOfCollection(lidvid);
        searchRequestBuilder.onlyBasicProducts();
      } else {
        throw new BadRequestException(
            "productMembers endpoint is only valid for products with Product_Class '"
                + PdsProductClasses.Product_Bundle + "' or '" + PdsProductClasses.Product_Collection
                + "' (got '" + productClass + "')");
      }

      SearchRequest searchRequest = searchRequestBuilder
          .applyMultipleProductsDefaults(fields, q, List.of(), limit, sort, searchAfter, facetFields, facetLimit, true)
          .build();

      SearchResponse<HashMap> searchResponse =
          this.openSearchClient.search(searchRequest, HashMap.class);

      RawMultipleProductResponse products = new RawMultipleProductResponse(searchResponse);

      //todo: apply faceting to this route if applicable
    return formatMultipleProducts(products, fields);

    } catch (IOException | OpenSearchException | UnparsableQParamException e) {
      throw new UnhandledException(e);
    }
  }

  @Override
  public ResponseEntity<Object> productMembersMembers(String identifier, List<String> fields,
      Integer limit, String q, List<String> sort, List<String> searchAfter, List<String> facetFields, Integer facetLimit)
      throws NotFoundException, UnhandledException, SortSearchAfterMismatchException,
      BadRequestException, AcceptFormatNotSupportedException {

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);
      PdsProductClasses productClass = resolveProductClass(pdsIdentifier);
      PdsLidVid lidvid = resolveIdentifierToLidvid(pdsIdentifier);

      RegistrySearchRequestBuilder searchRequestBuilder =
          new RegistrySearchRequestBuilder(this.connectionContext);

      if (productClass.isBundle()) {
        searchRequestBuilder.matchMembersOfBundle(lidvid);
        searchRequestBuilder.onlyBasicProducts();
      } else {
        throw new BadRequestException(
            "productMembers endpoint is only valid for products with Product_Class '"
                + PdsProductClasses.Product_Bundle + "' (got '" + productClass + "')");
      }

      SearchRequest searchRequest = searchRequestBuilder
          .applyMultipleProductsDefaults(fields, q, List.of(), limit, sort, searchAfter, facetFields, facetLimit, true)
          .build();

      SearchResponse<HashMap> searchResponse =
          this.openSearchClient.search(searchRequest, HashMap.class);

      RawMultipleProductResponse products = new RawMultipleProductResponse(searchResponse);

      //todo: apply faceting to this route if applicable
    return formatMultipleProducts(products, fields);

    } catch (IOException | OpenSearchException | UnparsableQParamException e) {
      throw new UnhandledException(e);
    }
  }

  /**
   * Given a PdsProductIdentifier and the name of a document field which is expected to contain an
   * array of LIDVID strings, return the chained contents of that field from all documents matching
   * the identifier (multiple docs are possible if the identifier is a LID).
   *
   * @param identifier the LID/LIDVID for which to retrieve documents
   * @param fieldName the name of the document _source property/field from which to extract results
   * @return a deduplicated list of the aggregated property/field contents, converted to
   *         PdsProductLidvids
   */
  private List<PdsLidVid> resolveLidVidsFromProductField(PdsProductIdentifier identifier,
      String fieldName)
      throws OpenSearchException, IOException, NotFoundException, UnhandledException {

    RegistrySearchRequestBuilder searchRequestBuilder =
        new RegistrySearchRequestBuilder(this.connectionContext);

    if (identifier.isLid()) {
      searchRequestBuilder.matchLid(identifier);
    } else if (identifier.isLidvid()) {
      searchRequestBuilder.matchLidvid(identifier);
    } else {
      throw new UnhandledException(
          "PdsProductIdentifier identifier is neither LID nor LIDVID. This should never occur");
    }

    SearchRequest searchRequest =
        searchRequestBuilder.matchLid(identifier).fieldsFromStrings(List.of(fieldName)).build();

    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    if (searchResponse.hits().total().value() == 0) {
      throw new NotFoundException("No product found with identifier " + identifier);
    }

    return searchResponse.hits().hits().stream()
        .map(hit -> (List<String>) hit.source().get(fieldName)).filter(Objects::nonNull)
        .flatMap(Collection::stream).map(PdsLidVid::fromString).toList();
  }


  @Override
  public ResponseEntity<Object> productMemberOf(String identifier, List<String> fields,
      Integer limit, String q, List<String> sort, List<String> searchAfter, List<String> facetFields, Integer facetLimit)
      throws NotFoundException, UnhandledException, SortSearchAfterMismatchException,
      BadRequestException, AcceptFormatNotSupportedException, UnparsableQParamException {

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);
      PdsProductClasses productClass = resolveProductClass(pdsIdentifier);
      PdsLidVid lidvid = resolveIdentifierToLidvid(pdsIdentifier);

      List<PdsLidVid> parentIds;
      if (productClass.isCollection()) {
        parentIds =
            resolveLidVidsFromProductField(lidvid, "ops:Provenance/ops:parent_bundle_identifier");
      } else if (productClass.isBasicProduct()) {
        parentIds = resolveLidVidsFromProductField(lidvid,
            "ops:Provenance/ops:parent_collection_identifier");
      } else {
        throw new BadRequestException(
            "productMembersOf endpoint is not valid for products with Product_Class '"
                + PdsProductClasses.Product_Bundle + "' (got '" + productClass + "')");
      }

      SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
          .applyMultipleProductsDefaults(fields, q, List.of(), limit, sort, searchAfter, facetFields, facetLimit, true)
          .matchFieldAnyOfIdentifiers("_id", parentIds).build();

      SearchResponse<HashMap> searchResponse =
          this.openSearchClient.search(searchRequest, HashMap.class);

      RawMultipleProductResponse products = new RawMultipleProductResponse(searchResponse);

      //todo: apply faceting to this route if applicable
    return formatMultipleProducts(products, fields);

    } catch (IOException | OpenSearchException e) {
      throw new UnhandledException(e);
    }
  }

  @Override
  public ResponseEntity<Object> productMemberOfOf(String identifier, List<String> fields,
      Integer limit, String q, List<String> sort, List<String> searchAfter, List<String> facetFields, Integer facetLimit)
      throws NotFoundException, UnhandledException, SortSearchAfterMismatchException,
      BadRequestException, AcceptFormatNotSupportedException, UnparsableQParamException {

    try {
      PdsProductIdentifier pdsIdentifier = PdsProductIdentifier.fromString(identifier);
      PdsProductClasses productClass = resolveProductClass(pdsIdentifier);
      PdsLidVid lidvid = resolveIdentifierToLidvid(pdsIdentifier);

      List<PdsLidVid> parentIds;
      if (productClass.isBasicProduct()) {
        parentIds =
            resolveLidVidsFromProductField(lidvid, "ops:Provenance/ops:parent_bundle_identifier");
      } else {
        // TODO: replace with enumeration of acceptable values later
        throw new BadRequestException(
            "productMembersOf endpoint is not valid for products with Product_Class '"
                + PdsProductClasses.Product_Bundle + "' or '" + PdsProductClasses.Product_Collection
                + "' (got '" + productClass + "')");
      }

      SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
          .applyMultipleProductsDefaults(fields, q, List.of(), limit, sort, searchAfter, facetFields, facetLimit, true)
          .matchFieldAnyOfIdentifiers("_id", parentIds).build();

      SearchResponse<HashMap> searchResponse =
          this.openSearchClient.search(searchRequest, HashMap.class);

      RawMultipleProductResponse products = new RawMultipleProductResponse(searchResponse);

      //todo: apply faceting to this route if applicable
    return formatMultipleProducts(products, fields);

    } catch (IOException | OpenSearchException e) {
      throw new UnhandledException(e);
    }
  }

  @Override
  // TODO: Relocate this to ClassesController once common controller code has been
  // extracted/refactored
  public ResponseEntity<Object> classList(String propertyClass, List<String> fields,
      List<String> keywords, Integer limit, String q, List<String> sort, List<String> searchAfter, List<String> facetFields, Integer facetLimit)
      throws Exception {
    PdsProductClasses pdsProductClass;
    try {
      pdsProductClass = PdsProductClasses.fromSwaggerName(propertyClass);
    } catch (IllegalArgumentException err) {
      throw new BadRequestException(err.getMessage());
    }

    SearchRequest searchRequest = new RegistrySearchRequestBuilder(this.connectionContext)
        .applyMultipleProductsDefaults(fields, q, keywords, limit, sort, searchAfter, facetFields, facetLimit, true)
        .matchProductClass(pdsProductClass).build();

    SearchResponse<HashMap> searchResponse =
        this.openSearchClient.search(searchRequest, HashMap.class);

    RawMultipleProductResponse products = new RawMultipleProductResponse(searchResponse);

    //todo: apply faceting to this route if applicable
    return formatMultipleProducts(products, fields);
  }

  @Override
  // TODO: Relocate this to ClassesController once common controller code has been
  // extracted/refactored
  public ResponseEntity<List<String>> classes() throws Exception {
    return new ResponseEntity<>(
        Arrays.stream(PdsProductClasses.values()).map(PdsProductClasses::getSwaggerName).toList(),
        HttpStatusCode.valueOf(200));
  }

  /**
   * Resolve the appropriate enumerated user type hint from an OpenSearch Property
   */
  protected PropertiesListInner.TypeEnum _resolvePropertyToEnumType(Property property) {
    if (property.isBoolean()) {
      return PropertiesListInner.TypeEnum.BOOLEAN;
    } else if (property.isKeyword() || property.isText()) {
      return PropertiesListInner.TypeEnum.STRING;
    } else if (property.isDate()) {
      return PropertiesListInner.TypeEnum.TIMESTAMP;
    } else if (property.isInteger() || property.isLong()) {
      return PropertiesListInner.TypeEnum.INTEGER;
    } else if (property.isFloat() || property.isDouble()) {
      return PropertiesListInner.TypeEnum.FLOAT;
    } else {
      return PropertiesListInner.TypeEnum.UNSUPPORTED;
    }
  }

  @Override
  public ResponseEntity<List<PropertiesListInner>> productPropertiesList() throws Exception {

    List<String> indexNames = this.connectionContext.getRegistryIndices();

    GetMappingRequest getMappingRequest = new GetMappingRequest.Builder().index(indexNames).build();

    OpenSearchIndicesClient indicesClient = this.openSearchClient.indices();

    GetMappingResponse getMappingResponse = indicesClient.getMapping(getMappingRequest);

    Set<String> resultIndexNames = getMappingResponse.result().keySet();
    SortedMap<String, PropertiesListInner.TypeEnum> aggregatedMappings = new TreeMap<>();
    for (String indexName : resultIndexNames) {
      Set<Map.Entry<String, Property>> indexProperties =
          getMappingResponse.result().get(indexName).mappings().properties().entrySet();
      for (Map.Entry<String, Property> property : indexProperties) {
        String jsonPropertyName = PdsProperty.toJsonPropertyString(property.getKey());
        Property openPropertyName = property.getValue();
        PropertiesListInner.TypeEnum propertyEnumType =
            _resolvePropertyToEnumType(openPropertyName);

        // No consistency-checking between duplicates, for now. TODO: add error log for mismatching
        // duplicates
        aggregatedMappings.put(jsonPropertyName, propertyEnumType);
      }
    }

    List<PropertiesListInner> apiResponseContent =
        aggregatedMappings.entrySet().stream().map((entry) -> {
          PropertiesListInner propertyElement = new PropertiesListInner();
          propertyElement.setProperty(entry.getKey());
          propertyElement.setType(entry.getValue());
          return propertyElement;
        }).toList();

    return new ResponseEntity<>(apiResponseContent, HttpStatus.OK);
  }
}

