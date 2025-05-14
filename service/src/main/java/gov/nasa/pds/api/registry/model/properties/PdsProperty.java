package gov.nasa.pds.api.registry.model.properties;

import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import static gov.nasa.pds.api.registry.model.SearchUtil.jsonPropertyToOpenProperty;
import static gov.nasa.pds.api.registry.model.SearchUtil.openPropertyToJsonProperty;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a unified interface for dealing with PDS product properties, which are named differently
 * depending on the context. The canonical names used by OpenSearch use '/' as a separator, whereas
 * users and the API URL interface replace these with "."
 */
public class PdsProperty implements Comparable<PdsProperty> {

  private static final Logger log = LoggerFactory.getLogger(PdsProperty.class);

  private final String value;


  public PdsProperty(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      log.debug("no equal because null or inconsistent class");
      return false;
    }
    String currentJsonPropertyString = this.toJsonPropertyString();
    String otherJsonPropertyString = ((PdsProperty) obj).toJsonPropertyString();

    boolean isEqual = currentJsonPropertyString.equals(otherJsonPropertyString);
    log.debug("Compare properties as Jsom {} == {} --> {}", currentJsonPropertyString,
        otherJsonPropertyString, isEqual);
    return isEqual;
  }


  @Override
  public int hashCode() {
    return Objects.hash(this.toJsonPropertyString());
  }

  @Override
  public int compareTo(PdsProperty other) {
    return this.toJsonPropertyString().compareTo(other.toJsonPropertyString());
  }


  /**
   * Returns the property, in the format used by OpenSearch
   */
  public static String toOpenPropertyString(String value) {
    // TODO: replace all uses of SearchUtil.jsonPropertyToOpenProperty with PdsProperty, move
    // conversion logic here,
    // and excise SearchUtil.jsonPropertyToOpenProperty
    return jsonPropertyToOpenProperty(value);
  }

  public String toOpenPropertyString() {
    return toOpenPropertyString(this.value);
  }

  /**
   * Returns the property, in the format used by the API interface (i.e. end-users)
   */
  public static String toJsonPropertyString(String value) {
    // TODO: replace all uses of SearchUtil.openPropertyToJsonProperty with PdsProperty, move
    // conversion logic here,
    // and excise SearchUtil.openPropertyToJsonProperty

    // TODO: Remove this stopgap once https://github.com/NASA-PDS/registry-api/issues/528 is
    // resolved
    try {
      return openPropertyToJsonProperty(value);
    } catch (UnsupportedSearchProperty err) {
      return value.replace('/', '.');
    }
  }

  public String toJsonPropertyString() {
    return toJsonPropertyString(this.value);
  }


}
