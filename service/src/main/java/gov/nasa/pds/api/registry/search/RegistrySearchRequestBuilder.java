package gov.nasa.pds.api.registry.search;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductClasses;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.client.json.jackson.JacksonJsonpGenerator;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.aggregations.TermsAggregation;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TermsQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQueryField;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.search.SourceConfig;
import org.opensearch.client.opensearch.core.search.SourceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.lexer.SearchLexer;
import gov.nasa.pds.api.registry.lexer.SearchParser;
import gov.nasa.pds.api.registry.model.Antlr4SearchListener;
import gov.nasa.pds.api.registry.model.SearchUtil;
import gov.nasa.pds.api.registry.model.exceptions.SortSearchAfterMismatchException;
import gov.nasa.pds.api.registry.model.exceptions.UnparsableQParamException;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;


public class RegistrySearchRequestBuilder extends SearchRequest.Builder {

  private static final Logger log = LoggerFactory.getLogger(RegistrySearchRequestBuilder.class);

  private ConnectionContext connectionContext;
  private List<String> registryIndices;
  private BoolQuery.Builder queryBuilder;

  public RegistrySearchRequestBuilder(ConnectionContext connectionContext) {
    // edunn TODO: Evaluate what can be taken out of the constructor

    this.connectionContext = connectionContext;

    this.registryIndices = this.connectionContext.getRegistryIndices();
    log.info("Use indices: " + String.join(",", registryIndices) + "End indices");

    this.index(registryIndices);

    Query baseQuery = getMandatoryBaselineQuery(connectionContext);
    this.queryBuilder = new BoolQuery.Builder().must(baseQuery);
  }

  /**
   * Return a baseline, non-configurable query which applies to all search requests. Currently, this
   * is just archive status, but this will likely be subject to extensive revision (may balloon, or
   * disappear entirely)
   * 
   * @param connectionContext
   * @return the minimal match constraints applicable to all search requests
   */
  private static Query getMandatoryBaselineQuery(ConnectionContext connectionContext) {
    List<String> archiveStatus = connectionContext.getArchiveStatus();
    List<FieldValue> archiveStatusFieldValues = archiveStatus.stream().map(FieldValue::of).toList();
    log.info("Only publishes archiveStatus: " + String.join(",", archiveStatus));
    TermsQueryField archiveStatusTerms =
        new TermsQueryField.Builder().value(archiveStatusFieldValues).build();

    TermsQuery archiveStatusQuery = new TermsQuery.Builder()
        .field("ops:Tracking_Meta/ops:archive_status").terms(archiveStatusTerms).build();

    return archiveStatusQuery.toQuery();
  }

  /**
   * Access the internal BoolQuery.Builder instance which is used to build a query during
   * RegistrySearchRequestBuilder.build() Before accessing the query builder directly, consider
   * whether the behaviour is common enough that it should be abstracted as a method of
   * RegistrySearchRequestBuilder.
   * 
   * @return the query builder instance for this search-request builder
   */
  public BoolQuery.Builder getQueryBuilder() {
    return this.queryBuilder;
  }

  /**
   * Applies a common set of constraints and other build options which generally apply to any
   * endpoint which queries OpenSearch for a result-set of multiple products.
   * 
   * @param includePdsProperties - which properties to include in the results (JSON format, not
   *        OpenSearch format)
   * @param queryString - a querystring (q=) to constrain the result-set by
   * @param keywords - a set of keyword matches to <exact behaviour TBD - not implemented yet>
   * @param pageSize - the page size to use for pagination
   * @param sortFieldNames - the fields by which results are sorted (ascending), from highest to
   *        lowest priority
   * @param searchAfterFieldValues - the values corresponding to the sort fields, for pagination
   * @param facetFields - the opensearch fields to generate facet aggregations for
   * @param excludeSupersededProducts - whether to exclude superseded products from the result set
   */
  public RegistrySearchRequestBuilder applyMultipleProductsDefaults(
      List<PdsProperty> includePdsProperties, String queryString, List<String> keywords,
      Integer pageSize, List<String> sortFieldNames, List<String> searchAfterFieldValues,
      List<String> facetFields, Integer facetLimit, Boolean excludeSupersededProducts)
      throws UnparsableQParamException, SortSearchAfterMismatchException {
    this.fieldsFromPdsProperties(includePdsProperties).constrainByQueryString(queryString)
        .addKeywordsParam(keywords).addPropertyFacets(facetFields, facetLimit)
        .paginate(pageSize, sortFieldNames, searchAfterFieldValues);

    if (excludeSupersededProducts) {
      this.excludeSupersededProducts();
    }

    return this;
  }

