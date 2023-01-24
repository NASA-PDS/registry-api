package gov.nasa.pds.api.registry.model;

import java.io.IOException;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.search.QuickSearch;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

@Immutable
class RefLogicAny implements ReferencingLogic
{
	@Override
    public GroupConstraint constraints()
    { return GroupConstraintImpl.empty(); }

	private boolean isGrandchild (ReferencingLogicTransmuter idType)
	{ return !(idType == ReferencingLogicTransmuter.Bundle || idType == ReferencingLogicTransmuter.Collection); }

	private ReferencingLogicTransmuter resolveID (ControlContext context, UserContext input)
			throws IOException, LidVidNotFoundException, UnknownGroupNameException
	{
		return ReferencingLogicTransmuter.getByProductClass(
				QuickSearch.getValue(context.getConnection(), input.getSelector() == ProductVersionSelector.LATEST,
			             LidVidUtils.resolve(input.getIdentifier(), ProductVersionSelector.SPECIFIC, context, RequestBuildContextFactory.empty()),
			             "product_class"));
	}

	private RequestAndResponseContext search(ControlContext context, UserContext input, boolean isIdToGroup)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException
	{
		// find all of the given group that reference the specified ID
		ReferencingLogicTransmuter groupType = ReferencingLogicTransmuter.getBySwaggerGroup(input.getGroup());
		ReferencingLogicTransmuter idType = this.resolveID(context, input);

		if (idType == ReferencingLogicTransmuter.Bundle && groupType == ReferencingLogicTransmuter.Collection)
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicBundle.children(context, input.getSelector(), input));
		if (idType == ReferencingLogicTransmuter.Bundle && this.isGrandchild(groupType))
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicBundle.grandchildren(context, input.getSelector(), input));
		if (idType == ReferencingLogicTransmuter.Collection && groupType == ReferencingLogicTransmuter.Bundle)
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicCollection.parents(context, input.getSelector(), input));
		if (idType == ReferencingLogicTransmuter.Collection && this.isGrandchild(groupType))
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicCollection.children(context, input.getSelector(), input));
		if (this.isGrandchild(idType) && groupType == ReferencingLogicTransmuter.Bundle)
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicProduct.grandparents(context, input.getSelector(), input));
		if (this.isGrandchild(idType) && groupType == ReferencingLogicTransmuter.Collection)
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicProduct.parents(context, input.getSelector(), input));

		throw new IOException("Waiting on implementation until references are figured out in database");
		// FIXME: return isIdToGroup ? this.idToGroup() : this.groupToId();
	}

	@Override
	public RequestAndResponseContext find(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException
	{ return this.search(context, input, false); }

	@Override
	public RequestAndResponseContext given(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException
	{ return this.search(context, input, true); }

	@Override
	public RequestAndResponseContext member(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException, UnknownGroupNameException
	{
		throw new MembershipException(input.getIdentifier(), "members", "product");
	}

	@Override
	public RequestAndResponseContext memberOf(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException, UnknownGroupNameException
	{
		if (twoSteps)
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicProduct.grandparents(context, input.getSelector(), input));
		return RequestAndResponseContext.buildRequestAndResponseContext
				(context, input, RefLogicProduct.parents(context, input.getSelector(), input));
	}
}
