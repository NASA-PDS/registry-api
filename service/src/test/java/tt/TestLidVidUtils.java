package tt;

import java.util.List;

import gov.nasa.pds.api.registry.business.BundleDAO;
import gov.nasa.pds.api.registry.opensearch.OpenSearchRegistryConnection;
import gov.nasa.pds.api.registry.opensearch.OpenSearchRegistryConnectionImpl;


public class TestLidVidUtils
{

    public static void main(String[] args) throws Exception
    {
        //SearchSourceBuilder src = LidVidUtils.buildGetLatestLidVidsRequest(Arrays.asList("urn:nasa:pds:orex.spice"));
        //System.out.println(src);
        
        
        OpenSearchRegistryConnection con = new OpenSearchRegistryConnectionImpl();
        
        //List<String> ids = LidVidUtils.getLatestLids(con, Arrays.asList("urn:nasa:pds:orex.spice"));
        //System.out.println(ids);

        
        BundleDAO dao = new BundleDAO(con);
        //List<String> ids = dao.getBundleCollectionLidVids("urn:nasa:pds:orex.spice::3.0");
        List<String> ids = dao.getAllBundleCollectionLidVids("urn:nasa:pds:orex.spice::3.0");
        
        System.out.println();
        for(String id: ids)
        {
            System.out.println(id);
        }
        System.out.println();
        
        con.close();
    }

}
