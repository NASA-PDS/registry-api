package gov.nasa.pds.api.registry;

import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.registry.opensearch.OpenSearchRegistryConnection;

public interface ControlContext
{
	public ObjectMapper getObjectMapper();
	public URL getBaseURL();
	public OpenSearchRegistryConnection getConnection();
}
