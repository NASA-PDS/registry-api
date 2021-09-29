package tt;

import java.util.List;


import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnectionImpl;
import gov.nasa.pds.api.engineering.elasticsearch.business.BundleDAO;


public class TestLidVidUtils
{

    public static void main(String[] args) throws Exception
    {
        //SearchSourceBuilder src = LidVidUtils.buildGetLatestLidVidsRequest(Arrays.asList("urn:nasa:pds:orex.spice"));
        //System.out.println(src);
        
        
        ElasticSearchRegistryConnection con = new ElasticSearchRegistryConnectionImpl();
        
        //List<String> ids = LidVidUtils.getLatestLids(con, Arrays.asList("urn:nasa:pds:orex.spice"));
        //System.out.println(ids);

        
        BundleDAO dao = new BundleDAO(con);
        List<String> ids = dao.getBundleCollectionLidVids("urn:nasa:pds:orex.spice::3.0");
        
        System.out.println();
        System.out.println(ids);
        System.out.println();
        
        con.close();
    }

}
