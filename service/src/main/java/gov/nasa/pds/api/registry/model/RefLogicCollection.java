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
import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
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
      throw new MembershipException(userContext.getIdentifier().toString(), "members/members", "collections");
    PdsLidVid collectionLidvid = PdsLidVid.fromString(userContext.getProductIdentifierStr());
    GroupConstraint childrenConstraint = getChildProductsConstraint(ctrlContext, collectionLidvid);

    return rrContextFromConstraint(ctrlContext, userContext, childrenConstraint);
  }

  private GroupConstraint getChildProductsConstraint(ControlContext control, PdsLidVid parentCollectionLidvid) throws IOException, LidVidNotFoundException {
    List<String> parentCollectionConstraintValues = List.of(parentCollectionLidvid.toString());
    GroupConstraint productClassConstraints = ReferencingLogicTransmuter.NonAggregateProduct.impl().constraints();
    GroupConstraint childrenSelectorConstraint = GroupConstraintImpl.buildAny(Map.of("ops:Provenance/ops:parent_collection_identifier", parentCollectionConstraintValues));
    return productClassConstraints.union(childrenSelectorConstraint);
  }

  @Override
  public RequestAndResponseContext memberOf(ControlContext ctrlContext, UserContext searchContext,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      MembershipException {
    if (twoSteps)
      throw new MembershipException(searchContext.getIdentifier().toString(), "member-of/member-of", "collections");

    List<String> parentIdStrings = QuickSearch.getValues(ctrlContext.getConnection(), false, searchContext.getProductIdentifierStr(), "ops:Provenance/ops:parent_bundle_identifier");

//    Get all the LIDVID strings, convert the LID strings to LIDVID strings, then add them all together
    Set<String> parentLidvidStrings = parentIdStrings.stream().filter(PdsProductIdentifier::stringIsLidvid).collect(Collectors.toSet());
    List<PdsLid> parentLids = parentIdStrings.stream().map(PdsLid::fromString).filter(PdsLid::isLid).collect(Collectors.toList());
    List<PdsLidVid> implicitParentLidvids =
            getAllLidVidsByLids(
                    ctrlContext,
                    RequestBuildContextFactory.given(
                            false, "lid", ReferencingLogicTransmuter.Bundle.impl().constraints()),
                    parentLids);
    List<String> implicitParentLidvidStrings = implicitParentLidvids.stream().map(PdsLidVid::toString).collect(Collectors.toList());
    parentLidvidStrings.addAll(implicitParentLidvidStrings);

    GroupConstraint productClassConstraints = ReferencingLogicTransmuter.NonAggregateProduct.impl().constraints();
    GroupConstraint parentSelectorConstraint =
            GroupConstraintImpl.buildAny(Map.of("_id", new ArrayList<>(parentLidvidStrings)));
    productClassConstraints.union(parentSelectorConstraint);

    return rrContextFromConstraint(ctrlContext, searchContext, parentSelectorConstraint);
  }
}
