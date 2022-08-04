package gov.nasa.pds.api.registry.serializer;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import gov.nasa.pds.api.registry.model.SearchUtil;
import gov.nasa.pds.model.Summary;

class Utilities
{
	static void fix (Summary summary)
	{
		List<String> fixed = new ArrayList<String>(summary.getProperties().size());
		for (String prop : summary.getProperties())
		{
			try { fixed.add(SearchUtil.openPropertyToJsonProperty(prop)); }
			catch (UnsupportedSearchProperty e) { fixed.add(prop); }
		}
		summary.setProperties(fixed);
	}
}
