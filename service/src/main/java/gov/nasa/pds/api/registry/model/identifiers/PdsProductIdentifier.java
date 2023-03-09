package gov.nasa.pds.api.registry.model.identifiers;

public abstract class PdsProductIdentifier {
  protected static final String LIDVID_SEPARATOR = "::";

  public abstract PdsLid getLid();

  @Override
  public abstract String toString();

  public static PdsProductIdentifier fromString(String identifier) {
    if (identifier.length() == 0)
      return null;

    try {
      return identifier.contains(LIDVID_SEPARATOR) ? PdsLidVid.fromString(identifier)
          : PdsLid.fromString(identifier);
    } catch (IllegalArgumentException err) {
      return resolvePartialLidVid(identifier);
    }
  }

  private static PdsLid resolvePartialLidVid(String identifier) {
    return PdsLid.fromString(identifier.split(LIDVID_SEPARATOR)[0]);
  }
}
