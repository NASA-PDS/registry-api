package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.LidvidsContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;

class RefLogicProduct extends RefLogicAny implements ReferencingLogic
{
	@Override
	public Map<String, String> constraints() 
	{
		return new HashMap<String,String>();
	}

    static Pagination<String> grandparents (ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
    {
    	List<String> parents = RefLogicProduct.parents(control, ProductVersionSelector.LATEST, new Unlimited(uid.getLidVid()))
    			.page();
    	PaginationLidvidBuilder grandparents =  new PaginationLidvidBuilder(uid);
    	for (String parent : parents)
    	{
    		grandparents.addAll(RefLogicCollection.parents(control, selection, new Unlimited(parent)).page());
    	}
    	return grandparents;
    }

    static Pagination<String> parents (ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
    {
    	List<String> sorted_lids;
        PaginationLidvidBuilder parents = new PaginationLidvidBuilder(uid);
        Set<String> lids = new HashSet<String>();

        for (final Map<String,Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
        		new SearchRequestFactory(RequestConstructionContextFactory.given("product_lidvid", uid.getLidVid(), true), control.getConnection())
        		.build (RequestBuildContextFactory.given("collection_lid"), control.getConnection().getRegistryRefIndex())))
		{ lids.addAll(parents.convert(kvp.get("collection_lid"))); }
        sorted_lids = new ArrayList<String>();
        Collections.sort(sorted_lids);

        if (selection == ProductVersionSelector.ALL)
        	parents.addAll (LidVidUtils.getAllLidVidsByLids(control, RequestBuildContextFactory.empty(), sorted_lids));
        else
        	parents.addAll(LidVidUtils.getLatestLidVidsByLids(control, RequestBuildContextFactory.empty(), sorted_lids));

        return parents;
    }

}
