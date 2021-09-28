package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.model.Products;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


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
    
    
    public Products getBundleCollections(String lidvid, int start, int limit, List<String> fields, 
            List<String> sort, boolean onlySummary) throws IOException, LidVidNotFoundException
    {
        // TODO: Move code from the bundle controller here.
        throw new NotImplementedException();
    }


    /**
     * Get collections of a bundle by bundle LIDVID. 
     * <p>If latest flag is true, then only latest versions of collections are returned 
     * even if LIDVID reference in a bundle points to previous version of a collection.
     * <p> If latest flag is false, then if a bundle has LIDVID collection references, 
     * then those collections are returned. If a bundle has LID collection references,
     * then the latest versions of collections are returned.
     * @param bundleLidVid bundle LIDVID (Could not pass LID here)
     * @param latest if true, return latest collection versions, even if LIDVID collection
     * references point to previous versions.
     * @return a list of collection LIDVIDs
     * @throws IOException IO exception
     * @throws LidVidNotFoundException LIDVID not found exception
     */
    public List<String> getBundleCollectionLidVids(String bundleLidVid, boolean latest) 
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

        if(!latest)
        {
            // LidVid references (e.g., OREX bundle)        
            List<String> primaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lidvid_collection");
            List<String> secondaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lidvid_collection_secondary");

            List<String> ids = new ArrayList<String>();
            if(primaryIds != null) ids.addAll(primaryIds); 
            if(secondaryIds != null) ids.addAll(secondaryIds);

            if(!ids.isEmpty()) return ids;
        }

        // Lid references (e.g., Kaguya bundle)
        List<String> primaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lid_collection");
        List<String> secondaryIds = ESResponseUtils.getFieldValues(fieldMap, "ref_lid_collection_secondary");

        List<String> ids = new ArrayList<String>();
        if(primaryIds != null) ids.addAll(primaryIds); 
        if(secondaryIds != null) ids.addAll(secondaryIds);
        
        if(!ids.isEmpty())
        {
            // Get the latest versions of LIDs (Return LIDVIDs)
            ids = LidVidUtils.getLatestLidVidsByLids(esConnection, ids);
            return ids;
        }
        
        return new ArrayList<String>(0);
    }

    
    public List<String> getAllBundleCollectionLidVids(String bundleLidVid, boolean latest) 
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
            ids = LidVidUtils.getLatestLidVidsByLids(esConnection, ids);
            return ids;
        }
        
        return new ArrayList<String>(0);
    }

}
