package gov.nasa.pds.api.registry;

import java.util.List;
import java.util.Map;

import com.google.errorprone.annotations.Immutable;

/**
 * A set of search constraints specifying three collections of keyword/value match conditions: FILTERTOANY, MUST and NOT.
 * <p>
 * Taken as a whole, GroupConstraint matches any element that matches any value in FILTERTOANY for the given keyword, matches every condition in MUST, and fails every condition in MUSTNOT.
 */
@Immutable
public interface GroupConstraint {

  /**
   * A single PDS keyword and a set of corresponding values, resulting in a match if a PDS item contains one or more
   * values from the set in its property corresponding to the keyword.
   */
  public Map<String, List<String>> filterToAny();

  /**
   * A set of PDS keywords/values that ALL must be true to define just PDS items that make up this
   * Group.
   */
  public Map<String, List<String>> must();

  /**
   * A set of PDS keywords/values that NONE must be true to define just PDS items that make up this
   * Group.
   */
  public Map<String, List<String>> mustNot();

  /**
   * Return the union of this GroupConstraint and another.  Equivalent to taking the union of both FILTER sets, the union of both MUST sets, and the union of both MUSTNOT sets
   */

  public GroupConstraint union(GroupConstraint otherConstraint);
}
