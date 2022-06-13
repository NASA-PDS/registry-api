package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

class SimpleRequestBuildContext implements RequestBuildContext
{
	final private List<String> fields;
	final private GroupConstraint preset;
	
	SimpleRequestBuildContext ()
	{
		this.fields = new ArrayList<String>();
		this.preset = GroupConstraintImpl.empty();
	}

	SimpleRequestBuildContext (List<String> fields)
	{
		this.fields = fields;
		this.preset = GroupConstraintImpl.empty();
	}

	SimpleRequestBuildContext (List<String> fields, GroupConstraint preset)
	{
		this.fields = fields;
		this.preset = preset;
	}

	@Override
	public List<String> getFields() { return fields; }

	@Override
	public GroupConstraint getPresetCriteria() { return preset; }
}
