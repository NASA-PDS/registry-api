package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.icu.util.StringTokenizer;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.RequestConstructionContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
import gov.nasa.pds.model.Summary;


public class RequestAndResponseContext implements RequestBuildContext, RequestConstructionContext {
  private static final Logger log = LoggerFactory.getLogger(RequestAndResponseContext.class);

  final private long begin_processing = System.currentTimeMillis();
  final private ControlContext controlContext;
  final private String queryString;
  final private List<String> keywords;
  final private PdsProductIdentifier productIdentifier;
  final private List<String> fields;
  final private List<String> sort;
  final private List<String> searchAfter;
  final private int limit;

  final private boolean singletonResultExpected;
  final private GroupConstraint presetCriteria;
  final private ProductVersionSelector selector;
  final private String format;
  final private Map<String, ProductBusinessLogic> formatters;

  static public RequestAndResponseContext buildRequestAndResponseContext(ControlContext connection, // webby
                                                                                                    // criteria
      UserContext parameters, GroupConstraint outPreset // when first and last node of the endpoint
                                                        // criteria are the same
  ) throws ApplicationTypeException, LidVidNotFoundException, IOException {
    return new RequestAndResponseContext(connection, parameters, outPreset, outPreset);
  }

  static public RequestAndResponseContext buildRequestAndResponseContext(ControlContext connection, // webby
                                                                                                    // criteria
      UserContext parameters, GroupConstraint outPreset, GroupConstraint resPreset // criteria for
                                                                                   // defining last
                                                                                   // node
                                                                                   // (outPreset)
                                                                                   // and first node
                                                                                   // (resOutput)
                                                                                   // for any
                                                                                   // endpoint
  ) throws ApplicationTypeException, LidVidNotFoundException, IOException {
    return new RequestAndResponseContext(connection, parameters, outPreset, resPreset);
  }

  private RequestAndResponseContext(ControlContext controlContext, // webby criteria
      UserContext parameters, GroupConstraint outPreset, GroupConstraint resPreset // criteria for
                                                                                   // defining last
                                                                                   // node
                                                                                   // (outPreset)
                                                                                   // and first node
                                                                                   // (resOutput)
                                                                                   // for any
                                                                                   // endpoint
  ) throws ApplicationTypeException, LidVidNotFoundException, IOException {
    ProductVersionSelector versionSelectionScope =
        outPreset.equals(resPreset) ? parameters.getSelector() : ProductVersionSelector.TYPED;

    Map<String, ProductBusinessLogic> formatters = new HashMap<String, ProductBusinessLogic>();
    formatters.put("*", new PdsProductBusinessObject());
    formatters.put("*/*", new PdsProductBusinessObject());
    formatters.put("application/csv", new WyriwygBusinessObject());
    formatters.put("application/json", new PdsProductBusinessObject());
    formatters.put("application/kvp+json", new WyriwygBusinessObject());
    formatters.put("application/vnd.nasa.pds.pds4+json", new Pds4ProductBusinessObject(true));
    formatters.put("application/vnd.nasa.pds.pds4+xml", new Pds4ProductBusinessObject(false));
    formatters.put("application/xml", new PdsProductBusinessObject());
    formatters.put("text/csv", new WyriwygBusinessObject());
    formatters.put("text/html", new PdsProductBusinessObject());
    formatters.put("text/xml", new PdsProductBusinessObject());

    this.controlContext = controlContext;
    this.formatters = formatters;
    this.format = this.find_match(parameters.getAccept());
    this.queryString = parameters.getQuery();
    this.keywords = parameters.getKeywords();
    this.fields = new ArrayList<String>();
    this.fields.addAll(this.add_output_needs(parameters.getFields()));
    this.productIdentifier = LidVidUtils.resolve(parameters.getIdentifier(), versionSelectionScope,
        controlContext, RequestBuildContextFactory
            .given(parameters.getSelector() == ProductVersionSelector.LATEST, fields, resPreset));
    this.searchAfter = parameters.getSearchAfterValues();
    this.limit = parameters.getLimit();
    this.singletonResultExpected = parameters.getSingletonResultExpected();
    this.sort = parameters.getSortFields();
    this.presetCriteria = outPreset;
    this.selector = parameters.getSelector();
  }


