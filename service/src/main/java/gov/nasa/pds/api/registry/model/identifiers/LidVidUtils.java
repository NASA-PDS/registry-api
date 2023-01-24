package gov.nasa.pds.api.registry.model.identifiers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.model.ProductVersionSelector;
import gov.nasa.pds.api.registry.model.ReferencingLogicTransmuter;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.search.SearchHit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.LidVidMismatchException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.search.QuickSearch;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;


/**
 * Methods to get latest versions of LIDs
 * @author karpenko
 */
public class LidVidUtils
{
	private static final Logger log = LoggerFactory.getLogger(LidVidUtils.class);

    /**
     * Get latest versions of LIDs
     */
    public static List<PdsLidVid> getLatestLidVidsForProductIdentifiers(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            Collection<PdsProductIdentifier> productIdentifiers) throws IOException,LidVidNotFoundException
    {
    	List<PdsLidVid> lidVids = new ArrayList<>();

    	for (PdsProductIdentifier id : productIdentifiers) {
			try {
				PdsLidVid latestLidVid = LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext, id.getLid().toString());
				lidVids.add(latestLidVid);
			} catch (LidVidNotFoundException e) {
				log.error("Database is corrupted. Have reference to LID but cannot find it: " + id.getLid().toString());
			}
		}

    	return lidVids;
    }

    public static PdsLidVid getLatestLidVidByLid(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            String productIdentifier) throws IOException,LidVidNotFoundException
    {
		PdsLid lid = PdsProductIdentifier.fromString(productIdentifier).getLid();

    	SearchRequest searchRequest = new SearchRequestFactory(RequestConstructionContextFactory.given("lid", lid.toString(), true), ctlContext.getConnection())
    			.build(RequestBuildContextFactory.given(true, "lidvid", reqContext.getPresetCriteria()), ctlContext.getConnection().getRegistryIndex());
    	SearchResponse searchResponse = ctlContext.getConnection().getRestHighLevelClient().search(searchRequest,
    			RequestOptions.DEFAULT);

    	if (searchResponse != null)
    	{
    		List<PdsLidVid> lidVids = new ArrayList<PdsLidVid>();
    		for (SearchHit searchHit : searchResponse.getHits())
    		{
    			String lidvidStr = (String)searchHit.getSourceAsMap().get("lidvid");;
				lidVids.add(PdsLidVid.fromString(lidvidStr));
    		}

    		Collections.sort(lidVids);

            if (lidVids.isEmpty()) {
				throw new LidVidNotFoundException(lid.toString());
			}

			return lidVids.get(lidVids.size() - 1);
    	}
    	throw new LidVidNotFoundException(lid.toString());
    }

    public static List<String> getAllLidVidsByLids(
    		ControlContext ctlContext,
    		RequestBuildContext reqContext,
            Collection<String> lids) throws IOException
    {
    	List<String> lidvids = new ArrayList<String>();

    	if (0 < lids.size())
    	{
    		ctlContext.getConnection().getRestHighLevelClient().search(
    				new SearchRequestFactory(RequestConstructionContextFactory.given("lid", new ArrayList<String>(lids), true), ctlContext.getConnection())
    				.build(reqContext, ctlContext.getConnection().getRegistryIndex()), RequestOptions.DEFAULT)
    		.getHits().forEach((hit) -> { lidvids.add(hit.getId()); });
    	}
    	return lidvids;
    }

    public static PdsProductIdentifier resolve (
    		String _identifier,
    		ProductVersionSelector scope,
    		ControlContext ctlContext,
    		RequestBuildContext reqContext) throws IOException, LidVidNotFoundException
    {
		PdsProductIdentifier productIdentifier = PdsProductIdentifier.fromString(_identifier);
    	PdsProductIdentifier result = null;

    	if (productIdentifier != null)
    	{
    		/* YUCK! This should use polymorphism in ProductVersionSelector not a switch statement */
    		switch (scope)
    		{
    		case ALL:
    			result = productIdentifier.getLid();
    			break;
    		case LATEST:
    			result = LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext, productIdentifier.getLid().toString());
    			break;
    		case SPECIFIC:
				result = productIdentifier instanceof PdsLidVid ? productIdentifier : LidVidUtils.getLatestLidVidByLid(ctlContext, reqContext, productIdentifier.getLid().toString());
    			break;
    		case ORIGINAL: throw new LidVidNotFoundException("ProductVersionSelector.ORIGINAL not supported");
    		default: throw new LidVidNotFoundException("Unknown and unhandles ProductVersionSelector value.");
    		}
    	}

		return productIdentifier;
    }

	public static void verify (ControlContext control, UserContext user)
			throws IOException, LidVidMismatchException, LidVidNotFoundException, UnknownGroupNameException
	{
		ReferencingLogicTransmuter expected_rlt = ReferencingLogicTransmuter.getBySwaggerGroup(user.getGroup());

		if (expected_rlt != ReferencingLogicTransmuter.Any)
		{
			String actual_group = QuickSearch.getValue(control.getConnection(), false, user.getLidVid(), "product_class");
			ReferencingLogicTransmuter actual_rlt = ReferencingLogicTransmuter.getByProductClass(actual_group);

			if (actual_rlt != expected_rlt)
				throw new LidVidMismatchException(user.getLidVid(), user.getGroup(), actual_group);
		}
	}
}
