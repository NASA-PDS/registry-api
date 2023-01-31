package gov.nasa.pds.api.registry.model.identifiers;

public class PdsLidVid extends PdsProductIdentifier implements Comparable<PdsLidVid>{
    private final PdsLid lid;
    private final PdsVid vid;

    public PdsLid getLid() {
        return lid;
    }

    public PdsVid getVid() {
        return vid;
    }

    public PdsLidVid(PdsLid lid, PdsVid vid) {
        this.lid = lid;
        this.vid = vid;
    }

    public static PdsLidVid fromString(String lidVidString) {
        String[] chunks = lidVidString.split(LIDVID_SEPARATOR);

        if (chunks.length != 2) {
            String errMsg = String.format("Provided value '%s' is not a valid LIDVID", lidVidString);
            throw new IllegalArgumentException(errMsg);
        }

        PdsLid lid = new PdsLid(chunks[0]);
        PdsVid vid = PdsVid.fromString(chunks[1]);

        return new PdsLidVid(lid, vid);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PdsLidVid)) {
            return false;
        }

        PdsLidVid otherAsLidVid = (PdsLidVid) other;
        return this.lid.equals(otherAsLidVid.lid) && this.vid.equals(otherAsLidVid.vid);
    }

    @Override
    public String toString() {
        return String.format("%s::%s", this.lid, this.vid);
    }

    @Override
    public int compareTo(PdsLidVid other) {
        if (this.equals(other)) {
            return 0;
        }

        if (!this.lid.equals(other.lid)) {
            String errMsg = String.format("Comparison is only defined between LIDVIDs with identical LIDs (got '%s', '%s')", this.lid, other.lid);
            throw new IllegalArgumentException(errMsg);
        }

        return this.vid.compareTo(other.vid);
    }
}
