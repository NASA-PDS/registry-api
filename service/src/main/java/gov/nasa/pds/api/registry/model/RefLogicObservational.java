package gov.nasa.pds.api.registry.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

class RefLogicObservational extends RefLogicAny implements ReferencingLogic {
  @Override
  public GroupConstraint constraints() {
    Map<String, List<String>> preset = new HashMap<String, List<String>>();
    preset.put("product_class", Arrays.asList("Product_Observational"));
    return GroupConstraintImpl.buildAll(preset);
  }
}
