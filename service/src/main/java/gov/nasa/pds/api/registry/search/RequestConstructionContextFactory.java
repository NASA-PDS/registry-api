package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.RequestConstructionContext;

public class RequestConstructionContextFactory
{
	/*
}
	public static RequestConstructionContext givenLidvid (String lidvid)
	{ return new SimpleRequestConstructionContext(lidvid); }
	
	public static RequestConstructionContext listOfLidvids (List<String> lidvids)
	{
		Map<String,List<String>> kvps = new HashMap<String,List<String>>();
		kvps.put("lidvid", lidvids);
		return new SimpleRequestConstructionContext(kvps);
	}
	*/
	public static RequestConstructionContext given (String key, String value)
	{
		List<String> values = new ArrayList<String>();
		Map<String,List<String>> kvps = new HashMap<String,List<String>>();
		kvps.put(key, values);
		values.add(value);
		return new SimpleRequestConstructionContext(kvps);
	}

	public static RequestConstructionContext given (String key, List<String> values)
	{
		Map<String,List<String>> kvps = new HashMap<String,List<String>>();
		kvps.put(key, values);
		return new SimpleRequestConstructionContext(kvps);
	}
}
