package gov.nasa.pds.api.registry.model.properties;

import java.util.List;

/**
 * Which properties to fetch from the OpenSearch _source: the included ones (everything if empty)
 * minus the excluded ones. As in OpenSearch, a property which is both included and excluded is
 * excluded.
 *
 * @param included the properties to fetch, empty for all of them
 * @param excluded the properties not to fetch
 */
public record FieldSelection(List<PdsProperty> included, List<PdsProperty> excluded) {

  /** Fetch everything. */
  public static final FieldSelection ALL = new FieldSelection(List.of(), List.of());

  public FieldSelection {
    included = included == null ? List.of() : List.copyOf(included);
    excluded = excluded == null ? List.of() : List.copyOf(excluded);
  }
}
