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
import org.opensearch.client.opensearch.core.search.TrackHits;
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


public class RegistrySearchRequestBuilder {

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

    this.connectionContext = connectionContext;

    this.registryIndices = this.connectionContext.getRegistryIndices();
    log.info("Use indices: " + String.join(",", registryIndices) + "End indices");

    // add index list
    this.searchRequestBuilder = new SearchRequest.Builder().index(registryIndices);

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

    this.searchRequestBuilder = new SearchRequest.Builder().index(registryIndices);

    this.must = new ArrayList<Query>(registrySearchRequestBuilder.getMust());
    this.mustNot = new ArrayList<Query>(registrySearchRequestBuilder.getMustNot());

  }



  public RegistrySearchRequestBuilder onlyLatest() {

    ExistsQuery supersededByExists =
        new ExistsQuery.Builder().field("ops:Provenance/ops:superseded_by").build();

    this.mustNot.add(supersededByExists.toQuery());

    return this;

  }


  public RegistrySearchRequestBuilder addLidvidMatch(PdsProductIdentifier identifier) {
    // lidvid match
    FieldValue lidvidFieldValue =
        new FieldValue.Builder().stringValue(identifier.toString()).build();

    MatchQuery lidvidMatch = new MatchQuery.Builder().field("_id").query(lidvidFieldValue).build();

    this.must.add(lidvidMatch.toQuery());

    return this;

  }


  public RegistrySearchRequestBuilder addLidMatch(PdsProductIdentifier identifier) {
    // lid match
    FieldValue lidvidFieldValue =
        new FieldValue.Builder().stringValue(identifier.getLid().toString()).build();

    MatchQuery lidMatch = new MatchQuery.Builder().field("lid").query(lidvidFieldValue).build();

    this.must.add(lidMatch.toQuery());

    return this;

  }

  public RegistrySearchRequestBuilder paginates(Integer limit, List<String> sort,
      List<String> searchAfter) throws SortSearchAfterMismatchException {

    if ((sort != null) && (!sort.isEmpty())) {
      this.sort(sort);
    }

    this.size(limit);

    if ((searchAfter != null) && (!searchAfter.isEmpty())) {
      if (sort == null) {
        throw new SortSearchAfterMismatchException("sort argument must be provided if searchAfter argument is provided");
      } else if (searchAfter.size() != sort.size()) {
        throw new SortSearchAfterMismatchException("sort and searchAfter arguments must be of equal length if provided");
      }
      this.searchAfter(searchAfter);
    }

    return this;

  }

  public RegistrySearchRequestBuilder sort(List<String> sort) {

    String openSearchField;

    List<SortOptions> sortOptionsList = new ArrayList<SortOptions>();
    for (String field : sort) {
      openSearchField = SearchUtil.jsonPropertyToOpenProperty(field);
      FieldSort fieldSort =
          new FieldSort.Builder().field(openSearchField).order(SortOrder.Asc).build();
      sortOptionsList.add(new SortOptions.Builder().field(fieldSort).build());
    }

    this.searchRequestBuilder.sort(sortOptionsList);

    return this;

  }

  public RegistrySearchRequestBuilder size(Integer size) {
    this.searchRequestBuilder.size(size);
    return this;
  }

  public RegistrySearchRequestBuilder searchAfter(List<String> searchAfter) {
    // TODO check if the number value need to be handled specfically.


    /*
     * This code is useless with the version of opensearch client we are using From the source code
     * of the opensearch client in a later version, that is what will need to be used. I am keeping
     * it here as a comment.
     * 
     * List<FieldValue> fieldValues = new ArrayList<FieldValue>();
     * 
     * for (String fieldValue : searchAfter) { fieldValues.add(new
     * FieldValue.Builder().stringValue(fieldValue).build()); }
     */
    this.searchRequestBuilder.searchAfter(searchAfter);


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

    BoolQuery boolQuery = new BoolQuery.Builder().must(this.must).mustNot(this.mustNot).build();

    this.searchRequestBuilder = this.searchRequestBuilder
            .query(q -> q.bool(boolQuery))
            .trackTotalHits(t -> t.enabled(true));

    return this.searchRequestBuilder.build();

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

  public RegistrySearchRequestBuilder fields(List<String> fields) {

    if ((fields == null) || (fields.isEmpty())) {
      return this;
    } else {
      log.info("restricting list of fields requested from OpenSearch.");
      // TODO refine to only pull the static field when the output response requires it.
      List<String> openSearchField =
          new ArrayList<String>(Arrays.asList(EntityProduct.JSON_PROPERTIES));
      for (String field : fields) {
        openSearchField.add(SearchUtil.jsonPropertyToOpenProperty(field));
      }

      SourceFilter sourceFilter = new SourceFilter.Builder().includes(openSearchField).build();
      SourceConfig limitedSourceCfg = new SourceConfig.Builder().filter(sourceFilter).build();

      this.searchRequestBuilder.source(limitedSourceCfg);

      return this;
    }

  }



}

