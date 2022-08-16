package gov.nasa.pds.api.registry.search;

import java.io.IOException;
import java.util.List;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.client.RequestOptions;

import gov.nasa.pds.api.registry.ConnectionContext;

public class QuickSearch
{
	final private static Object get(ConnectionContext connection, String index, String lidvid, String name) throws IOException
	{
		SearchRequest request = new SearchRequestFactory(RequestConstructionContextFactory.given(lidvid), connection)
				.build(RequestBuildContextFactory.given(name), index);
		return connection.getRestHighLevelClient().search(request, RequestOptions.DEFAULT)
				.getHits()
				.getAt(0)
				.getSourceAsMap()
				.get(name);
	}

	final public static String getValue (ConnectionContext connection, String lidvid, String name) throws IOException
	{ return (String)QuickSearch.get(connection, connection.getRegistryIndex(), lidvid, name); }

	final public static String getValue (ConnectionContext connection, String index, String lidvid, String name) throws IOException
	{ return (String)QuickSearch.get(connection, index, lidvid, name); }

	@SuppressWarnings("unchecked")
	final public static List<String> getValues (ConnectionContext connection, String lidvid, String name) throws IOException
	{ return (List<String>)QuickSearch.get(connection, connection.getRegistryIndex(), lidvid, name); }

	@SuppressWarnings("unchecked")
	final public static List<String> getValues (ConnectionContext connection, String index, String lidvid, String name) throws IOException
	{ return (List<String>)QuickSearch.get(connection, index, lidvid, name); }
}
