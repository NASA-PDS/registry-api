package gov.nasa.pds.api.registry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.GroupConstraint;

@Immutable
public class GroupConstraintImpl implements GroupConstraint {
  final private Map<String, List<String>> all;
  final private Map<String, List<String>> any;
  final private Map<String, List<String>> not;

  private GroupConstraintImpl(Map<String, List<String>> all, Map<String, List<String>> any,
      Map<String, List<String>> not) {
    this.all = Map.copyOf(all);
    this.any = Map.copyOf(any);
    this.not = Map.copyOf(not);
  }

  @Override
  public Map<String, List<String>> must() {
    return all;
  }

  @Override
  public Map<String, List<String>> filter() {
    return any;
  }

  @Override
  public Map<String, List<String>> mustNot() {
    return not;
  }

  final private static Map<String, List<String>> EMPTY = new HashMap<String, List<String>>();

  public static GroupConstraint empty() {
    return new GroupConstraintImpl(EMPTY, EMPTY, EMPTY);
  }

  public static GroupConstraint buildAll(Map<String, List<String>> map) {
    return new GroupConstraintImpl(map, EMPTY, EMPTY);
  }

  public static GroupConstraint buildAny(Map<String, List<String>> map) {
    return new GroupConstraintImpl(EMPTY, map, EMPTY);
  }

  public static GroupConstraint buildNot(Map<String, List<String>> map) {
    return new GroupConstraintImpl(EMPTY, EMPTY, map);
  }

  public static GroupConstraint buildAllAny(Map<String, List<String>> allmap,
      Map<String, List<String>> anymap) {
    return new GroupConstraintImpl(allmap, anymap, EMPTY);
  }

  public static GroupConstraint buildAllNot(Map<String, List<String>> allmap,
      Map<String, List<String>> notmap) {
    return new GroupConstraintImpl(allmap, EMPTY, notmap);
  }

  public static GroupConstraint buildAnyNot(Map<String, List<String>> anymap,
      Map<String, List<String>> notmap) {
    return new GroupConstraintImpl(EMPTY, anymap, notmap);
  }

  public static GroupConstraint build(Map<String, List<String>> allmap,
      Map<String, List<String>> anymap, Map<String, List<String>> notmap) {
    return new GroupConstraintImpl(allmap, anymap, notmap);
  }
}
