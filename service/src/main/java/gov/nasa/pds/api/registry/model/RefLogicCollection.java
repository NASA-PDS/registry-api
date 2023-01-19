package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
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
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

@Immutable
class RefLogicCollection extends RefLogicAny implements ReferencingLogic
{
	private static final Logger log = LoggerFactory.getLogger(RefLogicCollection.class);

    @Override
    public GroupConstraint constraints()
    {
    	Map<String,List<String>> preset = new HashMap<String,List<String>>();
    	preset.put("product_class", Arrays.asList("Product_Collection"));
    	return GroupConstraintImpl.buildAll(preset);
    }

    static Pagination<String> children (ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
    {
    	log.info("Find children of a collection -- both all and latest");
    	return selection == ProductVersionSelector.ALL ? RefLogicCollection.childrenAll(control, uid) : RefLogicCollection.childrenLatest(control, uid);
    }

    private static Pagination<String> childrenAll (ControlContext control, LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
	{
        PaginationLidvidBuilder productLidvids = new PaginationLidvidBuilder(uid);

        for (final Map<String,Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
        		new SearchRequestFactory(RequestConstructionContextFactory.given ("collection_lidvid", uid.getLidVid(), true), control.getConnection())
        				.build (RequestBuildContextFactory.given (false, "product_lid"), control.getConnection().getRegistryRefIndex())))
        {
        	productLidvids.addAll(LidVidUtils.getAllLidVidsByLids(control,
        			RequestBuildContextFactory.given(false, "lidvid", ReferencingLogicTransmuter.Product.impl().constraints()),
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
        				.build (RequestBuildContextFactory.given (true, "product_lidvid"), control.getConnection().getRegistryRefIndex())))
        { productLidvids.add(kvp.get("product_lidvid")); }
        return productLidvids;
    }

    static Pagination<String> parents (ControlContext control, ProductVersionSelector selection,  LidvidsContext uid)
    		throws IOException, LidVidNotFoundException
    {
    	List<String> keys = Arrays.asList("ref_lid_collection", "ref_lid_collection_secondary",
    		                              "ref_lidvid_collection", "ref_lidvid_collection_secondary");
    	List<String> sortedLids;
    	Set<String> lids = new HashSet<String>();
    	String lid = LidVidUtils.parseLid(uid.getLidVid());
        PaginationLidvidBuilder bundleLidvids = new PaginationLidvidBuilder(uid);

        log.info("Find parents of a colletion -- both all and latest");
        log.info("Find parents of collenction: " + uid.getLidVid() + "  --- " + LidVidUtils.parseLid(uid.getLidVid()));
        for (String key : keys)
        {
        	for (final Map<String,Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
        			new SearchRequestFactory(RequestConstructionContextFactory.given(key, lid, true), control.getConnection())
        			.build(RequestBuildContextFactory.given(true, "lid", ReferencingLogicTransmuter.Bundle.impl().constraints()),
        					control.getConnection().getRegistryIndex())))
        	{
        		lids.addAll(bundleLidvids.convert(kvp.get("lid")));
        	}
        }
        sortedLids = new ArrayList<String>(lids);
        Collections.sort(sortedLids);

        if (selection == ProductVersionSelector.ALL)
        	bundleLidvids.addAll (LidVidUtils.getAllLidVidsByLids(control, RequestBuildContextFactory.empty(), sortedLids));
        else
        	bundleLidvids.addAll (LidVidUtils.getLatestLidVidsByLids(control, RequestBuildContextFactory.empty(), sortedLids));

        return bundleLidvids;
    }

	@Override
	public RequestAndResponseContext member(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException,
			UnknownGroupNameException
	{
		if (twoSteps) throw new MembershipException(input.getIdentifier(), "members/members", "collections");
		return RequestAndResponseContext.buildRequestAndResponseContext
				(context, input, RefLogicCollection.children(context, input.getSelector(), input));
	}

	@Override
	public RequestAndResponseContext memberOf(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException,
			UnknownGroupNameException
	{
		if (twoSteps) throw new MembershipException(input.getIdentifier(), "member-of/member-of", "collections");
		return RequestAndResponseContext.buildRequestAndResponseContext
				(context, input, RefLogicCollection.parents(context, input.getSelector(), input));
	}
}
