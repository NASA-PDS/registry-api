package gov.nasa.pds.api.registry.model.identifiers;

public class PdsLid extends PdsProductIdentifier{
    private String value;

    public PdsLid(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public PdsLid getLid() {
        return this;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static PdsLid fromString(String lid) {
        return new PdsLid(lid);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PdsLid)) {
            return false;
        }

        PdsLid otherAsLid = (PdsLid) other;
        return this.value.equals(otherAsLid.getValue());
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
