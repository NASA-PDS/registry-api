package gov.nasa.pds.api.registry;

public interface LidvidsContext
{
	public String getLidVid();
	public Integer getLimit();
	public Integer getStart();
	public boolean getReturnSingularDatum();
}
