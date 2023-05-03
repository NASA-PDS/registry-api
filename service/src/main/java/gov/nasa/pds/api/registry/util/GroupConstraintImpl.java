package gov.nasa.pds.api.registry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.GroupConstraint;

@Immutable
public class GroupConstraintImpl implements GroupConstraint {
  final private Map<String, List<String>> filter;
  final private Map<String, List<String>> must;
  final private Map<String, List<String>> mustNot;

  private GroupConstraintImpl(Map<String, List<String>> must, Map<String, List<String>> filter,
                              Map<String, List<String>> mustNot) {
    this.must = Map.copyOf(must);
    this.filter = Map.copyOf(filter);
    this.mustNot = Map.copyOf(mustNot);
  }

  @Override
  public Map<String, List<String>> must() {
    return must;
  }

  @Override
  public Map<String, List<String>> filter() {
    return filter;
  }

  @Override
  public Map<String, List<String>> mustNot() {
    return mustNot;
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
