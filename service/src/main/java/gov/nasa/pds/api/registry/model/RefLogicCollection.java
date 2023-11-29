package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.LidvidsContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.RequestConstructionContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.controller.URIParametersBuilder;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.QuickSearch;
import org.opensearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
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

  static Pagination<String> children(ControlContext control, ProductVersionSelector selection,
      LidvidsContext uid) throws IOException, LidVidNotFoundException {
    return selection == ProductVersionSelector.ALL ? RefLogicCollection.childrenAll(control, uid)
        : RefLogicCollection.childrenLatest(control, uid);
  }

  private static Pagination<String> childrenAll(ControlContext control, LidvidsContext searchContext)
      throws IOException, LidVidNotFoundException {
    // TODO: Fully convert this function's internals (and eventually, interface) to use
    //  PdsProductIdentifier classes instead of strings

    PaginationLidvidBuilder bundleLidvidsPager = new PaginationLidvidBuilder(searchContext);

//    Get all non-aggregate products having this collection an ancestor

    GroupConstraint productTypeConstraints = ReferencingLogicTransmuter.NonAggregateProduct.impl().constraints();
    Map<String, List<String>> constraintFilter = new HashMap<>(productTypeConstraints.filter());
    Map<String, List<String>> constraintMust = new HashMap<>(productTypeConstraints.must());
    Map<String, List<String>> constraintMustNot = productTypeConstraints.mustNot();

    List<String> targetProductAlternateIds = QuickSearch.getValues(control.getConnection(), false, searchContext.getLidVid(), "alternate_ids");
    constraintFilter.put("ops:Provenance/ops:parent_collection_identifier", targetProductAlternateIds);
    GroupConstraint searchConstraints = GroupConstraintImpl.build(constraintMust, constraintFilter, constraintMustNot);


    RequestBuildContext childSearchReqBuildContext =
        RequestBuildContextFactory.given(false, "lidvid", searchConstraints);

    SearchRequest searchRequest= new SearchRequestFactory(RequestConstructionContextFactory.minimal(), control.getConnection()).build(childSearchReqBuildContext,
            control.getConnection().getRegistryIndex());


//    TODO NEXT : FIGURE OUT SEARCHAFTER PAGING, RIP OUT PaginationLidVidBuilder AND IMPLEMENT SEARCH-AFTER PAGING OF
//     THE QUERY RESULTS

//    TODO: FIGURE OUT /all functionality

//    Add the lidvids of all those products to the pager
    for (final Map<String, Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(), searchRequest)) {
      bundleLidvidsPager.addLidvid((String) kvp.get("lidvid"));
    }

    return bundleLidvidsPager;
  }

  private static Pagination<String> childrenLatest(ControlContext control, LidvidsContext searchContext)
      throws IOException, LidVidNotFoundException {
    PaginationLidvidBuilder productLidvids = new PaginationLidvidBuilder(searchContext);
    RequestConstructionContext requestConstructionContext =
        RequestConstructionContextFactory.given("collection_lidvid", searchContext.getLidVid(), true);
    RequestBuildContext requestBuildContext =
        RequestBuildContextFactory.given(true, "product_lidvid");
    String registryRefIndex = control.getConnection().getRegistryRefIndex();
    SearchRequest searchRequest =
        new SearchRequestFactory(requestConstructionContext, control.getConnection())
            .build(requestBuildContext, registryRefIndex);

    HitIterator pagesOfResults =
        new HitIterator(control.getConnection().getRestHighLevelClient(), searchRequest);

    for (final Map<String, Object> page : pagesOfResults) {
      // Every "hit" is a page containing n lidvids, where n is presumably determined by OpenSearch
      // configuration
      Object collectionOfLidvids = page.get("product_lidvid");
      productLidvids.add(collectionOfLidvids);
    }

    return productLidvids;
  }

  static Pagination<String> parents(ControlContext control, ProductVersionSelector selection,
      LidvidsContext searchContext) throws IOException, LidVidNotFoundException {
    // TODO: Fully convert this function's internals (and eventually, interface) to use
    // PdsProductIdentifier classes instead of strings

    PdsProductIdentifier targetProduct = PdsProductIdentifier.fromString(searchContext.getLidVid());
    PaginationLidvidBuilder bundleLidvidsPager = new PaginationLidvidBuilder(searchContext);

    List<String> parentIds = QuickSearch.getValues(control.getConnection(), false, searchContext.getLidVid(), "ops:Provenance/ops:parent_bundle_identifier");

//    Get all the LIDVID refs, convert the LID refs to LIDVIDs, then add them all together
    Set<String> parentLidvids = parentIds.stream().filter(PdsProductIdentifier::isLidvid).collect(Collectors.toSet());
    List<String> parentLids = parentIds.stream().filter(Predicate.not(PdsProductIdentifier::isLidvid)).collect(Collectors.toList());
    List<String> implicitParentLidvids =
        getAllLidVidsByLids(
            control,
            RequestBuildContextFactory.given(
                false, "lid", ReferencingLogicTransmuter.Bundle.impl().constraints()),
            parentLids);
    parentLidvids.addAll(implicitParentLidvids);

    bundleLidvidsPager.addAll(new ArrayList<>(parentLidvids));

    return bundleLidvidsPager;
  }

  @Override
  public RequestAndResponseContext member(
      ControlContext ctrlContext, UserContext userContext, boolean twoSteps)
      throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException {
    if (twoSteps)
      throw new MembershipException(userContext.getIdentifier(), "members/members", "collections");
    GroupConstraint childrenConstraint = getChildrenConstraint(ctrlContext, userContext);

    // Reset identifier to prevent it being applied as a filter during query, which would result in zero hits
    UserContext newUserContext = URIParametersBuilder.fromInstance(userContext).setIdentifier("").build();

    RequestAndResponseContext rrContext =
        RequestAndResponseContext.buildRequestAndResponseContext(
            ctrlContext, newUserContext, childrenConstraint);
    rrContext.setResponse(
        ctrlContext.getConnection().getRestHighLevelClient(),
        new SearchRequestFactory(rrContext, ctrlContext.getConnection())
            .build(rrContext, ctrlContext.getConnection().getRegistryIndex()));
    return rrContext;
  }

  private GroupConstraint getChildrenConstraint(ControlContext control, LidvidsContext searchContext) throws IOException, LidVidNotFoundException {
//    HACKY CONSTRUCTION OF GroupConstraint due to immutability
//    TODO: Fix this
    GroupConstraint productTypeConstraints = ReferencingLogicTransmuter.NonAggregateProduct.impl().constraints();
    Map<String, List<String>> constraintFilter = new HashMap<>(productTypeConstraints.filter());
    Map<String, List<String>> constraintMust = new HashMap<>(productTypeConstraints.must());
    Map<String, List<String>> constraintMustNot = productTypeConstraints.mustNot();

    List<String> targetProductAlternateIds = QuickSearch.getValues(control.getConnection(), false, searchContext.getLidVid(), "alternate_ids");
    constraintFilter.put("ops:Provenance/ops:parent_collection_identifier", targetProductAlternateIds);
    GroupConstraint searchConstraints = GroupConstraintImpl.build(constraintMust, constraintFilter, constraintMustNot);
    return searchConstraints;
  }

  @Override
  public RequestAndResponseContext memberOf(ControlContext context, UserContext input,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      MembershipException, UnknownGroupNameException {
    if (twoSteps)
      throw new MembershipException(input.getIdentifier(), "member-of/member-of", "collections");
    return RequestAndResponseContext.buildRequestAndResponseContext(context, input,
        RefLogicCollection.parents(context, input.getSelector(), input));
  }
}
