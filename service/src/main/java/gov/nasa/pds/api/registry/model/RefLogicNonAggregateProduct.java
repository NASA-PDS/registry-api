package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.QuickSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
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
            ctrlContext.getConnection(), false, searchContext.getProductIdentifierStr(), ancestorMetadataKey);

    //    Get all the LIDVID refs, resolve the LID refs to all relevant LIDVIDs, then add them all
    // together
    Set<String> parentLidvidStrings =
        ancestorIdentifiers.stream()
            .filter(PdsProductIdentifier::stringIsLidvid)
            .collect(Collectors.toSet());
    List<PdsLid> parentLids =
        ancestorIdentifiers.stream()
            .map(PdsLid::fromString)
            .filter(PdsLid::isLid)
            .collect(Collectors.toList());
    List<PdsLidVid> implicitParentLidvids =
        getAllLidVidsByLids(
            ctrlContext,
            RequestBuildContextFactory.given(false, "lid", ancestorClass.impl().constraints()),
            parentLids);
    List<String> implicitParentLidvidStrings = implicitParentLidvids.stream().map(PdsLidVid::toString).collect(Collectors.toList());
    parentLidvidStrings.addAll(implicitParentLidvidStrings);

    GroupConstraint ancestorProductTypeContstraints = ancestorClass.impl().constraints();
    GroupConstraint ancestorSelectorConstraint =
        GroupConstraintImpl.buildAny(Map.of("_id", new ArrayList<>(parentLidvidStrings)));
    ancestorProductTypeContstraints.union(ancestorSelectorConstraint);

    return rrContextFromConstraint(ctrlContext, searchContext, ancestorSelectorConstraint);
  }
}
