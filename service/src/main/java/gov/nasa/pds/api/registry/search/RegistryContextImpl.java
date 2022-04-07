package gov.nasa.pds.api.registry.search;

import gov.nasa.pds.api.registry.RegistryContext;

public class RegistryContextImpl implements RegistryContext
{
	public final String regIndex;
	public final String refIndex;
	public final int timeoutSeconds;

	public RegistryContextImpl (String regIndex, String refIndex, int timeoutSeconds)
	{
		this.refIndex = refIndex;
		this.regIndex = regIndex;
		this.timeoutSeconds = timeoutSeconds;
	}

	@Override
	public String getRegistryIndex() { return this.regIndex; }

	@Override
	public String getRegistryRefIndex() { return this.refIndex; }

	@Override
	public int getTimeOutSeconds() { return this.timeoutSeconds; }

}
