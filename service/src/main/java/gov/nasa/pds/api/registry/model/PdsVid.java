package gov.nasa.pds.api.registry.model;

public class PdsVid implements Comparable<PdsVid> {
    private final int majorVersion;
    private final int minorVersion;

    public PdsVid(int majorVersion, int minorVersion) {
        if (majorVersion < 1) {
            String errMsg = String.format("majorVersion must be 1 or higher (got '%d'))", majorVersion);
            throw new IllegalArgumentException(errMsg);
        }

        if (minorVersion < 0) {
            String errMsg = String.format("minorVersion must be 0 or higher (got '%d'))", minorVersion);
            throw new IllegalArgumentException(errMsg);
        }

        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public static PdsVid fromString(String vidString) {
        String[] versionChunks = vidString.split("\\.");

        if (versionChunks.length != 2) {
            String errMsg = String.format("Provided value '%s' is not a valid VID", vidString);
            throw new IllegalArgumentException(errMsg);
        }

        int majorVersion = Integer.parseInt(versionChunks[0]);
        int minorVersion = Integer.parseInt(versionChunks[1]);

        return new PdsVid(majorVersion, minorVersion);
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    @Override
    public String toString() {
        return String.format("%d.%d", majorVersion, minorVersion);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PdsVid)) {
            return false;
        }

        PdsVid otherAsVid = (PdsVid) other;
        return this.majorVersion == otherAsVid.getMajorVersion() && this.minorVersion == otherAsVid.getMinorVersion();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public int compareTo(PdsVid o) {
        if (this.equals(o)) {
            return 0;
        }

        if (this.majorVersion == o.getMajorVersion()) {
            return Integer.compare(this.minorVersion, o.getMinorVersion());
        } else {
            return Integer.compare(this.majorVersion, o.getMajorVersion());
        }
    }
}
