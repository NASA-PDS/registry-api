package gov.nasa.pds.api.registry.model;

import java.io.IOException;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.controller.URIParametersBuilder;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import gov.nasa.pds.api.registry.search.QuickSearch;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

@Immutable
class RefLogicAny implements ReferencingLogic {
  @Override
  public GroupConstraint constraints() {
    return GroupConstraintImpl.empty();
  }

//  TODO: Clarify what this does and whether it can be excised --- edunn
  private ReferencingLogicTransmuter resolveID(ControlContext context, UserContext input)
      throws IOException, LidVidNotFoundException, UnknownGroupNameException {
    PdsProductIdentifier productIdentifier = LidVidUtils.resolve(input.getIdentifier(),
        ProductVersionSelector.TYPED, context, RequestBuildContextFactory.empty());
    return ReferencingLogicTransmuter.getByProductClass(QuickSearch.getValue(
        context.getConnection(), input.getSelector() == ProductVersionSelector.LATEST,
        productIdentifier != null ? productIdentifier.toString() : "", "product_class"));
  }

  private RequestAndResponseContext search(ControlContext context, UserContext input,
      boolean isIdToGroup) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      UnknownGroupNameException {

//    check git blame for previous implementation if this exception is ever encountered --- edunn 20231130
    throw new IOException("DEVELOPMENT THROW - HOPEFULLY THIS ISN'T NECESSARY AND CAN BE EXCISED");
  }

  @Override
  public RequestAndResponseContext find(ControlContext context, UserContext input)
      throws ApplicationTypeException, IOException, LidVidNotFoundException,
      UnknownGroupNameException {
    return this.search(context, input, false);
  }

  @Override
  public RequestAndResponseContext given(ControlContext context, UserContext input)
      throws ApplicationTypeException, IOException, LidVidNotFoundException,
      UnknownGroupNameException {
    return this.search(context, input, true);
  }

  @Override
  public RequestAndResponseContext member(ControlContext context, UserContext input,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      MembershipException, UnknownGroupNameException {
    throw new RuntimeException("member() is not defined for arbitrary products - requires concrete product type");
  }

  @Override
  public RequestAndResponseContext memberOf(ControlContext context, UserContext input,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      MembershipException, UnknownGroupNameException {
    throw new RuntimeException("memberOf() is not defined for arbitrary products - requires concrete product type");
  }

  /*
  Given a control and user context, and a constraint specifying a membership-related subset of documents, return a
  RequestAndResponseContext to yield that subset.
  Incorporates a workaround to prevent the initial target product's identifier from intefering with the query resolution
   */
  RequestAndResponseContext rrContextFromConstraint(ControlContext ctrlContext, UserContext userContext, GroupConstraint constraint) throws IOException, ApplicationTypeException, LidVidNotFoundException {
    // Reset identifier to prevent it being applied as a filter during query, which would result in zero hits
    UserContext newUserContext = URIParametersBuilder.fromInstance(userContext).setIdentifier("").build();

    RequestAndResponseContext rrContext =
            RequestAndResponseContext.buildRequestAndResponseContext(
                    ctrlContext, newUserContext, constraint);
    rrContext.setResponse(
            ctrlContext.getConnection().getRestHighLevelClient(),
            new SearchRequestFactory(rrContext, ctrlContext.getConnection())
                    .build(rrContext, ctrlContext.getConnection().getRegistryIndex()));
    return rrContext;
  }
}
