package gov.nasa.pds.api.registry;

import java.util.List;
import java.util.Map;

public interface RequestBuildContext
{
	public List<String> getFields(); // must not return null but an empty list
	public Map<String,String> getPresetCriteria(); // must not return null but an empty list
}
