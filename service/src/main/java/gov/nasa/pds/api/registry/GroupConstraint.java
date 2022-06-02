package gov.nasa.pds.api.registry;

import java.util.Map;

import com.google.errorprone.annotations.Immutable;

@Immutable
public interface GroupConstraint
{
	/**
	 * A set of PDS keywords/values that ALL must be true to define just PDS items that make up this Group. 
	 */
	public Map<String,String> all();
	/**
	 * A set of PDS keywords/values that ANY must be true to define just PDS items that make up this Group. 
	 */
	public Map<String,String> any();
	/**
	 * A set of PDS keywords/values that NONE must be true to define just PDS items that make up this Group. 
	 */
	public Map<String,String> not();
}
