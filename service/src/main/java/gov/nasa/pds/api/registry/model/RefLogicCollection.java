package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.QuickSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

import static gov.nasa.pds.api.registry.model.identifiers.LidVidUtils.getAllLidVidsByLids;

@Immutable
class RefLogicCollection extends RefLogicAny implements ReferencingLogic {
  private static final Logger log = LoggerFactory.getLogger(RefLogicCollection.class);

  @Override
  public GroupConstraint constraints() {
    Map<String, List<String>> preset = new HashMap<String, List<String>>();
    preset.put("product_class", Arrays.asList("Product_Collection"));
    return GroupConstraintImpl.buildAll(preset);
  }

  @Override
  public RequestAndResponseContext member(
      ControlContext ctrlContext, UserContext userContext, boolean twoSteps)
      throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException {
    if (twoSteps)
      throw new MembershipException(userContext.getIdentifier(), "members/members", "collections");
    GroupConstraint childrenConstraint = getChildProductsConstraint(ctrlContext, userContext.getLidVid());

    return rrContextFromConstraint(ctrlContext, userContext, childrenConstraint);
  }

  private GroupConstraint getChildProductsConstraint(ControlContext control, String parentCollectionLidvid) throws IOException, LidVidNotFoundException {
//    TODO: targetProductAlternateIds should depend on all/latest/specific behaviour
    List<String> targetProductAlternateIds = QuickSearch.getValues(control.getConnection(), false, parentCollectionLidvid, "alternate_ids");

    GroupConstraint productClassConstraints = ReferencingLogicTransmuter.NonAggregateProduct.impl().constraints();
    GroupConstraint childrenSelectorConstraint = GroupConstraintImpl.buildAny(Map.of("ops:Provenance/ops:parent_collection_identifier", targetProductAlternateIds));
    return productClassConstraints.union(childrenSelectorConstraint);
  }

  @Override
  public RequestAndResponseContext memberOf(ControlContext ctrlContext, UserContext searchContext,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      MembershipException {
    if (twoSteps)
      throw new MembershipException(searchContext.getIdentifier(), "member-of/member-of", "collections");

    List<String> parentIds = QuickSearch.getValues(ctrlContext.getConnection(), false, searchContext.getLidVid(), "ops:Provenance/ops:parent_bundle_identifier");

//    Get all the LIDVID refs, convert the LID refs to LIDVIDs, then add them all together
    Set<String> parentLidvids = parentIds.stream().filter(PdsProductIdentifier::stringIsLidvid).collect(Collectors.toSet());
    List<String> parentLids = parentIds.stream().filter(PdsProductIdentifier::stringIsLid).collect(Collectors.toList());
    List<String> implicitParentLidvids =
            getAllLidVidsByLids(
                    ctrlContext,
                    RequestBuildContextFactory.given(
                            false, "lid", ReferencingLogicTransmuter.Bundle.impl().constraints()),
                    parentLids);
    parentLidvids.addAll(implicitParentLidvids);

    GroupConstraint productClassConstraints = ReferencingLogicTransmuter.NonAggregateProduct.impl().constraints();
    GroupConstraint parentSelectorConstraint =
            GroupConstraintImpl.buildAny(Map.of("_id", new ArrayList<>(parentLidvids)));
    productClassConstraints.union(parentSelectorConstraint);

    return rrContextFromConstraint(ctrlContext, searchContext, parentSelectorConstraint);
  }
}
