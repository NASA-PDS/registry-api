package gov.nasa.pds.api.registry.model;

import java.util.HashSet;
import java.util.Set;

import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;

public enum ReferencingLogicTransmuter
{
	Any(new RefLogicAny(), "", "any"),
	Bundle(new RefLogicBundle(), "Product_Bundle", "bundle"),
	Collection(new RefLogicCollection(), "Product_Collection", "collection"),
	Product(new RefLogicProduct(), "", "product");
	
	final private ReferencingLogic refLogic;
	final private String pds_name;
	final private String swagger_name;
	private ReferencingLogicTransmuter (ReferencingLogic impl, String pds_name, String swagger_name)
	{
		this.refLogic = impl;
		this.pds_name = pds_name;
		this.swagger_name = swagger_name;
	}
	
	private static ReferencingLogicTransmuter get (String name, boolean usingPDSName) throws NothingFoundException
	{
		ReferencingLogicTransmuter resultant = null;
		
		if (name.length() == 0) return ReferencingLogicTransmuter.Any;

		for (ReferencingLogicTransmuter pc : ReferencingLogicTransmuter.values())
		{
			if (name.equals(usingPDSName ? pc.pds_name : pc.swagger_name))
			{
				resultant = pc;
				break;
			}
		}
		
		if (resultant == null) throw new NothingFoundException();
		return resultant;
	}
	
	public static ReferencingLogicTransmuter getByProductClass (String name) throws UnknownGroupNameException
	{
		try { return ReferencingLogicTransmuter.get(name, true); }
		catch (NothingFoundException nfe)
		{
			return ReferencingLogicTransmuter.Any;
//			Set<String> known = new HashSet<String>();
//			for (ReferencingLogicTransmuter pc : ReferencingLogicTransmuter.values()) known.add(pc.pds_name);
//			throw new UnknownGroupNameException(name, known);
		}
	}

	public static ReferencingLogicTransmuter getBySwaggerGroup (String name) throws UnknownGroupNameException
	{
		try { return ReferencingLogicTransmuter.get(name, false); }
		catch (NothingFoundException nfe)
		{
			Set<String> known = new HashSet<String>();
			for (ReferencingLogicTransmuter pc : ReferencingLogicTransmuter.values()) known.add(pc.swagger_name);
			throw new UnknownGroupNameException(name, known);
		}
		
	}
	public ReferencingLogic impl() { return this.refLogic; }
}
