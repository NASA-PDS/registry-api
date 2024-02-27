package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.QuickSearch;
import org.opensearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

/**
 * Bundle Data Access Object (DAO). Provides methods to get bundle information from opensearch.
 *
 * @author karpenko
 */
@Immutable
class RefLogicBundle extends RefLogicAny implements ReferencingLogic {
  private static final Logger log = LoggerFactory.getLogger(RefLogicBundle.class);


  @Override
  public GroupConstraint constraints() {
    Map<String, List<String>> preset = new HashMap<String, List<String>>();
    preset.put("product_class", Arrays.asList("Product_Bundle"));
    return GroupConstraintImpl.buildAll(preset);
  }

  /**
   * Get collections of a bundle by bundle LIDVID. If a bundle has LIDVID collection references,
   * then those collections are returned. If a bundle has LID collection references, then the latest
   * versions of collections are returned.
   *
   * @return a list of collection LIDVIDs
   * @throws IOException IO exception
   */
  static private List<PdsLidVid> getAllBundleCollectionLidVids(ControlContext ctrlContext, PdsLidVid bundleLidvid) throws IOException {
    List<String> collectionPropertyKeys = List.of("ref_lid_collection", "ref_lid_collection_secondary");
    SearchRequest collectionReferencesRequest =
        new SearchRequestFactory(RequestConstructionContextFactory.given(bundleLidvid.toString()),
            ctrlContext.getConnection())
                .build(
                    RequestBuildContextFactory.given(false, collectionPropertyKeys,
                        ReferencingLogicTransmuter.Bundle.impl().constraints()),
                    ctrlContext.getConnection().getRegistryIndex());

    //    Retrieve member collection LIDVIDs
    List<PdsLidVid> results = new ArrayList<>();
    for (final Map<String, Object> kvp : new HitIterator(ctrlContext.getConnection().getRestHighLevelClient(), collectionReferencesRequest)) {
      collectionPropertyKeys.forEach(
          key -> {
            Object referencesRawObj = kvp.get(key);
            if (referencesRawObj == null) return;

            List<String> refStrings = (List<String>) referencesRawObj;
            Set<PdsLidVid> collectionRefLidVids = refStrings.stream().filter(PdsProductIdentifier::stringIsLidvid).map(PdsLidVid::fromString).collect(Collectors.toSet());
            Set<String> lidRefStrs = refStrings.stream().filter(PdsProductIdentifier::stringIsLid).collect(Collectors.toSet());
            for (String lidRef : lidRefStrs) {
              try {
                PdsLid lid = PdsLid.fromString(lidRef);
                PdsLidVid latestLidvid = LidVidUtils.getLatestLidVidByLid(ctrlContext, RequestBuildContextFactory.given(true, "_id"), lid);
                collectionRefLidVids.add(latestLidvid);
              } catch (IOException | LidVidNotFoundException e) {
                log.warn("Failed to find extant LIDVID for given LID: " + lidRef);
              }
            }

            results.addAll(collectionRefLidVids);
          });
    }

    return results;
  }


  @Override
  public RequestAndResponseContext member(ControlContext ctrlContext, UserContext searchContext,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException {

    List<PdsLidVid> collectionLidvids = getAllBundleCollectionLidVids(ctrlContext, PdsLidVid.fromString(searchContext.getProductIdentifierStr()));
    List<String> collectionLidvidStrs = collectionLidvids.stream().map(PdsLidVid::toString).collect(Collectors.toList());
    GroupConstraint collectionMemberSelector = GroupConstraintImpl.buildAny(Map.of("_id", collectionLidvidStrs));
    if (twoSteps) {
//      Current behaviour is to return all non-aggregate products referencing this bundle's LID or LIDVID as a parent.
//      This may not be desirable as it *may* end up inconsistent with "the member products of the collections returned
//      by the non-twoSteps query", but this is simple to change later once desired behaviour is ironed out.
      GroupConstraint nonAggregateSelector = ReferencingLogicTransmuter.getBySwaggerGroup("non-aggregate-products").impl().constraints();
      List<String> bundleAlternateIds = QuickSearch.getValues(ctrlContext.getConnection(), false, searchContext.getProductIdentifierStr(), "alternate_ids");
      GroupConstraint memberSelector = GroupConstraintImpl.buildAny(Map.of("ops:Provenance/ops:parent_bundle_identifier", bundleAlternateIds));
      GroupConstraint nonAggregateMemberSelector = nonAggregateSelector.union(memberSelector);
      return rrContextFromConstraint(ctrlContext, searchContext, nonAggregateMemberSelector);
    } else {
      return rrContextFromConstraint(ctrlContext, searchContext, collectionMemberSelector);
    }
  }

  @Override
  public RequestAndResponseContext memberOf(ControlContext context, UserContext input,
      boolean twoSteps) throws MembershipException {
    throw new MembershipException(input.getIdentifier().toString(), "member-of", "bundle");
  }

}
