package gov.nasa.pds.api.registry;

import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ControlContext
{
	public ObjectMapper getObjectMapper();
	public URL getBaseURL();
	public ConnectionContext getConnection();
}
