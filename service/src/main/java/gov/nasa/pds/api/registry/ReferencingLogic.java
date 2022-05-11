package gov.nasa.pds.api.registry;

import java.io.IOException;
import java.util.Map;

import gov.nasa.pds.api.registry.business.RequestAndResponseContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;

public interface ReferencingLogic
{
	/**
	 * Map the set of PDS constraints that define just PDS items that make up this Group. 
	 * @return
	 */
	public Map<String,String> constraints();
	
	/**
	 * Find all of the PDS items of the given Group that reference the specified ID.
	 * 
	 * @param input
	 * @return
	 */
	public RequestAndResponseContext find(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException;

	/**
	 * Find all of the PDS items of the given ID that reference the specified Group.
	 * 
	 * @param input
	 * @return
	 */
	public RequestAndResponseContext given(ControlContext context, UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, UnknownGroupNameException;
}
