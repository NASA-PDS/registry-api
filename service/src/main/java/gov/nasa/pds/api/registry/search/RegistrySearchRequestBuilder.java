package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.FieldAndFormat;
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
import gov.nasa.pds.api.registry.model.EntityProduct;
import gov.nasa.pds.api.registry.model.SearchUtil;
import gov.nasa.pds.api.registry.model.exceptions.SortSearchAfterMismatchException;
import gov.nasa.pds.api.registry.model.exceptions.UnparsableQParamException;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;


public class RegistrySearchRequestBuilder extends SearchRequest.Builder{

  private static final Logger log = LoggerFactory.getLogger(RegistrySearchRequestBuilder.class);

  private static final ArrayList<FieldAndFormat> STATIC_FIELDANDFORMATS =
      new ArrayList<FieldAndFormat>() {
        {
          for (String prop : EntityProduct.JSON_PROPERTIES) {
            add(new FieldAndFormat.Builder().field(prop).build());
          }
        }
      };


  private ConnectionContext connectionContext;
  private List<String> registryIndices;
  // we would prefer to only use the BoolQuery.Builder as a private attribute
  // but we are missing a copy method on the BoolQuery.Builder,
  // so we use the inner properties of the BoolQuery.Builder to keep its state and copy it when
  // needed, for each user request.
  List<Query> must = new ArrayList<Query>();
  List<Query> mustNot = new ArrayList<Query>();

  SearchRequest.Builder searchRequestBuilder = null;


  public RegistrySearchRequestBuilder(ConnectionContext connectionContext) {
//    edunn TODO: Evaluate what can be taken out of the constructor

    this.connectionContext = connectionContext;

    this.registryIndices = this.connectionContext.getRegistryIndices();
    log.info("Use indices: " + String.join(",", registryIndices) + "End indices");

    this.index(registryIndices);

    // add archive status filter
    List<String> archiveStatus = this.connectionContext.getArchiveStatus();
    List<FieldValue> archiveStatusFieldValues = archiveStatus.stream().map(FieldValue::of).toList();
    log.info("Only publishes archiveStatus: " + String.join(",", archiveStatus));
    TermsQueryField archiveStatusTerms =
        new TermsQueryField.Builder().value(archiveStatusFieldValues).build();


    TermsQuery archiveStatusQuery = new TermsQuery.Builder()
        .field("ops:Tracking_Meta/ops:archive_status").terms(archiveStatusTerms).build();

    this.must.add(archiveStatusQuery.toQuery());



  }

  public List<Query> getMust() {
    return must;
  }

  public List<Query> getMustNot() {
    return mustNot;
  }

  public RegistrySearchRequestBuilder(RegistrySearchRequestBuilder registrySearchRequestBuilder) {

    this.connectionContext = registrySearchRequestBuilder.getConnectionContext();
    this.registryIndices = registrySearchRequestBuilder.getRegistryIndices();

    this.index(registryIndices);

    this.must = new ArrayList<Query>(registrySearchRequestBuilder.getMust());
    this.mustNot = new ArrayList<Query>(registrySearchRequestBuilder.getMustNot());

  }



  public RegistrySearchRequestBuilder onlyLatest() {

    ExistsQuery supersededByExists =
        new ExistsQuery.Builder().field("ops:Provenance/ops:superseded_by").build();

    this.mustNot.add(supersededByExists.toQuery());

    return this;

  }

  /**
   * Add a constraint that a given field name must match the given field value
   * @param fieldName the name of the field in OpenSearch format
   * @param value the value which must be present in the given field
   */
  public RegistrySearchRequestBuilder mustMatch(String fieldName, String value) {
    FieldValue fieldValue = new FieldValue.Builder().stringValue(value).build();
    MatchQuery lidvidMatch = new MatchQuery.Builder().field(fieldName).query(fieldValue).build();

    this.must.add(lidvidMatch.toQuery());

    return this;

  }
  /**
   * Add a constraint that a given field name must match the given field value
   * @param fieldName the name of the field in OpenSearch format
   * @param identifier the PDS identifier whose string representation must be present in the given field
   */
  public RegistrySearchRequestBuilder mustMatch(String fieldName, PdsProductIdentifier identifier) {
    return this.mustMatch(fieldName, identifier.toString());
  }

