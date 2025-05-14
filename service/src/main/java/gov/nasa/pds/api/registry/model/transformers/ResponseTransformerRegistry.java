package gov.nasa.pds.api.registry.model.transformers;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import gov.nasa.pds.api.registry.model.exceptions.AcceptFormatNotSupportedException;

public class ResponseTransformerRegistry {
  private static final Logger log = LoggerFactory.getLogger(ResponseTransformerRegistry.class);
  public static final Map<String, Class<? extends ResponseTransformer>> TRANSFORMERS =
      new HashMap<>();

  static {
    TRANSFORMERS.put("*", PdsProductTransformer.class);
    TRANSFORMERS.put("*/*", PdsProductTransformer.class);
    TRANSFORMERS.put("application/csv", WyriwygProductTransformer.class);
    TRANSFORMERS.put("application/json", PdsProductTransformer.class);
    TRANSFORMERS.put("application/kvp+json", WyriwygProductTransformer.class);
    TRANSFORMERS.put("application/json", PdsProductTransformer.class);
    TRANSFORMERS.put("application/xml", PdsProductTransformer.class);
    TRANSFORMERS.put("text/csv", WyriwygProductTransformer.class);
    TRANSFORMERS.put("text/html", PdsProductTransformer.class);
    TRANSFORMERS.put("text/xml", PdsProductTransformer.class);
    TRANSFORMERS.put("application/vnd.nasa.pds.pds4+json", Pds4JsonProductTransformer.class);
    TRANSFORMERS.put("application/vnd.nasa.pds.pds4+xml", Pds4XmlProductTransformer.class);
  }

  private ResponseTransformerRegistry() {
    throw new AssertionError("Cannot instantiate ResponseTransformerRegistry");
  }

  public static String[] parseAcceptValues(String input, String defaultValue) {
    if (input == null || input.trim().isEmpty()) {
      log.info("No Accept header provided by the user, assigning the default value {}",
          defaultValue);
      return new String[] {defaultValue};
    }

    return Arrays.stream(input.split(",")).map(String::trim).filter(s -> !s.isEmpty())
        .toArray(String[]::new);
  }

  public static Class<? extends ResponseTransformer> selectTransformerClass(
      String acceptHeaderValue) throws AcceptFormatNotSupportedException {



    String[] acceptOrderedValues =
        parseAcceptValues(acceptHeaderValue, MediaType.APPLICATION_JSON_VALUE);

    for (String acceptValue : acceptOrderedValues) {
      log.debug("Checking Accept value: '{}'", acceptValue);
      if (TRANSFORMERS.containsKey(acceptValue)) {
        log.debug("Found matching transformer for: {}", acceptValue);
        return TRANSFORMERS.get(acceptValue);
      }
    }


    // if none of the Accept format proposed matches
    throw new AcceptFormatNotSupportedException(
        "None of the format(s) " + acceptHeaderValue + " is supported.");

  }

}
