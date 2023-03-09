package gov.nasa.pds.api.registry.exceptions;

import java.util.Set;

public class UnknownGroupNameException extends Exception {
	private static final long serialVersionUID = -5630215762959235121L;

	public UnknownGroupNameException(String group, Set<String> known) {
		super("Unknown group '" + group + "'. All known groups: " + String.join(", ", known));
	}
}
