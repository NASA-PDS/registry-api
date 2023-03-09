package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.RequestConstructionContext;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;

class SimpleRequestConstructionContext implements RequestConstructionContext {
	final private boolean isTerm;
	final private Map<String, List<String>> kvps;
	final private PdsProductIdentifier productIdentifier;

	SimpleRequestConstructionContext(Map<String, List<String>> kvps) {
		this.isTerm = false;
		this.kvps = kvps;
		this.productIdentifier = null;
	}

	SimpleRequestConstructionContext(Map<String, List<String>> kvps, boolean asTerm) {
		this.isTerm = asTerm;
		this.kvps = kvps;
		this.productIdentifier = null;
	}

	SimpleRequestConstructionContext(String productIdentifier) {
		this.isTerm = false;
		this.kvps = new HashMap<String, List<String>>();
		this.productIdentifier = PdsProductIdentifier.fromString(productIdentifier);
	}

	SimpleRequestConstructionContext(String productIdentifier, boolean isTerm) {
		this.isTerm = isTerm;
		this.kvps = new HashMap<String, List<String>>();
		this.productIdentifier = PdsProductIdentifier.fromString(productIdentifier);
	}

	@Override
	public List<String> getKeywords() {
		return new ArrayList<String>();
	}

	@Override
	public Map<String, List<String>> getKeyValuePairs() {
		return this.kvps;
	}

	@Override
	public PdsProductIdentifier getProductIdentifier() {
		return this.productIdentifier;
	}

	@Override
	public String getProductIdentifierString() {
		return this.productIdentifier == null ? "" : this.productIdentifier.toString();
	}

	@Override
	public String getQueryString() {
		return "";
	}

	@Override
	public boolean isTerm() {
		return this.isTerm;
	}
}
