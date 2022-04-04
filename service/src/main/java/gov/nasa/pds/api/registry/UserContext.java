package gov.nasa.pds.api.registry;

import java.util.List;

import gov.nasa.pds.api.registry.business.ProductVersionSelector;

public interface UserContext
{
	public List<String> getFields();
	public String getIdentifier();
	public List<String> getKeywords();
	public Integer getLimit();
	public String getQuery();
	public ProductVersionSelector getSelector();
	public List<String> getSort();
	public Integer getStart();
	public Boolean getSummanryOnly();
}
