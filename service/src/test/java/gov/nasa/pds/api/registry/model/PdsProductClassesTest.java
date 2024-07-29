package gov.nasa.pds.api.registry.model;

import gov.nasa.pds.api.registry.model.identifiers.PdsProductClasses;
import org.junit.Assert;
import org.junit.Test;

public class PdsProductClassesTest {
  @Test
  public void testSwaggerConversions() {
    for (PdsProductClasses canonicalClass : PdsProductClasses.values()) {
      String swaggerName = canonicalClass.getSwaggerName();
      PdsProductClasses parsedClass = PdsProductClasses.fromSwaggerName(swaggerName);
      Assert.assertEquals(canonicalClass, parsedClass);
    }
  }
}
