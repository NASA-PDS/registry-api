package gov.nasa.pds.api.registry.model;

import org.junit.Assert;
import org.junit.Test;

public class PdsLidTest {
    @Test
    public void testEquality() {
        PdsLid baseLid = new PdsLid("urn:nasa:pds:epoxi");
        PdsLid equalLid = new PdsLid("urn:nasa:pds:epoxi");
        PdsLid unequalLid = new PdsLid("urn:nasa:pds:notEpoxi");

        Assert.assertEquals(baseLid, equalLid);
        Assert.assertEquals(baseLid.hashCode(), equalLid.hashCode());

        Assert.assertNotEquals(baseLid, unequalLid);
        Assert.assertNotEquals(baseLid.hashCode(), unequalLid.hashCode());
    }
}
