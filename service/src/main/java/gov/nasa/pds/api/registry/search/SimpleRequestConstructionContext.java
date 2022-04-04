package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.RequestConstructionContext;

class SimpleRequestConstructionContext implements RequestConstructionContext
{
	final private boolean isTerm;
	final private Map<String,List<String>> kvps;
	final private String lidvid;
	
	SimpleRequestConstructionContext (Map<String,List<String>> kvps)
	{
		this.isTerm = false;
		this.kvps = kvps;
		this.lidvid = "";
	}
	
	SimpleRequestConstructionContext (String lidvid)
	{
		this.isTerm = false;
		this.kvps = new HashMap<String,List<String>>();
		this.lidvid = lidvid;
	}
	
	SimpleRequestConstructionContext (String lidvid, boolean isTerm)
	{
		this.isTerm = isTerm;
		this.kvps = new HashMap<String,List<String>>();
		this.lidvid = lidvid;
	}
	@Override
	public List<String> getKeywords() { return new ArrayList<String>(); }

	@Override
	public Map<String, List<String>> getKeyValuePairs() { return this.kvps; }

	@Override
	public String getLIDVID() { return this.lidvid; }

	@Override
	public String getQueryString() { return ""; }

	@Override
	public boolean isTerm() { return this.isTerm; }
}
