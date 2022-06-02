package gov.nasa.pds.api.registry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.GroupConstraint;

public class GroupConstraintImpl implements GroupConstraint
{
	final private Map<String,List<String>> all;
	final private Map<String,List<String>> any;
	final private Map<String,List<String>> not;

	private GroupConstraintImpl(Map<String,List<String>> all, Map<String,List<String>> any, Map<String,List<String>>not)
	{
		this.all = all;
		this.any = any;
		this.not = not;
	}

	@Override
	public Map<String, List<String>> all() { return all;}
	@Override
	public Map<String, List<String>> any() { return any; }
	@Override
	public Map<String, List<String>> not() { return not; }

	final private static Map<String,List<String>> EMPTY = new HashMap<String,List<String>>();
	public static GroupConstraint empty() { return new GroupConstraintImpl(EMPTY, EMPTY, EMPTY); }
	public static GroupConstraint buildAll(Map<String,List<String>> map)
	{ return new GroupConstraintImpl(new HashMap<String,List<String>>(map), EMPTY, EMPTY); }
	public static GroupConstraint buildAny(Map<String,List<String>> map)
	{ return new GroupConstraintImpl(EMPTY, new HashMap<String,List<String>>(map), EMPTY); }
	public static GroupConstraint buildNot(Map<String,List<String>> map)
	{ return new GroupConstraintImpl(EMPTY, EMPTY, new HashMap<String,List<String>>(map)); }
	public static GroupConstraint buildAllAny(Map<String,List<String>> allmap, Map<String,List<String>> anymap)
	{ return new GroupConstraintImpl(new HashMap<String,List<String>>(allmap), new HashMap<String,List<String>>(anymap), EMPTY); }
	public static GroupConstraint buildAllNot(Map<String,List<String>> allmap, Map<String,List<String>> notmap)
	{ return new GroupConstraintImpl(new HashMap<String,List<String>>(allmap), EMPTY, new HashMap<String,List<String>>(notmap)); }
	public static GroupConstraint buildAnyNot(Map<String,List<String>> anymap, Map<String,List<String>> notmap)
	{ return new GroupConstraintImpl(EMPTY, new HashMap<String,List<String>>(anymap), new HashMap<String,List<String>>(notmap)); }
	public static GroupConstraint build (Map<String,List<String>> allmap, Map<String,List<String>> anymap, Map<String,List<String>> notmap)
	{ return new GroupConstraintImpl(new HashMap<String,List<String>>(allmap), new HashMap<String,List<String>>(anymap), new HashMap<String,List<String>>(notmap)); }
}
