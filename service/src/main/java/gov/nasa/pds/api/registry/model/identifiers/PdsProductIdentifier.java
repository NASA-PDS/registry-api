package gov.nasa.pds.api.registry.model.identifiers;

public abstract class PdsProductIdentifier {
    protected static final String LIDVID_SEPARATOR = "::";

    public abstract PdsLid getLid();
    @Override
    public abstract String toString();

    public static PdsProductIdentifier fromString(String identifier) {
        return identifier.contains(LIDVID_SEPARATOR) ? PdsLidVid.fromString(identifier) : PdsLid.fromString(identifier);
    }
}
