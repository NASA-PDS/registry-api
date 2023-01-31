package gov.nasa.pds.api.registry;

import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;

import java.util.List;
import java.util.Map;

public interface RequestConstructionContext
{
	public List<String> getKeywords(); // must not return null but an empty list
	public Map<String, List<String>> getKeyValuePairs(); // must not return null but an empty map
	public PdsProductIdentifier getProductIdentifier();
	public String getProductIdentifierString(); // must not return null but an empty string
	public String getQueryString(); // must not return null but an empty string
	public boolean isTerm(); // if true, then use QueryBuilders.termQuery otherwise use QueryBuilders.matchQuery
}
