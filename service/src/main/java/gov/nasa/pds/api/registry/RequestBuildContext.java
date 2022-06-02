package gov.nasa.pds.api.registry;

import java.util.List;

public interface RequestBuildContext
{
	public List<String> getFields(); // must not return null but an empty list
	public GroupConstraint getPresetCriteria(); // must not return null but an empty list
}
