package gov.nasa.pds.api.registry.search;

import java.io.IOException;
import java.util.List;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;

import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;

public class QuickSearch
{
	final private static Object get(ConnectionContext connection, String index, String lidvid, String name) throws IOException, LidVidNotFoundException
	{
		SearchRequest request = new SearchRequestFactory(RequestConstructionContextFactory.given(lidvid), connection)
				.build(RequestBuildContextFactory.given(name), index);
		SearchResponse result = connection.getRestHighLevelClient().search(request, RequestOptions.DEFAULT);
		
		if (result.getHits().getTotalHits().value == 0L) throw new LidVidNotFoundException(lidvid);

		return result.getHits().getAt(0).getSourceAsMap().get(name);
	}

	final public static String getValue (ConnectionContext connection, String lidvid, String name) throws IOException, LidVidNotFoundException
	{ return (String)QuickSearch.get(connection, connection.getRegistryIndex(), lidvid, name); }

	final public static String getValue (ConnectionContext connection, String index, String lidvid, String name) throws IOException, LidVidNotFoundException
	{ return (String)QuickSearch.get(connection, index, lidvid, name); }

	@SuppressWarnings("unchecked")
	final public static List<String> getValues (ConnectionContext connection, String lidvid, String name) throws IOException, LidVidNotFoundException
	{ return (List<String>)QuickSearch.get(connection, connection.getRegistryIndex(), lidvid, name); }

	@SuppressWarnings("unchecked")
	final public static List<String> getValues (ConnectionContext connection, String index, String lidvid, String name) throws IOException, LidVidNotFoundException
	{ return (List<String>)QuickSearch.get(connection, index, lidvid, name); }
}
