package gov.nasa.pds.api.registry.model.identifiers;

public class PdsLid {
    private String value;

    public PdsLid(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
    @Override
    public String toString() {
        return this.value;
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