  @Override
  public List<String> getKeywords() {
    return this.keywords;
  }

  @Override
  public Map<String, List<String>> getKeyValuePairs() {
    return new HashMap<String, List<String>>();
  }

  @Override

  public PdsProductIdentifier getProductIdentifier() {
    return this.productIdentifier;
  }

  public String getProductIdentifierString() {
    return this.productIdentifier.toString();
  }

  public final List<String> getFields() {
    return this.fields;
  }

  public final List<String> getSort() {
    return this.sort;
  }

  public List<String> getSearchAfter() {
    return this.searchAfter;
  }

  public int getLimit() {
    return this.limit;
  }

  @Override
  public String getQueryString() {
    return this.queryString;
  }

  public final GroupConstraint getPresetCriteria() {
    return this.presetCriteria;
  };

  public ProductVersionSelector getSelector() {
    return this.selector;
  }

  public boolean isSingular() {
    return this.singletonResultExpected;
  }

  @Override
  public boolean isTerm() {
    return true;
  } // no way to make this decision here so always term for lidvid

  @Override
  public boolean justLatest() {
    boolean specificVersionRequested = this.productIdentifier instanceof PdsLidVid;
    return getSelector() == ProductVersionSelector.LATEST && !specificVersionRequested;
  }

  private List<String> add_output_needs(List<String> given) throws ApplicationTypeException {
    List<String> complete = new ArrayList<String>();
    String max_needs[] = {}, min_needs[] = {};
    given = SearchUtil.jsonPropertyToOpenProperty(given);

    if (this.formatters.containsKey(this.format)) {
      this.formatters.get(this.format).setObjectMapper(this.controlContext.getObjectMapper());
      max_needs = SearchUtil.jsonPropertyToOpenProperty(
          this.formatters.get(this.format).getMaximallyRequiredFields());
      min_needs = SearchUtil.jsonPropertyToOpenProperty(
          this.formatters.get(this.format).getMinimallyRequiredFields());
    } else {
      String known = String.join(", ", this.formatters.keySet());
      log.warn("The Accept header value " + String.valueOf(this.format)
          + " is not supported, supported values are " + known);
      throw new ApplicationTypeException("The Accept header value " + String.valueOf(this.format)
          + " is not supported, supported values are " + known);
    }

    /*
     * if the URL contains fields, then make sure the minimum was included too OR there is maximum
     * set.
     */
    if ((given != null && 0 < given.size()) || 0 < max_needs.length) {
      if (given != null)
        complete.addAll(given);

      for (int index = 0; index < min_needs.length; index++) {
        if (!complete.contains(min_needs[index]))
          complete.add(min_needs[index]);
      }
    }

    if (0 < max_needs.length) {
      List<String> allowed = Arrays.asList(max_needs);
      List<String> filtered = new ArrayList<String>();

      for (String field : complete) {
        if (allowed.contains(field)) {
          filtered.add(field);
        }
      }
      complete = filtered;
    }

    return complete;
  }

  private String find_match(String from_user) {
    String match = from_user;
    StringTokenizer mimes = new StringTokenizer(from_user, ",");

    while (mimes.hasMoreTokens()) {
      /* separate the mime_type/mime_subtype from ;* stuff */
      String mime = mimes.nextToken();
      if (mime.contains(";"))
        mime = mime.substring(0, mime.indexOf(";"));

      if (this.formatters.keySet().contains(mime)) {
        match = mime;
        break;
      }
    }
    log.info("Matched output type as '" + match + "' from '" + from_user + "'.");
    return match;
  }

  public Object getResponse() throws NothingFoundException {
    Object response = this.formatters.get(this.format).getResponse();

    if (response == null) {
      log.warn("Could not find any data given these conditions");
      log.warn("   fields (whitespace-normalized): " + String.valueOf(this.getFields().size()));
      for (String field : this.getFields())
        log.warn("      " + StringUtils.normalizeSpace(field));
      log.warn("   keyword (whitespace-normalized): " + String.valueOf(this.getKeywords().size()));
      for (String keyword : this.getKeywords())
        log.warn("    " + StringUtils.normalizeSpace(keyword));
      log.warn("   lidvid (whitespace-normalized): " + StringUtils.normalizeSpace(this.getProductIdentifierString()));
      log.warn("   limit: " + String.valueOf(this.getLimit()));
      log.warn("   query string: " + String.valueOf(this.getQueryString()));
      log.warn("   selector: " + String.valueOf(this.getSelector()));
      log.warn("   sorting (whitespace-normalized): " + String.valueOf(this.getSort().size()));
      for (String sort : this.getSort())
        log.warn("      " + StringUtils.normalizeSpace(sort));
      log.warn("   searchAfter: " + String.valueOf(this.getSearchAfter()));
      throw new NothingFoundException();
    }
    return response;
  }

