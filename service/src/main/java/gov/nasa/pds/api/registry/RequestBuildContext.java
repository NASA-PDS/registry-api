package gov.nasa.pds.api.registry;

import java.util.List;

public interface RequestBuildContext {
	public boolean justLatest(); // return just the latest LIDVIDs. return false if request is made for a
									// specific version

	public List<String> getFields(); // must not return null but an empty list

	public GroupConstraint getPresetCriteria(); // must not return null but an empty list
}
