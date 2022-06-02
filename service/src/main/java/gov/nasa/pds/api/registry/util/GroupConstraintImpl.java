package gov.nasa.pds.api.registry.util;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.api.registry.GroupConstraint;

public class GroupConstraintImpl implements GroupConstraint
{
	final private Map<String,String> all;
	final private Map<String,String> any;
	final private Map<String,String> not;

	private GroupConstraintImpl(Map<String,String> all, Map<String,String> any, Map<String,String>not)
	{
		this.all = all;
		this.any = any;
		this.not = not;
	}

	@Override
	public Map<String, String> all() { return all;}
	@Override
	public Map<String, String> any() { return any; }
	@Override
	public Map<String, String> not() { return not; }

	final private static Map<String,String> EMPTY = new HashMap<String,String>();
	public static GroupConstraint empty() { return new GroupConstraintImpl(EMPTY, EMPTY, EMPTY); }
	public static GroupConstraint buildAll(Map<String,String> map)
	{ return new GroupConstraintImpl(new HashMap<String,String>(map), EMPTY, EMPTY); }
	public static GroupConstraint buildAny(Map<String,String> map)
	{ return new GroupConstraintImpl(EMPTY, new HashMap<String,String>(map), EMPTY); }
	public static GroupConstraint buildNot(Map<String,String> map)
	{ return new GroupConstraintImpl(EMPTY, EMPTY, new HashMap<String,String>(map)); }
	public static GroupConstraint buildAllAny(Map<String,String> allmap, Map<String,String> anymap)
	{ return new GroupConstraintImpl(new HashMap<String,String>(allmap), new HashMap<String,String>(anymap), EMPTY); }
	public static GroupConstraint buildAllNot(Map<String,String> allmap, Map<String,String> notmap)
	{ return new GroupConstraintImpl(new HashMap<String,String>(allmap), EMPTY, new HashMap<String,String>(notmap)); }
	public static GroupConstraint buildAnyNot(Map<String,String> anymap, Map<String,String> notmap)
	{ return new GroupConstraintImpl(EMPTY, new HashMap<String,String>(anymap), new HashMap<String,String>(notmap)); }
	public static GroupConstraint build (Map<String,String> allmap, Map<String,String> anymap, Map<String,String> notmap)
	{ return new GroupConstraintImpl(new HashMap<String,String>(allmap), new HashMap<String,String>(anymap), new HashMap<String,String>(notmap)); }
}