  public void setResponse(HitIterator hits, int real_total) {
    Summary summary = new Summary();
    summary.setQ(this.getQueryString());
    summary.setSearchAfter(this.getSearchAfter());
    summary.setLimit(this.getLimit());
    summary.setSort(this.getSort());
    summary.setHits(this.formatters.get(this.format).setResponse(hits, summary, this.fields));
    summary.setProperties(new ArrayList<String>());

    if (0 < real_total)
      summary.setHits(real_total);

    summary.setTook((int) (System.currentTimeMillis() - this.begin_processing));
  }

  public void setResponse(SearchHits hits) {
    this.setResponse(hits, null);
  }

  public void setResponse(SearchHits hits, List<String> uniqueProperties) {
    if (hits != null)
      this.setResponse(hits, uniqueProperties, (int) hits.getTotalHits().value);
  }

  public void setResponse(SearchHits hits, List<String> uniqueProperties, int total_hits) {
    if (hits != null) {
      Summary summary = new Summary();
      summary.setQ(this.getQueryString());
      summary.setSearchAfter(this.getSearchAfter());
      summary.setLimit(this.getLimit());
      summary.setSort(this.getSort());
      summary.setHits(total_hits);

      if (uniqueProperties != null)
        summary.setProperties(uniqueProperties);
      this.formatters.get(this.format).setResponse(hits, summary, this.fields);

      summary.setTook((int) (System.currentTimeMillis() - this.begin_processing));
    }
  }

  public void setResponse(RestHighLevelClient client, SearchRequest request) throws IOException {
    if (this.isSingular()) {
      SearchHits hits;

      request.source().size(2);
      request.source().from(0);
      hits = client.search(request, RequestOptions.DEFAULT).getHits();

      if (hits != null && hits.getTotalHits() != null) {
        long hitCount = hits.getTotalHits().value;
        long distinctHitCount = Arrays.stream(hits.getHits()).map(SearchHit::getId).distinct().count();
        if (distinctHitCount == 1L) {
          SearchHit hit = hits.getAt(0);
          if (hitCount > 1L) {
            log.info("Multiple ("+ hitCount +") hits for id '" + hit.getId() + "'. "
                + "This may be due to duplication across multiple clusters. De-duplicating to single result.");
          }

          this.formatters.get(this.format).setResponse(hit, this.fields);
        } else if (distinctHitCount > 1L) {
          String basicErrMsg =
              "Got " + distinctHitCount + " distinct hits (" + hitCount + " total) "
                  + "for a query which should have returned a singular result. "
                  + "Is provenance metadata present and up-to-date?";
          log.error(basicErrMsg + " Query was " + request.source().query().toString());
          throw new IOException(basicErrMsg);
        }
      } else {
        log.error(
            "Registry returned unexpected response (could not parse hits count from response)");
        throw new IOException(
            "Registry returned unexpected response (could not parse hits count from response)");
      }
    } else {

      request.source().size(this.getLimit());
      this.getSort().forEach(request.source()::sort);  // Currently, no user-set sort order is implemented
      if (!String.join("", this.getSearchAfter()).isEmpty()) {  // if searchAfter contains no non-empty values
        request.source().searchAfter(this.getSearchAfter().toArray());
      }

      long responseStart = System.currentTimeMillis();
      SearchResponse response = client.search(request, RequestOptions.DEFAULT);
      long responseStop = System.currentTimeMillis();
      long responseElapsed = responseStop - responseStart;

      long getHitsStart = responseStop;
      this.setResponse(response.getHits());
      long getHitsStop = System.currentTimeMillis();
      long getHitsElapsed = getHitsStop - getHitsStart;

      System.out.println("response took " + responseElapsed + "ms.  getHits took " + getHitsElapsed + "ms");
    }
  }
}
