package gov.nasa.pds.api.registry.business;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.search.QuickSearch;

public class RefLogicAny implements ReferencingLogic
{
	@Override
    public Map<String,String> constraints()
    {
    	Map<String,String> preset = new HashMap<String,String>();
    	return preset;
    }

	private ReferencingLogicTransmuter resolveID (ControlContext context, UserContext input)
			throws IOException, LidVidNotFoundException, UnknownGroupNameException
	{
		return ReferencingLogicTransmuter.getByProductClass(
				QuickSearch.getValue(context.getConnection(),
			             LidVidUtils.resolve(input.getIdentifier(), ProductVersionSelector.TYPED, context, null),
			             "product_class"));
	}

	@Override
	public RequestAndResponseContext find(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException
	{
		// find all of the given group that reference the specified ID
		ReferencingLogicTransmuter groupType = ReferencingLogicTransmuter.getBySwaggerGroup(input.getGroup());
		ReferencingLogicTransmuter idType = this.resolveID(context, input);
		
		if (groupType == ReferencingLogicTransmuter.Bundle)
		{
			if (idType == ReferencingLogicTransmuter.Bundle) ;// TODO: ?? what does one do here ??
			else if (idType == ReferencingLogicTransmuter.Collection) return RefLogicCollection.parents();
			else return this.grandparents();
		}
		else if (groupType == ReferencingLogicTransmuter.Collection)
		{
			if (idType == ReferencingLogicTransmuter.Bundle) ;// TODO: ?? what does one do here ??
			else if (idType == ReferencingLogicTransmuter.Collection) ;// TODO: ?? what does one do here ??
			else return this.parents();
		}
		else
		{
			if (idType == ReferencingLogicTransmuter.Bundle)
			{
				// TODO: ?? what does one do here ?? --what product classes reference a bundle?
			}
			else if (idType == ReferencingLogicTransmuter.Collection)
			{
				// TODO: ?? what does one do here ?? --what product classes reference a collection?
			}
			else
			{
			}
		}
		return null;
	}

	@Override
	public RequestAndResponseContext given(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException
	{
		// find all of the specified groups the given ID references
		ReferencingLogicTransmuter groupType = ReferencingLogicTransmuter.getBySwaggerGroup(input.getGroup());
		ReferencingLogicTransmuter idType = this.resolveID(context, input);
		
		if (idType == ReferencingLogicTransmuter.Bundle)
		{
			if (groupType == ReferencingLogicTransmuter.Bundle) ; // TODO: ?? what does one do here ??
			else if (groupType == ReferencingLogicTransmuter.Collection) return RefLogicBundle.children();
			else return RefLogicBundle.grandchildren();
		}
		else if (idType == ReferencingLogicTransmuter.Collection)
		{
			if (groupType == ReferencingLogicTransmuter.Bundle) ; // TODO: ?? what does one do here ??
			else if (groupType == ReferencingLogicTransmuter.Collection) ; // TODO: ?? what does one do here ??
			else return RefLogicCollection.children();
		}
		else
		{
			if (groupType == ReferencingLogicTransmuter.Bundle)
			{
				// TODO: ?? what does one do here ?? --what product classes references a bundle?
			}
			else if (groupType == ReferencingLogicTransmuter.Collection)
			{
				// TODO: ?? what does one do here ?? --what product classes references a collection?
			}
			else
			{
			}
		}
		return null;
	}
}
