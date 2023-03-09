package gov.nasa.pds.api.registry.model;

import gov.nasa.pds.api.registry.model.identifiers.PdsLid;
import gov.nasa.pds.api.registry.model.identifiers.PdsLidVid;
import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import org.junit.Assert;
import org.junit.Test;

public class PdsProductIdentifierTest {
  @Test
  public void testSubclassObjectFactory() {
    PdsProductIdentifier lid = PdsProductIdentifier.fromString("urn:nasa:pds:epoxi");
    Assert.assertTrue(lid instanceof PdsLid);

    PdsProductIdentifier lidvid = PdsProductIdentifier.fromString("urn:nasa:pds:epoxi::1.0");
    Assert.assertTrue(lidvid instanceof PdsLidVid);

    PdsProductIdentifier lidnull = PdsProductIdentifier.fromString("urn:nasa:pds:epoxi::");
    Assert.assertTrue(lidnull instanceof PdsLid);
    Assert.assertEquals("urn:nasa:pds:epoxi", lidnull.getLid().toString());

    PdsProductIdentifier lidv = PdsProductIdentifier.fromString("urn:nasa:pds:epoxi::1");
    Assert.assertTrue(lidv instanceof PdsLid);
    Assert.assertEquals("urn:nasa:pds:epoxi", lidv.getLid().toString());

    PdsProductIdentifier lidvi = PdsProductIdentifier.fromString("urn:nasa:pds:epoxi::1.");
    Assert.assertTrue(lidvi instanceof PdsLid);
    Assert.assertEquals("urn:nasa:pds:epoxi", lidv.getLid().toString());

  }
}
