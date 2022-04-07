package gov.nasa.pds.api.registry.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.NotImplementedException;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;

/**
 * Bundle Data Access Object (DAO). 
 * Provides methods to get bundle information from opensearch.
 * 
 * @author karpenko
 */
public class BundleDAO
{
    @SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(BundleDAO.class);

    static public Map<String,String> searchConstraints()
    {
    	Map<String,String> preset = new HashMap<String,String>();
    	preset.put("product_class", "Product_Bundle");
    	return preset;
    }

    static public void getBundleCollections() throws IOException, LidVidNotFoundException
    {
        // TODO: Move code from the bundle controller here.
        throw new NotImplementedException();
    }

    /**
     * Get collections of a bundle by bundle LIDVID. 
     * If a bundle has LIDVID collection references, then those collections are returned. 
     * If a bundle has LID collection references, then the latest versions of collections are returned.
     * @param bundleLidVid bundle LIDVID (Could not pass LID here)
     * @return a list of collection LIDVIDs
     * @throws IOException IO exception
     * @throws LidVidNotFoundException LIDVID not found exception
     */
    static public List<String> getBundleCollectionLidVids(
    		String bundleLidVid,
    		ControlContext ctlContext,
    		RequestBuildContext reqBuildContext) 
            throws IOException, LidVidNotFoundException
    {
        // Fetch collection references only.
    	List<String> fields = new ArrayList<String>(
    			Arrays.asList("ref_lidvid_collection","ref_lidvid_collection_secondary",
                               "ref_lid_collection", "ref_lid_collection_secondary"));
    	
        // Get bundle by lidvid.
        SearchRequest request = new SearchRequestFactory(RequestConstructionContextFactory.given(bundleLidVid))
        		.build(RequestBuildContextFactory.given(fields, BundleDAO.searchConstraints()), ctlContext.getRegistryContext().getRegistryIndex());
        
        // Call opensearch
        SearchHit hit;
        SearchHits hits = ctlContext.getConnection().getRestHighLevelClient().search(request, RequestOptions.DEFAULT)
        		.getHits();
        if(hits == null || hits.getTotalHits() == null || hits.getTotalHits().value != 1)
        	throw new LidVidNotFoundException(bundleLidVid);
        else hit = hits.getAt(0);

        // Get fields
        // LidVid references (e.g., OREX bundle)        
        Map<String, Object> fieldMap = hit.getSourceAsMap();
        List<String> primaryIds = ResponseUtils.getFieldValues(fieldMap, "ref_lidvid_collection");
        List<String> secondaryIds = ResponseUtils.getFieldValues(fieldMap, "ref_lidvid_collection_secondary");

        List<String> lidVids = new ArrayList<String>();
        if(primaryIds != null) lidVids.addAll(primaryIds); 
        if(secondaryIds != null) lidVids.addAll(secondaryIds);

        // !!! NOTE !!! 
        // Harvest converts LIDVID references to LID references and stores them in
        // "ref_lid_collection" and "ref_lid_collection_secondary" fields.
        // To get "real" LID references, we have to exclude LIDVID references from these fields.
        Set<String> lidsToRemove = new TreeSet<String>();
        for(String lidVid: lidVids)
        {
            int idx = lidVid.indexOf("::");
            if(idx > 0)
            {
                String lid = lidVid.substring(0, idx);
                lidsToRemove.add(lid);
            }
        }
        
        // Lid references (e.g., Kaguya bundle) plus LIDVID references converted by Harvest
        primaryIds = ResponseUtils.getFieldValues(fieldMap, "ref_lid_collection");
        secondaryIds = ResponseUtils.getFieldValues(fieldMap, "ref_lid_collection_secondary");

        List<String> lids = new ArrayList<String>();
        if(primaryIds != null) lids.addAll(primaryIds); 
        if(secondaryIds != null) lids.addAll(secondaryIds);
        
        // Get "real" LIDs
        if(!lidsToRemove.isEmpty())
        {
            lids.removeAll(lidsToRemove);
        }

        // Get the latest versions of LIDs
        if(!lids.isEmpty())
        {
            List<String> latestLidVids = LidVidUtils.getLatestLidVidsByLids(ctlContext,
            		RequestBuildContextFactory.given(reqBuildContext.getFields(), CollectionDAO.searchConstraints()), lids);
            lidVids.addAll(latestLidVids);
        }
       
        return lidVids;
    }

    
    /**
     * Get all versions of bundle's collections by bundle LIDVID.
     * @param bundleLidVid bundle LIDVID (Could not pass LID here)
     * @return a list of collection LIDVIDs
     * @throws IOException IO exception 
     * @throws LidVidNotFoundException LIDVID not found exception
     */
    static public List<String> getAllBundleCollectionLidVids(
    		String bundleLidVid,
    		ControlContext ctlContext,
    		RequestBuildContext reqBuildContext)
            throws IOException, LidVidNotFoundException
    {
        // Fetch collection references only.
        List<String> fields = new ArrayList<String>(
                Arrays.asList("ref_lid_collection", "ref_lid_collection_secondary"));

        // Get bundle by lidvid.
        SearchRequest request = new SearchRequestFactory(RequestConstructionContextFactory.given(bundleLidVid))
        		.build(RequestBuildContextFactory.given(fields, BundleDAO.searchConstraints()), ctlContext.getRegistryContext().getRegistryIndex());
        
        // Call opensearch
        SearchHit hit;
        SearchHits hits = ctlContext.getConnection().getRestHighLevelClient().search(request, RequestOptions.DEFAULT).getHits();
        if (hits == null || hits.getTotalHits() == null || hits.getTotalHits().value != 1)
        	throw new LidVidNotFoundException(bundleLidVid);
        else hit = hits.getAt(0);

        // Get fields
        Map<String, Object> fieldMap = hit.getSourceAsMap();

        // Lid references (e.g., Kaguya bundle)
        List<String> primaryIds = ResponseUtils.getFieldValues(fieldMap, "ref_lid_collection");
        List<String> secondaryIds = ResponseUtils.getFieldValues(fieldMap, "ref_lid_collection_secondary");

        List<String> ids = new ArrayList<String>();
        if(primaryIds != null) ids.addAll(primaryIds); 
        if(secondaryIds != null) ids.addAll(secondaryIds);
        
        if(!ids.isEmpty())
        {
            // Get the latest versions of LIDs (Return LIDVIDs)
            ids = LidVidUtils.getAllLidVidsByLids(ctlContext, reqBuildContext, ids);
            return ids;
        }
        
        return new ArrayList<String>(0);
    }

}
