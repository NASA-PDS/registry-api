package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.LidvidsContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;

@Immutable
class RefLogicCollection extends RefLogicAny implements ReferencingLogic
{
    @Override
    public Map<String,String> constraints()
    {
    	Map<String,String> preset = new HashMap<String,String>();
    	preset.put("product_class", "Product_Collection");
    	return preset;
    }
	
    static Pagination<String> children (ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
    {
    	return selection == ProductVersionSelector.ALL ? RefLogicCollection.childrenAll(control, uid) : RefLogicCollection.childrenLatest(control, uid);
    }

    private static Pagination<String> childrenAll (ControlContext control, LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
	{
        PaginationLidvidBuilder productLidvids = new PaginationLidvidBuilder(uid);

        for (final Map<String,Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
        		new SearchRequestFactory(RequestConstructionContextFactory.given ("collection_lidvid", uid.getLidVid(), true), control.getConnection())
        				.build (RequestBuildContextFactory.given ("product_lid"), control.getConnection().getRegistryRefIndex())))
        {
        	productLidvids.addAll(LidVidUtils.getAllLidVidsByLids(control,
        			RequestBuildContextFactory.given("lidvid", ReferencingLogicTransmuter.Product.impl().constraints()),
        			productLidvids.convert(kvp.get("product_lid"))));
        }
        return productLidvids;
}

    private static Pagination<String> childrenLatest (ControlContext control, LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
    {
        PaginationLidvidBuilder productLidvids = new PaginationLidvidBuilder(uid);

        for (final Map<String,Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
        		new SearchRequestFactory(RequestConstructionContextFactory.given ("collection_lidvid", uid.getLidVid(), true), control.getConnection())
        				.build (RequestBuildContextFactory.given ("product_lidvid"), control.getConnection().getRegistryRefIndex())))
        { productLidvids.add(kvp.get("product_lidvid")); }
        return productLidvids;
    }
    
    static Pagination<String> parents (ControlContext control, ProductVersionSelector selection,  LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
    {
    	List<String> sortedLids;
    	Set<String> lids = new HashSet<String>();
        PaginationLidvidBuilder bundleLidvids = new PaginationLidvidBuilder(uid);
        for (final Map<String,Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
        		new SearchRequestFactory(RequestConstructionContextFactory.given("ref_lid_collection",
        				LidVidUtils.extractLidFromLidVid(uid.getLidVid()), true), control.getConnection())
                .build(RequestBuildContextFactory.given("lid", ReferencingLogicTransmuter.Bundle.impl().constraints()),
                		control.getConnection().getRegistryIndex())))
        {
        	lids.addAll(bundleLidvids.convert(kvp.get("lid")));
        }
        sortedLids = new ArrayList<String>(lids);
        Collections.sort(sortedLids);
        
        if (selection == ProductVersionSelector.ALL)
        	bundleLidvids.addAll (LidVidUtils.getAllLidVidsByLids(control, RequestBuildContextFactory.empty(), sortedLids));
        else 
        	bundleLidvids.addAll (LidVidUtils.getLatestLidVidsByLids(control, RequestBuildContextFactory.empty(), sortedLids));

        return bundleLidvids;
    }
}
