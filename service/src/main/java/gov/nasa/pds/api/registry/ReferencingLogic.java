package gov.nasa.pds.api.registry;

import java.io.IOException;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.RequestAndResponseContext;

@Immutable
public interface ReferencingLogic
{
	/**
	 * Map the set of PDS constraints that define just PDS items that make up this Group. 
	 */
	public GroupConstraint constraints();
	
	/**
	 * Find all of the PDS items of the given Group that reference the specified ID.
	 */
	public RequestAndResponseContext find(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException;

	/**
	 * Find all of the PDS items of the given ID that reference the specified Group.
	 */
	public RequestAndResponseContext given(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException;
}
