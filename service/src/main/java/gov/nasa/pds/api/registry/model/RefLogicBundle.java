package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.LidvidsContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

/**
 * Bundle Data Access Object (DAO). 
 * Provides methods to get bundle information from opensearch.
 * 
 * @author karpenko
 */
@Immutable
class RefLogicBundle extends RefLogicAny implements ReferencingLogic
{
	private static final Logger log = LoggerFactory.getLogger(RefLogicBundle.class);

	static Pagination<String> children (ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
    		throws ApplicationTypeException, IOException, LidVidNotFoundException
    {
    	log.info("Find children of a bundle");
    	return selection == ProductVersionSelector.ALL ?
    			getAllBundleCollectionLidVids(uid, control) : 
    			getBundleCollectionLidVids(uid, control);
    }


	/**
     * Get all versions of bundle's collections by bundle LIDVID.
     */
    static private Pagination<String> getAllBundleCollectionLidVids(
    		LidvidsContext idContext,
    		ControlContext ctlContext)
            throws IOException, LidVidNotFoundException
    {
        // Fetch collection references only.
        List<String> fields = Arrays.asList("ref_lid_collection", "ref_lid_collection_secondary");

        // Get bundle by lidvid.
        SearchRequest request = new SearchRequestFactory(RequestConstructionContextFactory.given(idContext.getLidVid()), ctlContext.getConnection())
        		.build(RequestBuildContextFactory.given(false, fields, ReferencingLogicTransmuter.Bundle.impl().constraints()), ctlContext.getConnection().getRegistryIndex());
        
        // Call opensearch
        SearchHit hit;
        SearchHits hits = ctlContext.getConnection().getRestHighLevelClient().search(request, RequestOptions.DEFAULT).getHits();
        if (hits == null || hits.getTotalHits() == null || hits.getTotalHits().value != 1)
        	throw new LidVidNotFoundException(idContext.getLidVid());
        else hit = hits.getAt(0);

        // Get fields
        Map<String, Object> fieldMap = hit.getSourceAsMap();

        // Lid references (e.g., Kaguya bundle)
        List<String> ids = new ArrayList<String>();
        PaginationLidvidBuilder lidvids = new PaginationLidvidBuilder(idContext);

        ids.addAll(lidvids.convert(fieldMap.get("ref_lid_collection")));
        ids.addAll(lidvids.convert(fieldMap.get("ref_lid_collection_secondary")));
        ids = LidVidUtils.getAllLidVidsByLids(ctlContext, 
        		RequestBuildContextFactory.given(false, "lidvid", ReferencingLogicTransmuter.Collection.impl().constraints()),
        		ids);
        lidvids.addAll(ids);
        return lidvids;
    }

    /**
     * Get collections of a bundle by bundle LIDVID. 
     * If a bundle has LIDVID collection references, then those collections are returned. 
     * If a bundle has LID collection references, then the latest versions of collections are returned.
     * @return a list of collection LIDVIDs
     * @throws IOException IO exception
     * @throws LidVidNotFoundException LIDVID not found exception
     */
    static private Pagination<String> getBundleCollectionLidVids(
    		LidvidsContext idContext,
    		ControlContext ctlContext) 
            throws IOException, LidVidNotFoundException
    {
        // Fetch collection references only.
    	List<String> fields = Arrays.asList("ref_lidvid_collection","ref_lidvid_collection_secondary",
                                            "ref_lid_collection", "ref_lid_collection_secondary");
    	
        // Get bundle by lidvid.
        SearchRequest request = new SearchRequestFactory(RequestConstructionContextFactory.given(idContext.getLidVid()), ctlContext.getConnection())
        		.build(RequestBuildContextFactory.given(true, fields,
        				ReferencingLogicTransmuter.Bundle.impl().constraints()),
        				ctlContext.getConnection().getRegistryIndex());
        
        // Call opensearch
        SearchHit hit;
        SearchHits hits = ctlContext.getConnection().getRestHighLevelClient().search(request, RequestOptions.DEFAULT).getHits();
        if(hits == null || hits.getTotalHits() == null || hits.getTotalHits().value != 1)
        	throw new LidVidNotFoundException(idContext.getLidVid());
        else hit = hits.getAt(0);

        // Get fields
        // LidVid references (e.g., OREX bundle)
        List<String> ids = new ArrayList<String>();
        Map<String, Object> fieldMap = hit.getSourceAsMap();
        PaginationLidvidBuilder lidvids = new PaginationLidvidBuilder(idContext);
        
        ids.addAll(lidvids.convert(fieldMap.get("ref_lidvid_collection")));
        ids.addAll(lidvids.convert(fieldMap.get("ref_lidvid_collection_secondary")));
        
        // !!! NOTE !!! 
        // Harvest converts LIDVID references to LID references and stores them in
        // "ref_lid_collection" and "ref_lid_collection_secondary" fields.
        // To get "real" LID references, we have to exclude LIDVID references from these fields.
        Set<String> lidsToRemove = new TreeSet<String>();
        for(String id: ids)
        {
            int idx = id.indexOf("::");
            if(idx > 0)
            {
                String lid = id.substring(0, idx);
                lidsToRemove.add(lid);
            }
        }
        
        // Lid references (e.g., Kaguya bundle) plus LIDVID references converted by Harvest
        List<String> lids = new ArrayList<String>();
        lids.addAll(lidvids.convert(fieldMap.get("ref_lid_collection")));
        lids.addAll(lidvids.convert(fieldMap.get("ref_lid_collection_secondary")));
        
        // Get "real" LIDs
        if(!lidsToRemove.isEmpty())
        { lids.removeAll(lidsToRemove); }

        // Get the latest versions of LIDs
        lidvids.addAll(LidVidUtils.getLatestLidVidsByLids(ctlContext,
        		RequestBuildContextFactory.given(true, "lid",
        				ReferencingLogicTransmuter.Collection.impl().constraints()), lids));       
        return lidvids;
    }

    static Pagination<String> grandchildren (ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
    		throws ApplicationTypeException, IOException, LidVidNotFoundException
    {
    	log.info("Find grandchildren of a bundle");
    	PaginationLidvidBuilder ids = new PaginationLidvidBuilder(uid);
    	for (String cid : getBundleCollectionLidVids(new Unlimited(uid.getLidVid()), control).page())
    	{ ids.addAll(RefLogicCollection.children (control, selection, new Unlimited(cid)).page()); }
    	return ids;
    }

    @Override
    public GroupConstraint constraints()
    {
    	Map<String,List<String>> preset = new HashMap<String,List<String>>();
    	preset.put("product_class", Arrays.asList("Product_Bundle"));
    	return GroupConstraintImpl.buildAll(preset);
    }

    @Override
	public RequestAndResponseContext member(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException,
			UnknownGroupNameException
	{
		if (twoSteps)
			return RequestAndResponseContext.buildRequestAndResponseContext
					(context, input, RefLogicBundle.grandchildren(context, input.getSelector(), input));
		return RequestAndResponseContext.buildRequestAndResponseContext
				(context, input, RefLogicBundle.children(context, input.getSelector(), input));
	}

    
    @Override
	public RequestAndResponseContext memberOf(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException,
			UnknownGroupNameException
	{
		throw new MembershipException(input.getIdentifier(), "member-of", "bundle");
	}

}
