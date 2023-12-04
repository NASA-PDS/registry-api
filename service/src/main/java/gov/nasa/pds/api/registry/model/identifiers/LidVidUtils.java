package gov.nasa.pds.api.registry.model.identifiers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import gov.nasa.pds.api.registry.model.ProductVersionSelector;
import gov.nasa.pds.api.registry.model.ReferencingLogicTransmuter;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.search.SearchHit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.LidVidMismatchException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.search.QuickSearch;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;


/**
 * Methods to get latest versions of LIDs
 *
 * @author karpenko
 */
public class LidVidUtils {
  private static final Logger log = LoggerFactory.getLogger(LidVidUtils.class);

  public static PdsLidVid getLatestLidVidByLid(ControlContext ctlContext,
      RequestBuildContext reqContext, PdsLid lid)
      throws IOException, LidVidNotFoundException {

    SearchRequest searchRequest = new SearchRequestFactory(
        RequestConstructionContextFactory.given("lid", lid.toString(), true),
        ctlContext.getConnection()).build(
            RequestBuildContextFactory.given(true, "lidvid", reqContext.getPresetCriteria()),
            ctlContext.getConnection().getRegistryIndex());
    SearchResponse searchResponse = ctlContext.getConnection().getRestHighLevelClient()
        .search(searchRequest, RequestOptions.DEFAULT);

    if (searchResponse != null) {
      List<PdsLidVid> lidVids = new ArrayList<PdsLidVid>();
      for (SearchHit searchHit : searchResponse.getHits()) {
        String lidvidStr = (String) searchHit.getSourceAsMap().get("lidvid");;
        lidVids.add(PdsLidVid.fromString(lidvidStr));
      }

      Collections.sort(lidVids);

      if (lidVids.isEmpty()) {
        throw new LidVidNotFoundException(lid.toString());
      }

      return lidVids.get(lidVids.size() - 1);
    }
    throw new LidVidNotFoundException(lid.toString());
  }

  public static List<String> getAllLidVidsByLids(ControlContext ctlContext,
      RequestBuildContext reqContext, Collection<String> lids) throws IOException {
    List<String> lidvids = new ArrayList<String>();

    if (0 < lids.size()) {
      ctlContext.getConnection().getRestHighLevelClient()
          .search(new SearchRequestFactory(
              RequestConstructionContextFactory.given("lid", new ArrayList<String>(lids), true),
              ctlContext.getConnection()).build(reqContext,
                  ctlContext.getConnection().getRegistryIndex()),
              RequestOptions.DEFAULT)
          .getHits().forEach((hit) -> {
            lidvids.add(hit.getId());
          });
    }
    return lidvids;
  }

  public static PdsProductIdentifier resolve(PdsProductIdentifier productIdentifier,
      ProductVersionSelector scope, ControlContext ctlContext, RequestBuildContext reqContext)
      throws IOException, LidVidNotFoundException {
    PdsProductIdentifier result = null;

    if (productIdentifier != null) {
      /* YUCK! This should use polymorphism in ProductVersionSelector not a switch statement */
      switch (scope) {
        case ALL:
          result = productIdentifier.getLid();
          break;
        case LATEST:
          // Per discussion with Al Niessner, the intended functionality of attempting to resolve a
          // LIDVID with
          // selector LATEST is that it should return the exact product specified by the LIDVID,
          // *not* the latest
          // equivalent product. This is somewhat unintuitive, but ProductVersionSelector's purpose
          // is not actually
          // to force that kind of resolution.
          result = productIdentifier instanceof PdsLidVid ? productIdentifier
              : LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext,
                  productIdentifier.getLid());
          break;
        case TYPED:
          result = productIdentifier instanceof PdsLidVid ? productIdentifier
              : LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext,
                  productIdentifier.getLid());
          break;
        case ORIGINAL:
          throw new LidVidNotFoundException("ProductVersionSelector.ORIGINAL not supported");
        default:
          throw new LidVidNotFoundException("Unknown and unhandles ProductVersionSelector value.");
      }
    }

    return result;
  }

  public static void verify(ControlContext control, UserContext user) throws IOException,
      LidVidMismatchException, LidVidNotFoundException, UnknownGroupNameException {
    ReferencingLogicTransmuter expected_rlt =
        ReferencingLogicTransmuter.getBySwaggerGroup(user.getGroup());

    if (expected_rlt != ReferencingLogicTransmuter.Any) {
      String actual_group =
          QuickSearch.getValue(control.getConnection(), false, user.getLidVid(), "product_class");
      ReferencingLogicTransmuter actual_rlt =
          ReferencingLogicTransmuter.getByProductClass(actual_group);

      if (actual_rlt != expected_rlt)
        throw new LidVidMismatchException(user.getLidVid(), user.getGroup(), actual_group);
    }
  }
}
