package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;

public class LidVidDAO
{
    private ElasticSearchRegistryConnection esConnection;
    
    
    public LidVidDAO(ElasticSearchRegistryConnection esConnection)
    {
        this.esConnection = esConnection;        
    }
    

    /**
     * Get latest version of a LID.
     * @param lid a LID or LIDVID. If a LIDVID is passed it is returned as is. 
     * @return a LIDVID
     * @throws IOException
     * @throws LidVidNotFoundException
     */
    public String getLatestLidVidFromLid(String lid) throws IOException, LidVidNotFoundException
    {
        if(lid == null) throw new LidVidNotFoundException("");
        if(lid.contains("::")) return lid;
        
        List<String> lidvids = LidVidUtils.getLatestLids(esConnection, Arrays.asList(lid));
        if(lidvids == null || lidvids.isEmpty()) throw new LidVidNotFoundException(lid);
        
        return lidvids.get(0);
    }


    public List<String> getLatestLidVidsFromLids(Collection<String> lids) throws IOException
    {
        return LidVidUtils.getLatestLids(esConnection, lids);
    }

}