  public SearchRequest build() {
    BoolQuery bQuery = this.queryBuilder.build();
    this.query(bQuery.toQuery());
    this.trackTotalHits(t -> t.enabled(true));

    SearchRequest searchRequest = super.build();

    try {
      String requestJson = serializeSearchRequest(searchRequest);
      log.debug("Generated OpenSearch SearchRequest with query:\n" + requestJson);
    } catch (Exception e) {
      log.error("Failed to generate json serialization of SearchRequest: " + e);
    }

    return searchRequest;
  }

  public String serializeSearchRequest(SearchRequest searchRequest) throws IOException {
    StringWriter writer = new StringWriter();
    JsonFactory jsonFactory = new JsonFactory();
    JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
    jsonGenerator.useDefaultPrettyPrinter();

    JacksonJsonpGenerator jacksonJsonpGenerator = new JacksonJsonpGenerator(jsonGenerator);
    searchRequest.serialize(jacksonJsonpGenerator, null);
    jsonGenerator.close();
    return writer.toString();
  }

  /**
   * Add a constraint that a given field name must match the given field value
   * 
   * @param fieldName the name of the field in OpenSearch format
   * @param value the value which must be present in the given field
   */
  public RegistrySearchRequestBuilder matchField(String fieldName, String value) {
    FieldValue fieldValue = new FieldValue.Builder().stringValue(value).build();
    MatchQuery lidvidMatch = new MatchQuery.Builder().field(fieldName).query(fieldValue).build();

    this.queryBuilder.must(lidvidMatch.toQuery());

    return this;
  }

  /**
   * Add a constraint that a given field name must match the given field value
   * 
   * @param fieldName the name of the field in OpenSearch format
   * @param identifier the PDS identifier whose string representation must be present in the given
   *        field
   */
  public RegistrySearchRequestBuilder matchField(String fieldName,
      PdsProductIdentifier identifier) {
    return this.matchField(fieldName, identifier.toString());
  }


  /**
   * Add a constraint that a given field name must match at least one of the given field values
   * 
   * @param fieldName the name of the field in OpenSearch format
   * @param values the values, one of which must be present in the given field
   */
  public RegistrySearchRequestBuilder matchFieldAnyOf(String fieldName, List<String> values) {
    List<FieldValue> fieldValues =
        values.stream().map(value -> new FieldValue.Builder().stringValue(value).build()).toList();
    TermsQueryField termsQueryField = new TermsQueryField.Builder().value(fieldValues).build();
    TermsQuery query = new TermsQuery.Builder().field(fieldName).terms(termsQueryField).build();

    this.queryBuilder.must(query.toQuery());

    return this;
  }

  /**
   * Add a constraint that a given field name must match at least one of the given field values
   * 
   * @param fieldName the name of the field in OpenSearch format
   * @param identifiers the PDS identifiers, one of whose string representation must be present in
   *        the given field
   */
  public RegistrySearchRequestBuilder matchFieldAnyOfIdentifiers(String fieldName,
      List<? extends PdsProductIdentifier> identifiers) {
    return this.matchFieldAnyOf(fieldName,
        identifiers.stream().map(PdsProductIdentifier::toString).toList());
  }

  public RegistrySearchRequestBuilder matchLidvid(PdsProductIdentifier identifier) {
    return this.matchField("_id", identifier);
  }

  public RegistrySearchRequestBuilder matchLid(PdsProductIdentifier identifier) {
    return this.matchField("lid", identifier.getLid());
  }

  public RegistrySearchRequestBuilder matchProductClass(PdsProductClasses productClass) {
    return this.matchField(PdsProductClasses.getPropertyName(), productClass.getValue());
  }

