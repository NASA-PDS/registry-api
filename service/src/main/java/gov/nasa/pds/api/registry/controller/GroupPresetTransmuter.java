package gov.nasa.pds.api.registry.controller;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.api.registry.business.BundleDAO;
import gov.nasa.pds.api.registry.business.CollectionDAO;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;

class GroupPresetTransmuter
{
	final Map<String,Map<String,String>> group_table;
	
	public GroupPresetTransmuter()
	{
		HashMap<String,Map<String,String>> table = new HashMap<String,Map<String,String>>();
		table.put("any", new HashMap<String,String>());
		table.put("bundle", BundleDAO.searchConstraints());
		table.put("collection", CollectionDAO.searchConstraints());
		group_table = table;
	}
	final public Map<String,String> transform (String group) throws UnknownGroupNameException
	{
		if (this.group_table.containsKey(group)) return this.group_table.get(group);
		throw new UnknownGroupNameException(group, this.group_table.keySet());
	}
}
