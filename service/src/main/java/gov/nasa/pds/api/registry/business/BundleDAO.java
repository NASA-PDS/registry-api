package gov.nasa.pds.api.registry.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.NotImplementedException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import gov.nasa.pds.api.registry.search.ElasticSearchRegistryConnection;

/**
 * Bundle Data Access Object (DAO). 
 * Provides methods to get bundle information from Elasticsearch.
 * 
 * @author karpenko
 */
public class BundleDAO
{
    private ElasticSearchRegistryConnection esConnection;
    
    /**
     * Constructor
     * @param esConnection Elasticsearch connection
     */
    public BundleDAO(ElasticSearchRegistryConnection esConnection)
    {
        this.esConnection = esConnection;
    }
    
    
    public void getBundleCollections(String lidvid, int start, int limit, List<String> fields, 
            List<String> sort, boolean onlySummary) throws IOException, LidVidNotFoundException
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
    public List<String> getBundleCollectionLidVids(String bundleLidVid) 
            throws IOException, LidVidNotFoundException
    {
        // Get bundle by lidvid.
        GetRequest esRequest = new GetRequest(esConnection.getRegistryIndex(), bundleLidVid);
        
        // Fetch collection references only.
        String[] includes = { 
                "ref_lidvid_collection", "ref_lidvid_collection_secondary",
                "ref_lid_collection", "ref_lid_collection_secondary"
        };
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, null);
        esRequest.fetchSourceContext(fetchSourceContext);
        
        // Call Elasticsearch
        RestHighLevelClient client = esConnection.getRestHighLevelClient();
        GetResponse esResponse = client.get(esRequest, RequestOptions.DEFAULT);
        if(!esResponse.isExists()) throw new LidVidNotFoundException(bundleLidVid);

        // Get fields
        Map<String, Object> fieldMap = esResponse.getSourceAsMap();

        // LidVid references (e.g., OREX bundle)        
        List<String> primaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lidvid_collection");
        List<String> secondaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lidvid_collection_secondary");

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
        primaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lid_collection");
        secondaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lid_collection_secondary");

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
            List<String> latestLidVids = LidVidUtils.getLatestLidVidsByLids(esConnection, lids);
            if(latestLidVids != null && !latestLidVids.isEmpty())
            {
                // Combine LIDVID references and latest versions of "real" LID references
                lidVids.addAll(latestLidVids);
            }
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
    public List<String> getAllBundleCollectionLidVids(String bundleLidVid) 
            throws IOException, LidVidNotFoundException
    {
        // Get bundle by lidvid.
        GetRequest esRequest = new GetRequest(esConnection.getRegistryIndex(), bundleLidVid);
        
        // Fetch collection references only.
        String[] includes = { 
                "ref_lid_collection", "ref_lid_collection_secondary"
        };
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, null);
        esRequest.fetchSourceContext(fetchSourceContext);
        
        // Call Elasticsearch
        RestHighLevelClient client = esConnection.getRestHighLevelClient();
        GetResponse esResponse = client.get(esRequest, RequestOptions.DEFAULT);
        if(!esResponse.isExists()) throw new LidVidNotFoundException(bundleLidVid);

        // Get fields
        Map<String, Object> fieldMap = esResponse.getSourceAsMap();

        // Lid references (e.g., Kaguya bundle)
        List<String> primaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lid_collection");
        List<String> secondaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lid_collection_secondary");

        List<String> ids = new ArrayList<String>();
        if(primaryIds != null) ids.addAll(primaryIds); 
        if(secondaryIds != null) ids.addAll(secondaryIds);
        
        if(!ids.isEmpty())
        {
            // Get the latest versions of LIDs (Return LIDVIDs)
            ids = LidVidUtils.getAllLidVidsByLids(esConnection, ids);
            return ids;
        }
        
        return new ArrayList<String>(0);
    }

}