  public RegistrySearchRequestBuilder matchMembersOfBundle(PdsLidVid identifier) {
    return this.matchField("ops:Provenance/ops:parent_bundle_identifier", identifier);
  }

  public RegistrySearchRequestBuilder matchMembersOfCollection(PdsLidVid identifier) {
    return this.matchField("ops:Provenance/ops:parent_collection_identifier", identifier);
  }

  public RegistrySearchRequestBuilder paginate(Integer pageSize, List<String> sortFieldNames,
      List<String> searchAfterFieldValues) throws SortSearchAfterMismatchException {
    if ((sortFieldNames != null) && (!sortFieldNames.isEmpty())) {
      this.sortFromStrings(sortFieldNames);
    }

    this.size(pageSize);

    if ((searchAfterFieldValues != null) && (!searchAfterFieldValues.isEmpty())) {
      if (sortFieldNames == null) {
        throw new SortSearchAfterMismatchException(
            "sort argument must be provided if searchAfter argument is provided");
      } else if (searchAfterFieldValues.size() != sortFieldNames.size()) {
        throw new SortSearchAfterMismatchException(
            "sort and searchAfter arguments must be of equal length if provided");
      }
      this.searchAfterFromStrings(searchAfterFieldValues);
    }

    return this;

  }

  /**
   * Implements an alternative to .sort() that accepts strings in API property format. Currently
   * hardcoded to sort in ascending order only.
   * 
   * @param sortFieldNames
   */
  public RegistrySearchRequestBuilder sortFromStrings(List<String> sortFieldNames) {

    String openSearchField;

    List<SortOptions> sortOptionsList = new ArrayList<SortOptions>();
    for (String field : sortFieldNames) {
      openSearchField = SearchUtil.jsonPropertyToOpenProperty(field);
      FieldSort fieldSort =
          new FieldSort.Builder().field(openSearchField).order(SortOrder.Asc).build();
      sortOptionsList.add(new SortOptions.Builder().field(fieldSort).build());
    }

    this.sort(sortOptionsList);

    return this;

  }

  /**
   * Implements an alternative to .searchAfter() that accepts values as strings. No-op in current
   * version of OpenSearch client, but a later version will require the commented-out implementation
   * to convert the Strings to FieldValues
   * 
   * @param searchAfterValues
   */
  public RegistrySearchRequestBuilder searchAfterFromStrings(List<String> searchAfterValues) {
    /*
     * List<FieldValue> fieldValues = new ArrayList<FieldValue>();
     * 
     * for (String fieldValue : searchAfter) { fieldValues.add(new // TODO check if the number value
     * need to be handled specfically. Method stringValue() implies yes
     * FieldValue.Builder().stringValue(fieldValue).build()); }
     */
    this.searchAfter(searchAfterValues);


    return this;
  }


  /**
   * Implements an alternative to .fields() that accepts values as strings.
   * 
   * @param fieldNames
   */
  public RegistrySearchRequestBuilder fieldsFromStrings(List<String> fieldNames) {
    // We want everything if the user did not request any specific fields
    if ((fieldNames != null) && (!fieldNames.isEmpty())) {
      log.info("restricting list of fields requested from OpenSearch.");
      List<String> openSearchField = fieldNames.stream().map(SearchUtil::jsonPropertyToOpenProperty)
          .collect(Collectors.toList());

      SourceFilter sourceFilter = new SourceFilter.Builder().includes(openSearchField).build();
      SourceConfig limitedSourceCfg = new SourceConfig.Builder().filter(sourceFilter).build();

      this.source(limitedSourceCfg);
    }
    return this;
  }



  /**
   * Implements an alternative to .fields() that accepts values as List<PdsProperty>.
   * 
   * @param PdsProperties
   */
  public RegistrySearchRequestBuilder fieldsFromPdsProperties(List<PdsProperty> PdsProperties) {
    // We want everything if the user did not request any specific fields
    if ((PdsProperties != null) && (!PdsProperties.isEmpty())) {
      log.info("restricting list of fields requested from OpenSearch.");
      List<String> openSearchField = PdsProperties.stream().map(PdsProperty::toOpenPropertyString)
          .collect(Collectors.toList());

      SourceFilter sourceFilter = new SourceFilter.Builder().includes(openSearchField).build();
      SourceConfig limitedSourceCfg = new SourceConfig.Builder().filter(sourceFilter).build();

      this.source(limitedSourceCfg);
    }
    return this;
  }



