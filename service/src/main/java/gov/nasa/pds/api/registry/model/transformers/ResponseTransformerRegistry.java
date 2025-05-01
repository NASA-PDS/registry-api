package gov.nasa.pds.api.registry.model.transformers;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.model.exceptions.AcceptFormatNotSupportedException;

public class ResponseTransformerRegistry {
  private static final Logger log = LoggerFactory.getLogger(ResponseTransformerRegistry.class);
  public static final Map<String, Class<? extends ResponseTransformer>> TRANSFORMERS = new HashMap<>();
  
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

  public static Class<? extends ResponseTransformer> selectTransformerClass(String acceptHeaderValue)
      throws AcceptFormatNotSupportedException {
    log.debug("Processing Accept header: '{}'", acceptHeaderValue);
    
    if (acceptHeaderValue == null || acceptHeaderValue.trim().isEmpty()) {
      log.debug("Accept header is null or empty, using default JSON transformer");
      return Pds4JsonProductTransformer.class;
    }

    // split by , and remove extra spaces
    String[] acceptOrderedValues =
        Arrays.stream(acceptHeaderValue.split(",")).map(String::trim).toArray(String[]::new);
    
    log.debug("Parsed Accept values: {}", Arrays.toString(acceptOrderedValues));

    for (String acceptValue : acceptOrderedValues) {
      log.debug("Checking Accept value: '{}'", acceptValue);
      if (TRANSFORMERS.containsKey(acceptValue)) {
        log.debug("Found matching transformer for: {}", acceptValue);
        return TRANSFORMERS.get(acceptValue);
      }
    }

    // if none of the Accept format proposed matches
    log.warn("No matching transformer found for Accept header: {}", acceptHeaderValue);
    throw new AcceptFormatNotSupportedException(
        "None of the format(s) " + acceptHeaderValue + " is supported.");
  }
} 