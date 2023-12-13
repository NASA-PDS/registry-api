package gov.nasa.pds.api.registry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.GroupConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public class GroupConstraintImpl implements GroupConstraint {

  private static final Logger log = LoggerFactory.getLogger(GroupConstraintImpl.class);

  final private Map<String, List<String>> filterToAny;
  final private Map<String, List<String>> must;
  final private Map<String, List<String>> mustNot;

  private GroupConstraintImpl(Map<String, List<String>> must, Map<String, List<String>> filterToAny,
                              Map<String, List<String>> mustNot) {
    int filterTermsKeysCount = filterToAny.keySet().size();
    if (filterTermsKeysCount > 1) {
      throw new RuntimeException(
              "Filtering on multiple keys is not supported by OpenSearch terms query, so is undefined");
    }

    this.must = Map.copyOf(must);
    this.filterToAny = Map.copyOf(filterToAny);
    this.mustNot = Map.copyOf(mustNot);
  }

  @Override
  public Map<String, List<String>> must() {
    return must;
  }

  @Override
  public Map<String, List<String>> filterToAny() {
    return filterToAny;
  }

  @Override
  public Map<String, List<String>> mustNot() {
    return mustNot;
  }


  /**
   * Perform a union on a pair of a single constraint type (filter, must or mustNot)
   */
  private Map<String, List<String>> performConstraintUnion(Map<String, List<String>> constraint1, Map<String, List<String>> constraint2) {
    Map<String, List<String>> unionConstraint = new HashMap<>(constraint1);
    constraint2.forEach(
        (key, value) -> {
          if (unionConstraint.containsKey(key)) {
            unionConstraint.get(key).addAll(value);
            List<String> deduplicatedValue = List.copyOf(Set.copyOf(unionConstraint.get(key)));
            unionConstraint.put(key, deduplicatedValue);
          } else {
            unionConstraint.put(key, List.copyOf(value));
          }
        });

    return unionConstraint;
  }

  private void logMutuallyExclusiveMustConditions(Map<String, List<String>> mustConditions) {
    mustConditions.forEach(
        (key, value) -> {
          if (value.size() > 1) {
            log.warn(
                "Multiple values for 'must' condition are mutually-exclusive and will always fail: property='"
                    + key
                    + "', values=['"
                    + String.join("', '", value)
                    + "']");
          }
        });
  }

  @Override
  public GroupConstraint union(GroupConstraint otherGroupConstraint) {
    Map<String, List<String>> unionFilter =
        performConstraintUnion(this.filterToAny, otherGroupConstraint.filterToAny());
    Map<String, List<String>> unionMust =
        performConstraintUnion(this.must, otherGroupConstraint.must());
    Map<String, List<String>> unionMustNot =
        performConstraintUnion(this.mustNot, otherGroupConstraint.mustNot());

    logMutuallyExclusiveMustConditions(unionMust);

    return new GroupConstraintImpl(unionMust, unionFilter, unionMustNot);
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
