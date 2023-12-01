package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.QuickSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.LidvidsContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

import static gov.nasa.pds.api.registry.model.identifiers.LidVidUtils.getAllLidVidsByLids;

@Immutable
class RefLogicNonAggregateProduct extends RefLogicAny implements ReferencingLogic {
  private static final Logger log = LoggerFactory.getLogger(RefLogicNonAggregateProduct.class);

  @Override
  public GroupConstraint constraints() {
    Map<String, List<String>> preset = new HashMap<String, List<String>>();
    preset.put("product_class", Arrays.asList("Product_Bundle", "Product_Collection"));
    return GroupConstraintImpl.buildNot(preset);
  }

  static Pagination<String> grandparents(ControlContext control, ProductVersionSelector selection,
      LidvidsContext uid) throws IOException, LidVidNotFoundException {
    log.info("Find the grandparents of a product -- both all and latest");
    List<String> parents = RefLogicNonAggregateProduct
        .parents(control, ProductVersionSelector.LATEST, new Unlimited(uid.getLidVid())).page();
    PaginationLidvidBuilder grandparents = new PaginationLidvidBuilder(uid);
    for (String parent : parents) {
      log.info("Find all the parents of collection: " + parent);
      grandparents
          .addAll(RefLogicCollection.parents(control, selection, new Unlimited(parent)).page());
      log.info("Find grandparents size: " + String.valueOf(grandparents.size()));
      for (String gp : grandparents.page()) {
        log.info("   grandparent: " + gp);
      }
    }
    return grandparents;
  }

  static Pagination<String> parents(ControlContext control, ProductVersionSelector selection,
      LidvidsContext uid) throws IOException, LidVidNotFoundException {
    List<String> sortedLidStrings;
    PaginationLidvidBuilder parents = new PaginationLidvidBuilder(uid);
    Set<String> lids = new HashSet<String>();

    log.info("Find the parents of a product -- both all and latest");
    for (final Map<String, Object> kvp : new HitIterator(
        control.getConnection().getRestHighLevelClient(),
        new SearchRequestFactory(
            RequestConstructionContextFactory.given("product_lidvid", uid.getLidVid(), true),
            control.getConnection()).build(RequestBuildContextFactory.given(true, "collection_lid"),
                control.getConnection().getRegistryRefIndex()))) {
      lids.addAll(parents.convert(kvp.get("collection_lid")));
    }
    sortedLidStrings = new ArrayList<>(lids);
    Collections.sort(sortedLidStrings);
    List<PdsProductIdentifier> sortedLids =
        sortedLidStrings.stream().map(PdsLid::fromString).collect(Collectors.toList());

    if (selection == ProductVersionSelector.ALL) {
      parents.addAll(LidVidUtils.getAllLidVidsByLids(control, RequestBuildContextFactory.empty(),
          sortedLidStrings));
    } else {
      RequestBuildContext reqContext = RequestBuildContextFactory.empty();
      for (PdsProductIdentifier id : sortedLids) {
        try {
          PdsLidVid latestLidvid =
              LidVidUtils.getLatestLidVidByLid(control, reqContext, id.getLid().toString());
          parents.add(latestLidvid.toString());
        } catch (LidVidNotFoundException e) {
          log.warn(
              "LID is referenced but is in non-findable archive-status or does not exist in db: "
                  + e.toString());
        }
      }
    }

    return parents;
  }

  @Override
  public RequestAndResponseContext memberOf(
      ControlContext ctrlContext, UserContext searchContext, boolean twoSteps)
      throws ApplicationTypeException, IOException, LidVidNotFoundException {
    String ancestorMetadataKey =
        twoSteps
            ? "ops:Provenance/ops:parent_bundle_identifier"
            : "ops:Provenance/ops:parent_collection_identifier";
    ReferencingLogicTransmuter ancestorClass =
        twoSteps ? ReferencingLogicTransmuter.Bundle : ReferencingLogicTransmuter.Collection;

    List<String> ancestorIdentifiers =
        QuickSearch.getValues(
            ctrlContext.getConnection(), false, searchContext.getLidVid(), ancestorMetadataKey);

    //    Get all the LIDVID refs, resolve the LID refs to all relevant LIDVIDs, then add them all
    // together
    Set<String> parentLidvids =
        ancestorIdentifiers.stream()
            .filter(PdsProductIdentifier::stringIsLidvid)
            .collect(Collectors.toSet());
    List<String> parentLids =
        ancestorIdentifiers.stream()
            .filter(PdsProductIdentifier::stringIsLid)
            .collect(Collectors.toList());
    List<String> implicitParentLidvids =
        getAllLidVidsByLids(
            ctrlContext,
            RequestBuildContextFactory.given(false, "lid", ancestorClass.impl().constraints()),
            parentLids);
    parentLidvids.addAll(implicitParentLidvids);

    GroupConstraint ancestorProductTypeContstraints = ancestorClass.impl().constraints();
    GroupConstraint ancestorSelectorConstraint =
        GroupConstraintImpl.buildAny(Map.of("_id", new ArrayList<>(parentLidvids)));
    ancestorProductTypeContstraints.union(ancestorSelectorConstraint);

    return rrContextFromConstraint(ctrlContext, searchContext, ancestorSelectorConstraint);
  }
}