  public RegistrySearchRequestBuilder matchLidvid(PdsProductIdentifier identifier) {
    return this.mustMatch("_id", identifier);
  }

  public RegistrySearchRequestBuilder matchLid(PdsProductIdentifier identifier) {
    return this.mustMatch("lid", identifier);
  }

  public RegistrySearchRequestBuilder paginates(Integer pageSize, List<String> sortFieldNames,
      List<String> searchAfterFieldValues) throws SortSearchAfterMismatchException {
    if ((sortFieldNames != null) && (!sortFieldNames.isEmpty())) {
      this.sortFromStrings(sortFieldNames);
    }

    this.size(pageSize);

    if ((searchAfterFieldValues != null) && (!searchAfterFieldValues.isEmpty())) {
      if (sortFieldNames == null) {
        throw new SortSearchAfterMismatchException("sort argument must be provided if searchAfter argument is provided");
      } else if (searchAfterFieldValues.size() != sortFieldNames.size()) {
        throw new SortSearchAfterMismatchException("sort and searchAfter arguments must be of equal length if provided");
      }
      this.searchAfterFromStrings(searchAfterFieldValues);
    }

    return this;

  }

  /**
   * Implements an alternative to .sort() that accepts strings in API property format.
   * Currently hardcoded to sort in ascending order only.
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
   * Implements an alternative to .searchAfter() that accepts values as strings.
   * No-op in current version of OpenSearch client, but a later version will require the commented-out
   * implementation to convert the Strings to FieldValues
   * @param searchAfterValues
   */
  public RegistrySearchRequestBuilder searchAfterFromStrings(List<String> searchAfterValues) {
    /*
     * List<FieldValue> fieldValues = new ArrayList<FieldValue>();
     * 
     * for (String fieldValue : searchAfter) { fieldValues.add(new
    // TODO check if the number value need to be handled specfically. Method stringValue() implies yes
     * FieldValue.Builder().stringValue(fieldValue).build()); }
     */
    this.searchAfter(searchAfterValues);


    return this;
  }


  public RegistrySearchRequestBuilder addQParam(String q) throws UnparsableQParamException {

    try {
      if ((q != null) && (q.length() > 0)) {
        BoolQuery qBoolQuery = RegistrySearchRequestBuilder.parseQueryString(q);
        this.must.add(qBoolQuery.toQuery());
      }
      return this;
    } catch (RecognitionException | ParseCancellationException e) {
      log.info("Unable to parse q " + q + "error message is " + e);
      throw new UnparsableQParamException(
          "q string value:" + q + " Error message " + e.getMessage());
    }


  }

  public RegistrySearchRequestBuilder addKeywordsParam(List<String> keywords) {

    // TODO implement
    return this;
  }



  public SearchRequest build() {
// edunn TODO: this override will probably go away once the rework is ironed out - everything necessary should have been
//        applied prior to calling build(), and universal stuff like trackTotalHits() should be moved to the constructor
    BoolQuery boolQuery = new BoolQuery.Builder().must(this.must).mustNot(this.mustNot).build();

    this.query(q -> q.bool(boolQuery))
        .trackTotalHits(t -> t.enabled(true));

    return super.build();

  }



  public ConnectionContext getConnectionContext() {
    return connectionContext;
  }

  public List<String> getRegistryIndices() {
    return registryIndices;
  }

  public SearchRequest.Builder getSearchRequestBuilder() {
    return searchRequestBuilder;
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
   * Implements an alternative to .fields() that accepts values as strings.
   * @param fieldNames
   */
  public RegistrySearchRequestBuilder fieldsFromStrings(List<String> fieldNames) {

    if ((fieldNames == null) || (fieldNames.isEmpty())) {
      return this;
    } else {
      log.info("restricting list of fields requested from OpenSearch.");
      // TODO refine to only pull the static field when the output response requires it.
      List<String> openSearchField =
          new ArrayList<String>(Arrays.asList(EntityProduct.JSON_PROPERTIES));
      for (String field : fieldNames) {
        openSearchField.add(SearchUtil.jsonPropertyToOpenProperty(field));
      }

      SourceFilter sourceFilter = new SourceFilter.Builder().includes(openSearchField).build();
      SourceConfig limitedSourceCfg = new SourceConfig.Builder().filter(sourceFilter).build();

      this.source(limitedSourceCfg);

      return this;
    }

  }



}

