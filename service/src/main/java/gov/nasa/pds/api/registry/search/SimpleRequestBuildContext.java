package gov.nasa.pds.api.registry.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.RequestBuildContext;

class SimpleRequestBuildContext implements RequestBuildContext
{
	final private List<String> fields;
	final private Map<String,String> preset;
	
	SimpleRequestBuildContext (List<String> fields)
	{
		this.fields = fields;
		this.preset = new HashMap<String,String>();
	}

	SimpleRequestBuildContext (List<String> fields, Map<String,String> preset)
	{
		this.fields = fields;
		this.preset = preset;
	}

	@Override
	public List<String> getFields() { return fields; }

	@Override
	public Map<String, String> getPresetCriteria() { return preset; }
}
