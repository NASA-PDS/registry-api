package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.RequestBuildContext;

public class RequestBuildContextFactory
{
	public static RequestBuildContext empty() { return new SimpleRequestBuildContext(); }

	public static RequestBuildContext given (String field)
	{ return new SimpleRequestBuildContext(new ArrayList<String>(Arrays.asList(field))); }
	
	public static RequestBuildContext given (List<String> fields)
	{ return new SimpleRequestBuildContext(fields); }
	
	public static RequestBuildContext given (String field, Map<String,String> preset)
	{ return new SimpleRequestBuildContext(new ArrayList<String>(Arrays.asList(field)), preset); }
	
	public static RequestBuildContext given (List<String> fields, Map<String,String> preset)
	{ return new SimpleRequestBuildContext(fields, preset); }
}
