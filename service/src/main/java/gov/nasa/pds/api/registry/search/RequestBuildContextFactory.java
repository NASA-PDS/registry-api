package gov.nasa.pds.api.registry.search;

import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.RequestBuildContext;

public class RequestBuildContextFactory {
  public static RequestBuildContext empty() {
    return new SimpleRequestBuildContext(true);
  }

  public static RequestBuildContext given(boolean justLatest, String field) {
    return new SimpleRequestBuildContext(justLatest, Arrays.asList(field));
  }

  public static RequestBuildContext given(boolean justLatest, List<String> fields) {
    return new SimpleRequestBuildContext(justLatest, fields);
  }

  public static RequestBuildContext given(boolean justLatest, String field,
      GroupConstraint preset) {
    return new SimpleRequestBuildContext(justLatest, Arrays.asList(field), preset);
  }

  public static RequestBuildContext given(boolean justLatest, List<String> fields,
      GroupConstraint preset) {
    return new SimpleRequestBuildContext(justLatest, fields, preset);
  }
}
