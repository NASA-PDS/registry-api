package gov.nasa.pds.api.registry.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.RequestConstructionContext;

public class RequestConstructionContextFactory {

  public static RequestConstructionContext minimal() {
    return new SimpleRequestConstructionContext(new HashMap<>(), true);
  }
  public static RequestConstructionContext given(String lidvid) {
    return new SimpleRequestConstructionContext(lidvid);
  }

  public static RequestConstructionContext given(List<String> lidvids) {
    Map<String, List<String>> kvps = new HashMap<String, List<String>>();
    kvps.put("lidvid", lidvids);
    return new SimpleRequestConstructionContext(kvps, true);
  }

  public static RequestConstructionContext given(String key, String value, boolean asTerm) {
    List<String> values = Arrays.asList(value);
    Map<String, List<String>> kvps = new HashMap<String, List<String>>();
    kvps.put(key, values);
    return new SimpleRequestConstructionContext(kvps, asTerm);
  }

  public static RequestConstructionContext given(String key, List<String> values, boolean asTerm) {
    Map<String, List<String>> kvps = new HashMap<String, List<String>>();
    kvps.put(key, values);
    return new SimpleRequestConstructionContext(kvps, asTerm);
  }
}
