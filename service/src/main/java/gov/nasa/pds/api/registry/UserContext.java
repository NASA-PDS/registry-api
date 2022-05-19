package gov.nasa.pds.api.registry;

import java.util.List;

import gov.nasa.pds.api.registry.model.ProductVersionSelector;

public interface UserContext extends LidvidsContext
{
	public String getAccept();
	public List<String> getFields();
	public String getGroup();
	public String getIdentifier();
	public List<String> getKeywords();
	public String getQuery();
	public ProductVersionSelector getSelector();
	public List<String> getSort();
	public String getVersion();
}
