package gov.nasa.pds.api.registry.business;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import gov.nasa.pds.api.registry.opensearch.OpenSearchRegistryConnection;

public class LidVidDAO
{
    private OpenSearchRegistryConnection esConnection;
    
    
    public LidVidDAO(OpenSearchRegistryConnection esConnection)
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
    public String getLatestLidVidByLid(String lid) throws IOException, LidVidNotFoundException
    {
    	if(lid == null) throw new LidVidNotFoundException("");
    	String given = lid.contains("::")? lid : null;

        if(lid.contains("::")) lid = lid.substring(0, lid.indexOf("::"));
        
        List<String> lidvids = LidVidUtils.getLatestLidVidsByLids(esConnection, Arrays.asList(lid));
        if(lidvids == null || lidvids.isEmpty()) throw new LidVidNotFoundException(lid);
        if (given != null && lidvids.contains(given) && -1 < lidvids.indexOf(given) && lidvids.indexOf(given) == lidvids.lastIndexOf(given))
        {
        	if (lidvids.get(lidvids.indexOf(given)).equals(given)) return given;
        	else throw new LidVidNotFoundException("Partial lidvid is not valid: " + given);
        }
        else if (given != null) throw new LidVidNotFoundException("No such lidvid: " + given);

        return lidvids.get(0);
    }


    public List<String> getLatestLidVidsByLids(Collection<String> lids) throws IOException
    {
        return LidVidUtils.getLatestLidVidsByLids(esConnection, lids);
    }

}
