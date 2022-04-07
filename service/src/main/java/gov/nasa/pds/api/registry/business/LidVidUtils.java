package gov.nasa.pds.api.registry.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.search.SearchHit;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestBuilder;


/**
 * Methods to get latest versions of LIDs
 * @author karpenko
 */
public class LidVidUtils
{
	private static final String LIDVID_SEPARATOR = "::";
	private static final Logger log = LoggerFactory.getLogger(LidVidUtils.class);
	/**
     * Extract lid from lidvid.
     * @param identifier LIDVID or LID.
     * @return LID
     */
    public static String extractLidFromLidVid(String identifier)
    {
        if (identifier == null) return null;
        else return identifier.contains(LIDVID_SEPARATOR)?identifier.substring(0, identifier.indexOf(LIDVID_SEPARATOR)):identifier;
    }
    
    
    /**
     * Get latest versions of LIDs
     * @param esConnection opensearch connection
     * @param lids list of LIDs
     * @return list of LIDVIDs
     * @throws IOException
     */
    public static List<String> getLatestLidVidsByLids(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            Collection<String> lids) throws IOException,LidVidNotFoundException
    {
    	List<String> lidvids = new ArrayList<String>(lids.size());
    	
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
    	SearchRequest searchRequest = new SearchRequestBuilder(RequestConstructionContextFactory.given("lid", lid))
    			.build(RequestBuildContextFactory.given("lidvid", reqContext.getPresetCriteria()), ctlContext.getRegistryContext().getRegistryIndex());
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

            if (lidvids.size() == 0) throw new LidVidNotFoundException(lid);
    		else return lidvids.get(lidvids.size() - 1);
    	}
    	throw new LidVidNotFoundException(lid);
    }
    
    /**
     * Get all LIDVIDs by LIDs
     * @param esConnection opensearch connection
     * @param lids list of LIDs
     * @return a list of LIDVIDs
     * @throws IOException an exception
     */
    public static List<String> getAllLidVidsByLids(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            Collection<String> lids) throws IOException
    {
        // Create request
        SearchSourceBuilder src = xxbuildGetAllLidVidsRequest(reqContext, lids);
        src.timeout(new TimeValue(ctlContext.getRegistryContext().getTimeOutSeconds(), TimeUnit.SECONDS));
        
        SearchRequest esRequest = new SearchRequest(ctlContext.getRegistryContext().getRegistryIndex()).source(src);
        
        // Call opensearch
        SearchResponse esResp = ctlContext.getConnection().getRestHighLevelClient().search(esRequest, RequestOptions.DEFAULT);
        
        // Parse response
        List<String> lidvids = new ArrayList<>();
        esResp.getHits().forEach((hit) -> { lidvids.add(hit.getId()); });
        return lidvids;
    }

    
    /**
     * Build aggregation query to select latest versions of lids
     * @param lids list of LIDs
     * @return opensearch query
     */
    public static SearchSourceBuilder xxbuildGetLatestLidVidsRequest(Collection<String> lids, RequestBuildContext context)
    {
        if(lids == null || lids.isEmpty()) return null;
        
        SearchSourceBuilder src = new SearchSourceBuilder();
        
        // Query
        // FIXME: no, this needs to use the usual route and not do it on its own
        src.query(new SearchRequestBuilder(RequestConstructionContextFactory.given("lid", new ArrayList<String>(lids), true))
        		.getQueryBuilder(context)).fetchSource(false).size(0);
        // Aggregations
        src.aggregation(AggregationBuilders.terms("lids").field("lid").size(lids.size())
            .subAggregation(AggregationBuilders.topHits("latest").sort(new FieldSortBuilder("vid").order(SortOrder.DESC))
                    .fetchSource(false).size(1))
        );

        return src;
    }

    
    /**
     * Build terms query to select all document ids by a list of LIDs.
     * @param lids a list of LIDS
     * @return opensearch query
     */
    public static SearchSourceBuilder xxbuildGetAllLidVidsRequest(
    		RequestBuildContext reqContext, Collection<String> lids)
    {
        if(lids == null || lids.isEmpty()) return null;

        // FIXME: no, this needs to use the usual route and not do it on its own
        SearchSourceBuilder src = new SearchSourceBuilder();
        src.query(new SearchRequestBuilder(RequestConstructionContextFactory.given("lid", new ArrayList<String>(lids), true))
        		.getQueryBuilder(reqContext)).fetchSource(false).size(5000);
        return src;
    }
    
    public static String resolveLIDVID (
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
    		case ORIGINAL: throw new LidVidNotFoundException("ProductVersionSelector.ORIGINAL not supported");
    		default: throw new LidVidNotFoundException("Unknown and unhandles ProductVersionSelector value.");
    		}
    	}
    	return result;
    }
}
