package gov.nasa.pds.api.registry.model;

import gov.nasa.pds.api.registry.model.identifiers.PdsVid;
import org.junit.Assert;
import org.junit.Test;

public class PdsVidTest {

  @Test
  public void testValidInstantiations() {
    PdsVid.fromString("1.0");
  }

  @Test
  public void testInvalidInstantiations() {
    Assert.assertThrows(IllegalArgumentException.class, () -> {
      PdsVid.fromString("1.-1");
    });

    Assert.assertThrows(IllegalArgumentException.class, () -> {
      PdsVid.fromString("1.0.0");
    });
  }

  @Test
  public void testEquality() {
    PdsVid base = new PdsVid(1, 0);
    PdsVid equal = new PdsVid(1, 0);
    PdsVid differentMajorVersion = new PdsVid(2, 0);
    PdsVid differentMinorVersion = new PdsVid(1, 1);

    Assert.assertEquals(base, equal);
    Assert.assertEquals(base.hashCode(), equal.hashCode());

    Assert.assertNotEquals(base, differentMajorVersion);
    Assert.assertNotEquals(base.hashCode(), differentMajorVersion.hashCode());

    Assert.assertNotEquals(base, differentMinorVersion);
    Assert.assertNotEquals(base.hashCode(), differentMinorVersion.hashCode());
  }

  @Test
  public void testComparison() {
    PdsVid base = new PdsVid(5, 5);
    PdsVid equal = new PdsVid(5, 5);
    PdsVid higherMajorVersion = new PdsVid(10, 5);
    PdsVid lowerMajorVersion = new PdsVid(1, 5);
    PdsVid higherMinorVersion = new PdsVid(5, 10);
    PdsVid lowerMinorVersion = new PdsVid(5, 1);
    PdsVid higherMajorLowerMinor = new PdsVid(10, 1);
    PdsVid lowerMajorHigherMinor = new PdsVid(1, 10);

    Assert.assertEquals(0, base.compareTo(equal));
    Assert.assertTrue(base.compareTo(higherMajorVersion) < 0);
    Assert.assertTrue(base.compareTo(lowerMajorVersion) > 0);
    Assert.assertTrue(base.compareTo(higherMinorVersion) < 0);
    Assert.assertTrue(base.compareTo(lowerMinorVersion) > 0);
    Assert.assertTrue(base.compareTo(higherMajorLowerMinor) < 0);
    Assert.assertTrue(base.compareTo(lowerMajorHigherMinor) > 0);
  }
}
