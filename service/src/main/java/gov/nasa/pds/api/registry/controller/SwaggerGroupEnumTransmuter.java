package gov.nasa.pds.api.registry.controller;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.business.RefLogicBundle;
import gov.nasa.pds.api.registry.business.RefLogicCollection;
import gov.nasa.pds.api.registry.business.RefLogicProduct;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;

class SwaggerGroupEnumTransmuter
{
	final Map<String,ReferencingLogic> group_table;
	
	public SwaggerGroupEnumTransmuter()
	{
		HashMap<String,ReferencingLogic> table = new HashMap<String,ReferencingLogic>();
		table.put("any", new RefLogicProduct());
		table.put("bundle", new RefLogicBundle());
		table.put("collection", new RefLogicCollection());
		group_table = table;
	}

	final public ReferencingLogic transform (String group) throws UnknownGroupNameException
	{
		if (this.group_table.containsKey(group)) return this.group_table.get(group);
		throw new UnknownGroupNameException(group, this.group_table.keySet());
	}
}
