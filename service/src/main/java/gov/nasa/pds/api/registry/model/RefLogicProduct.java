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

import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;
import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.LidvidsContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.RequestBuildContextFactory;
import gov.nasa.pds.api.registry.search.RequestConstructionContextFactory;
import gov.nasa.pds.api.registry.search.SearchRequestFactory;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

@Immutable
class RefLogicProduct extends RefLogicAny implements ReferencingLogic {
	private static final Logger log = LoggerFactory.getLogger(RefLogicProduct.class);

	@Override
	public GroupConstraint constraints() {
		Map<String, List<String>> preset = new HashMap<String, List<String>>();
		preset.put("product_class", Arrays.asList("Product_Bundle", "Product_Collection"));
		return GroupConstraintImpl.buildNot(preset);
	}

	static Pagination<String> grandparents(ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
			throws IOException, LidVidNotFoundException {
		log.info("Find the grandparents of a product -- both all and latest");
		List<String> parents = RefLogicProduct
				.parents(control, ProductVersionSelector.LATEST, new Unlimited(uid.getLidVid())).page();
		PaginationLidvidBuilder grandparents = new PaginationLidvidBuilder(uid);
		for (String parent : parents) {
			log.info("Find all the parents of collection: " + parent);
			grandparents.addAll(RefLogicCollection.parents(control, selection, new Unlimited(parent)).page());
			log.info("Find grandparents size: " + String.valueOf(grandparents.size()));
			for (String gp : grandparents.page()) {
				log.info("   grandparent: " + gp);
			}
		}
		return grandparents;
	}

	static Pagination<String> parents(ControlContext control, ProductVersionSelector selection, LidvidsContext uid)
			throws IOException, LidVidNotFoundException {
		List<String> sortedLidStrings;
		PaginationLidvidBuilder parents = new PaginationLidvidBuilder(uid);
		Set<String> lids = new HashSet<String>();

		log.info("Find the parents of a product -- both all and latest");
		for (final Map<String, Object> kvp : new HitIterator(control.getConnection().getRestHighLevelClient(),
				new SearchRequestFactory(
						RequestConstructionContextFactory.given("product_lidvid", uid.getLidVid(), true),
						control.getConnection()).build(RequestBuildContextFactory.given(true, "collection_lid"),
								control.getConnection().getRegistryRefIndex()))) {
			lids.addAll(parents.convert(kvp.get("collection_lid")));
		}
		sortedLidStrings = new ArrayList<>(lids);
		Collections.sort(sortedLidStrings);
		List<PdsProductIdentifier> sortedLids = sortedLidStrings.stream().map(PdsLid::fromString)
				.collect(Collectors.toList());

		if (selection == ProductVersionSelector.ALL) {
			parents.addAll(
					LidVidUtils.getAllLidVidsByLids(control, RequestBuildContextFactory.empty(), sortedLidStrings));
		} else {
			RequestBuildContext reqContext = RequestBuildContextFactory.empty();
			for (PdsProductIdentifier id : sortedLids) {
				try {
					PdsLidVid latestLidvid = LidVidUtils.getLatestLidVidByLid(control, reqContext,
							id.getLid().toString());
					parents.add(latestLidvid.toString());
				} catch (LidVidNotFoundException e) {
					log.warn("LID is referenced but is in non-findable archive-status or does not exist in db: "
							+ e.toString());
				}
			}
		}

		return parents;
	}
}
