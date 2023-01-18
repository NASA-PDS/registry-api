package gov.nasa.pds.api.registry.model;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PdsLidVidTest {

    @Test
    public void testValidInstantiations() {
        PdsLidVid lidVid = PdsLidVid.fromString("urn:nasa:pds:epoxi::1.0");
    }

    @Test
    public void testInvalidInstantiations() {
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            PdsLidVid.fromString("some:lid:without:vid");
        });
    }


    @Test
    public void testEquality() {
        PdsLidVid base = PdsLidVid.fromString("urn:nasa:pds:epoxi::1.0");
        PdsLidVid equal = PdsLidVid.fromString("urn:nasa:pds:epoxi::1.0");
        PdsLidVid differentLid = PdsLidVid.fromString("urn:nasa:pds:notEpoxi::1.0");
        PdsLidVid differentVid = PdsLidVid.fromString("urn:nasa:pds:epoxi::1.1");

        Assert.assertEquals(base, equal);
        Assert.assertEquals(base.hashCode(), equal.hashCode());

        Assert.assertNotEquals(base, differentLid);
        Assert.assertNotEquals(base.hashCode(), differentLid.hashCode());

        Assert.assertNotEquals(base, differentVid);
        Assert.assertNotEquals(base.hashCode(), differentVid.hashCode());
    }

    @Test
    public void testComparison() {
        PdsLidVid first = PdsLidVid.fromString("something::1.0");
        PdsLidVid second = PdsLidVid.fromString("something::2.0");
        PdsLidVid third = PdsLidVid.fromString("something::3.0");
        PdsLidVid mismatched = PdsLidVid.fromString("somethingElse::4.0");

        List<PdsLidVid> valid = Arrays.asList(third, first, second);
        Collections.sort(valid);
        Assert.assertArrayEquals(new PdsLidVid[]{first, second, third}, valid.toArray());

        List<PdsLidVid> invalid = Arrays.asList(third, first, second, mismatched);
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            Collections.sort(invalid);
        });
    }
}
