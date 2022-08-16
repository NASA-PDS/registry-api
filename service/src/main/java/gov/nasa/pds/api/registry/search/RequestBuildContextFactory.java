package gov.nasa.pds.api.registry.search;

import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.RequestBuildContext;

public class RequestBuildContextFactory
{
	public static RequestBuildContext empty() { return new SimpleRequestBuildContext(); }

	public static RequestBuildContext given (String field)
	{ return new SimpleRequestBuildContext(Arrays.asList(field)); }
	
	public static RequestBuildContext given (List<String> fields)
	{ return new SimpleRequestBuildContext(fields); }
	
	public static RequestBuildContext given (String field, GroupConstraint preset)
	{ return new SimpleRequestBuildContext(Arrays.asList(field), preset); }
	
	public static RequestBuildContext given (List<String> fields, GroupConstraint preset)
	{ return new SimpleRequestBuildContext(fields, preset); }
}
