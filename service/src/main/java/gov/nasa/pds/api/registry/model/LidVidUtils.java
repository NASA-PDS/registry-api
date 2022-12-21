package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.search.SearchHit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.LidVidMismatchException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.search.QuickSearch;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;


/**
 * Methods to get latest versions of LIDs
 * @author karpenko
 */
public class LidVidUtils
{
	private static final String LIDVID_SEPARATOR = "::";
	private static final Logger log = LoggerFactory.getLogger(LidVidUtils.class);
	
	public static String extractLidFromLidVid(String identifier)
    {
        if (identifier == null) return null;
        else return identifier.contains(LIDVID_SEPARATOR)?identifier.substring(0, identifier.indexOf(LIDVID_SEPARATOR)):identifier;
    }
    
    
    /**
     * Get latest versions of LIDs
     */
    public static List<String> getLatestLidVidsByLids(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            Collection<String> lids) throws IOException,LidVidNotFoundException
    {
    	List<String> lidvids = new ArrayList<String>();
    	
    	for (String lid : lids)
    	{
    		try { lidvids.add (LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext, lid)); }
    		catch (LidVidNotFoundException e)
    		{ log.error("Database is corrupted. Have reference to this lid but cannot find it: " + lid); }
    	}

    	return lidvids;
    }
    
    public static String getLatestLidVidByLid(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            String lid) throws IOException,LidVidNotFoundException
    {
    	lid = LidVidUtils.extractLidFromLidVid(lid);
    	SearchRequest searchRequest = new SearchRequestFactory(RequestConstructionContextFactory.given("lid", lid, true), ctlContext.getConnection())
    			.build(RequestBuildContextFactory.given(true, "lidvid", reqContext.getPresetCriteria()), ctlContext.getConnection().getRegistryIndex());
    	SearchResponse searchResponse = ctlContext.getConnection().getRestHighLevelClient().search(searchRequest, 
    			RequestOptions.DEFAULT);

    	if (searchResponse != null)
    	{
    		ArrayList<String> lidvids = new ArrayList<String>();
    		String lidvid;
    		for (SearchHit searchHit : searchResponse.getHits())
    		{
    			lidvid = (String)searchHit.getSourceAsMap().get("lidvid");;
    			lidvids.add(lidvid);                
    		}
    		Collections.sort(lidvids);

            if (lidvids.isEmpty()) throw new LidVidNotFoundException(lid);
    		else return lidvids.get(lidvids.size() - 1);
    	}
    	throw new LidVidNotFoundException(lid);
    }
    
    public static List<String> getAllLidVidsByLids(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            Collection<String> lids) throws IOException
    {
    	List<String> lidvids = new ArrayList<String>();

    	if (0 < lids.size())
    	{
    		ctlContext.getConnection().getRestHighLevelClient().search(
    				new SearchRequestFactory(RequestConstructionContextFactory.given("lid", new ArrayList<String>(lids), true), ctlContext.getConnection())
    				.build(reqContext, ctlContext.getConnection().getRegistryIndex()), RequestOptions.DEFAULT)
    		.getHits().forEach((hit) -> { lidvids.add(hit.getId()); });
    	}
    	return lidvids;
    }

    public static String resolve (
    		String identifier,
    		ProductVersionSelector scope,
    		ControlContext ctlContext,
    		RequestBuildContext reqContext) throws IOException, LidVidNotFoundException
    {
    	String result = identifier;
    	
    	if (0 < identifier.length())
    	{
    		String lid = LidVidUtils.extractLidFromLidVid(identifier);
    		/* YUCK! This should use polymorphism in ProductVersionSelector not a switch statement */
    		switch (scope)
    		{
    		case ALL:
    			result = lid;
    			break;
    		case LATEST:
    			result = LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext, lid);
    			break;
    		case TYPED:
    			result = lid.equals(identifier) ? LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext, lid) : identifier; 
    			break;
    		case ORIGINAL: throw new LidVidNotFoundException("ProductVersionSelector.ORIGINAL not supported");
    		default: throw new LidVidNotFoundException("Unknown and unhandles ProductVersionSelector value.");
    		}
    	}
    	return result;
    }

	public static void verify (ControlContext control, UserContext user)
			throws IOException, LidVidMismatchException, LidVidNotFoundException, UnknownGroupNameException
	{
		ReferencingLogicTransmuter expected_rlt = ReferencingLogicTransmuter.getBySwaggerGroup(user.getGroup());
			
		if (expected_rlt != ReferencingLogicTransmuter.Any)
		{
			String actual_group = QuickSearch.getValue(control.getConnection(), false, user.getLidVid(), "product_class");
			ReferencingLogicTransmuter actual_rlt = ReferencingLogicTransmuter.getByProductClass(actual_group);
			
			if (actual_rlt != expected_rlt)
				throw new LidVidMismatchException(user.getLidVid(), user.getGroup(), actual_group);
		}
	}
}