  private static BoolQuery parseQueryString(String queryString) {
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);

    SearchParser par = new SearchParser(tokens);
    par.setErrorHandler(new BailErrorStrategy());
    ParseTree tree = par.query();

    log.debug(tree.toStringTree(par));

    // Walk it and attach our listener
    ParseTreeWalker walker = new ParseTreeWalker();
    Antlr4SearchListener listener = new Antlr4SearchListener();
    walker.walk(listener, tree);

    return listener.getBoolQuery();
  }

  /**
   * Constrain results with a query-string in PDS API Search Query syntax
   * 
   * @param q a PDS API Search Query string
   * @throws UnparsableQParamException if the string is not parseable
   */
  public RegistrySearchRequestBuilder constrainByQueryString(String q)
      throws UnparsableQParamException {

    try {
      if ((q != null) && (q.length() > 0)) {
        BoolQuery qBoolQuery = RegistrySearchRequestBuilder.parseQueryString(q);
        this.queryBuilder.must(qBoolQuery.toQuery());
      }
      return this;
    } catch (RecognitionException | ParseCancellationException e) {
      log.info("Unable to parse q " + StringUtils.normalizeSpace(q) + "error message is " + e);
      throw new UnparsableQParamException(
          "q string value:" + q + " Error message " + e.getMessage());
    }


  }

  public RegistrySearchRequestBuilder addKeywordsParam(List<String> keywords) {

    // TODO implement
    return this;
  }

  /**
   * Limit results to the latest version of each LID in the result-set. N.B. this does *not* mean
   * the latest version which satisfies other constraints, so application of this constraint can
   * result in no hits being returned despite valid results existing.
   */
  public RegistrySearchRequestBuilder excludeSupersededProducts() {

    ExistsQuery supersededByExists =
        new ExistsQuery.Builder().field("ops:Provenance/ops:superseded_by").build();

    this.queryBuilder.mustNot(supersededByExists.toQuery());

    return this;
  }

  /**
   * Limit results to bundle products
   */
  public RegistrySearchRequestBuilder onlyBundles() {
    return this.matchField(PdsProductClasses.getPropertyName(),
        PdsProductClasses.Product_Bundle.toString());
  }


  /**
   * Limit results to collection products
   */
  public RegistrySearchRequestBuilder onlyCollections() {
    return this.matchField(PdsProductClasses.getPropertyName(),
        PdsProductClasses.Product_Collection.toString());
  }


  /**
   * Limit results to basic (non-aggregate) products, i.e. exclude bundles/collections
   */
  public RegistrySearchRequestBuilder onlyBasicProducts() {
    List<FieldValue> excludeValues =
        Arrays.stream(PdsProductClasses.values()).filter(cls -> !cls.isBasicProduct())
            .map(value -> new FieldValue.Builder().stringValue(value.toString()).build()).toList();
    TermsQueryField termsQueryField = new TermsQueryField.Builder().value(excludeValues).build();
    TermsQuery query = new TermsQuery.Builder().field(PdsProductClasses.getPropertyName())
        .terms(termsQueryField).build();

    this.queryBuilder.mustNot(query.toQuery());
    return this;
  }

  /**
   * Add a collection of properties to the response as bucket aggregations.
   * 
   * @param propertyNames a flat list of properties on which to facet
   */
  public RegistrySearchRequestBuilder addPropertyFacets(List<String> propertyNames,
      Integer bucketSize) {
    if (propertyNames != null) {
      for (String propertyName : propertyNames) {
        PdsProperty property = new PdsProperty(propertyName);
        this.aggregations(property.toJsonPropertyString(), field -> field.terms(TermsAggregation
            .of(term -> term.field(property.toOpenPropertyString()).size(bucketSize))));
      }
    }

    return this;
  }

}

