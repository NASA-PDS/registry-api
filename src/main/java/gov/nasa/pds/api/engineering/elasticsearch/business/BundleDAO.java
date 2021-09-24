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


    public List<String> getBundleCollectionLidVids(String bundleLidVid) throws IOException, LidVidNotFoundException
    {
        // Get bundle by lidvid.
        GetRequest esRequest = new GetRequest(esConnection.getRegistryIndex(), bundleLidVid);
        
        // Fetch collection references only.
        String[] includes = new String[] { "ref_lidvid_collection", "ref_lid_collection" };
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, null);
        esRequest.fetchSourceContext(fetchSourceContext);
        
        // Call Elasticsearch
        RestHighLevelClient client = esConnection.getRestHighLevelClient();
        GetResponse esResponse = client.get(esRequest, RequestOptions.DEFAULT);
        if(!esResponse.isExists()) throw new LidVidNotFoundException(bundleLidVid);

        Map<String, Object> fieldMap = esResponse.getSourceAsMap();
        
        // LidVid references (e.g., OREX bundle)        
        List<String> ids = ESResponseUtils.getFieldValues(fieldMap, "ref_lidvid_collection");
        if(ids != null) return ids;

        // Lid references (e.g., Kaguya bundle)
        ids = ESResponseUtils.getFieldValues(fieldMap, "ref_lid_collection");
        if(ids != null)
        {
            // Get the latest versions of LIDs (Return LIDVIDs)
            ids = LidVidUtils.getLatestLids(esConnection, ids);
            return ids;
        }
        
        return new ArrayList<String>(0);
    }

}
