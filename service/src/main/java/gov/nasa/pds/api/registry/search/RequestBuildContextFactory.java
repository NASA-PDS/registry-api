package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.api.registry.RequestBuildContext;

public class RequestBuildContextFactory
{
	public static RequestBuildContext given (String field)
	{
		List<String> fields = new ArrayList<String>();
		fields.add(field);
		return new SimpleRequestBuildContext(fields);
	}
	
	public static RequestBuildContext given (List<String> fields)
	{ return new SimpleRequestBuildContext(fields); }
}
