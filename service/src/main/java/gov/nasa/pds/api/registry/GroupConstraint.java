package gov.nasa.pds.api.registry;

import java.util.List;
import java.util.Map;

import com.google.errorprone.annotations.Immutable;

/**
 * A set of search constraints specifying three collections of keyword/value match conditions: FILTER, MUST and NOT.
 * <p>
 * Taken as a whole, GroupConstraint matches any element that matches any condition in FILTER, matches every condition in MUST, and fails every condition in MUSTNOT.
 */
@Immutable
public interface GroupConstraint {

  /**
   * A set of PDS keywords/values that ANY must be true to define just PDS items that make up this
   * Group.
   */
  public Map<String, List<String>> filter();

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
}
