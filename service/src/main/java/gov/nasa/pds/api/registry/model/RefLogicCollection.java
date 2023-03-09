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
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.LidvidsContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.RequestConstructionContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import org.opensearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

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
class RefLogicCollection extends RefLogicAny implements ReferencingLogic {
	private static final Logger log = LoggerFactory.getLogger(RefLogicCollection.class);

	@Override
	public GroupConstraint constraints() {
		Map<String, List<String>> preset = new HashMap<String, List<String>>();
		preset.put("product_class", Arrays.asList("Product_Collection"));
		return GroupConstraintImpl.buildAll(preset);
	}

	static Pagination<String> children(ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
			throws IOException, LidVidNotFoundException {
		log.info("Find children of a collection -- both all and latest");
		return selection == ProductVersionSelector.ALL ? RefLogicCollection.childrenAll(control, uid)
				: RefLogicCollection.childrenLatest(control, uid);
	}

	private static Pagination<String> childrenAll(ControlContext control, LidvidsContext uid)
			throws IOException, LidVidNotFoundException {
		PaginationLidvidBuilder productLidvids = new PaginationLidvidBuilder(uid);

		for (final Map<String, Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
				new SearchRequestFactory(
						RequestConstructionContextFactory.given("collection_lidvid", uid.getLidVid(), true),
						control.getConnection()).build(RequestBuildContextFactory.given(false, "product_lid"),
								control.getConnection().getRegistryRefIndex()))) {
			productLidvids.addAll(LidVidUtils.getAllLidVidsByLids(control,
					RequestBuildContextFactory.given(false, "lidvid",
							ReferencingLogicTransmuter.Product.impl().constraints()),
					productLidvids.convert(kvp.get("product_lid"))));
		}
		return productLidvids;
	}

	private static Pagination<String> childrenLatest(ControlContext control, LidvidsContext uid)
			throws IOException, LidVidNotFoundException {
		PaginationLidvidBuilder productLidvids = new PaginationLidvidBuilder(uid);
		RequestConstructionContext requestConstructionContext = RequestConstructionContextFactory
				.given("collection_lidvid", uid.getLidVid(), true);
		RequestBuildContext requestBuildContext = RequestBuildContextFactory.given(true, "product_lidvid");
		String registryRefIndex = control.getConnection().getRegistryRefIndex();
		SearchRequest searchRequest = new SearchRequestFactory(requestConstructionContext, control.getConnection())
				.build(requestBuildContext, registryRefIndex);

		HitIterator pagesOfResults = new HitIterator(control.getConnection().getRestHighLevelClient(), searchRequest);

		for (final Map<String, Object> page : pagesOfResults) {
//			Every "hit" is a page containing n lidvids, where n is presumably determined by OpenSearch configuration
			Object collectionOfLidvids = page.get("product_lidvid");
			productLidvids.add(collectionOfLidvids);
		}

		return productLidvids;
	}

	static Pagination<String> parents(ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
			throws IOException, LidVidNotFoundException {
//        TODO: Fully convert this function's internals (and eventually, interface) to use PdsProductIdentifier classes instead of strings
		List<String> keys = Arrays.asList("ref_lid_collection", "ref_lid_collection_secondary", "ref_lidvid_collection",
				"ref_lidvid_collection_secondary");
		List<String> sortedLidStrings;
		Set<String> lids = new HashSet<String>();
		PdsProductIdentifier productIdentifier = PdsProductIdentifier.fromString(uid.getLidVid());
		PaginationLidvidBuilder bundleLidvids = new PaginationLidvidBuilder(uid);

		log.info("Find parents of a collection -- both all and latest");
		log.info("Find parents of collection: " + uid.getLidVid() + "  --- " + productIdentifier.getLid().toString());
		for (String key : keys) {
			for (final Map<String, Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
					new SearchRequestFactory(
							RequestConstructionContextFactory.given(key, productIdentifier.getLid().toString(), true),
							control.getConnection())
							.build(RequestBuildContextFactory.given(true, "lid",
									ReferencingLogicTransmuter.Bundle.impl().constraints()),
									control.getConnection().getRegistryIndex()))) {
				lids.addAll(bundleLidvids.convert(kvp.get("lid")));
			}
		}
		sortedLidStrings = new ArrayList<String>(lids);
		Collections.sort(sortedLidStrings); // TODO: Implement comparison for PdsLids (only with other PdsLids)
		List<PdsProductIdentifier> sortedLids = sortedLidStrings.stream().map(PdsLid::fromString)
				.collect(Collectors.toList());

		if (selection == ProductVersionSelector.ALL) {
			bundleLidvids.addAll(
					LidVidUtils.getAllLidVidsByLids(control, RequestBuildContextFactory.empty(), sortedLidStrings));
		} else {
			RequestBuildContext reqContext = RequestBuildContextFactory.empty();
			for (PdsProductIdentifier lid : sortedLids) {
				try {
					PdsLidVid latestLidvid = LidVidUtils.getLatestLidVidByLid(control, reqContext,
							lid.getLid().toString());
					bundleLidvids.add(latestLidvid.toString());
				} catch (LidVidNotFoundException e) {
					log.warn("LID is referenced but is in non-findable archive-status or does not exist in db: "
							+ e.toString());
				}
			}
		}

		return bundleLidvids;
	}

	@Override
	public RequestAndResponseContext member(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException,
			UnknownGroupNameException {
		if (twoSteps)
			throw new MembershipException(input.getIdentifier(), "members/members", "collections");
		return RequestAndResponseContext.buildRequestAndResponseContext(context, input,
				RefLogicCollection.children(context, input.getSelector(), input));
	}

	@Override
	public RequestAndResponseContext memberOf(ControlContext context, UserContext input, boolean twoSteps)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException,
			UnknownGroupNameException {
		if (twoSteps)
			throw new MembershipException(input.getIdentifier(), "member-of/member-of", "collections");
		return RequestAndResponseContext.buildRequestAndResponseContext(context, input,
				RefLogicCollection.parents(context, input.getSelector(), input));
	}
}
