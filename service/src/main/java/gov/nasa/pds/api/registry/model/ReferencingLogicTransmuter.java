package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;

public enum ReferencingLogicTransmuter {
  Any(new RefLogicAny(), "", "any"), Bundle(new RefLogicBundle(), "Product_Bundle",
      "bundles"), Collection(new RefLogicCollection(), "Product_Collection",
          "collections"), Document(new RefLogicDocument(), "Product_Document",
              "documents"), Observational(new RefLogicObservational(), "Product_Observational",
                  "observationals"), NonAggregateProduct(new RefLogicNonAggregateProduct(), "", "non-aggregate-products");

  final private ReferencingLogic refLogic;
  final private String pds_name;
  final private String swagger_name;

  private ReferencingLogicTransmuter(ReferencingLogic impl, String pds_name, String swagger_name) {
    this.refLogic = impl;
    this.pds_name = pds_name;
    this.swagger_name = swagger_name;
  }

  private static ReferencingLogicTransmuter get(String name, boolean usingPDSName)
      throws NothingFoundException {
    ReferencingLogicTransmuter resultant = null;

    if (name.isEmpty() || name.equals("any")) return ReferencingLogicTransmuter.Any;

    for (ReferencingLogicTransmuter pc : ReferencingLogicTransmuter.values()) {
      if (name.equals(usingPDSName ? pc.pds_name : pc.swagger_name)) {
        resultant = pc;
        break;
      }
    }

      //      Previously, this would throw a NothingFoundException, but this prevents non-enumerated non-aggregate products
      //      like Product_Ancillary from being correctly resolved.  It's necessary to either enumerate all non-aggregate
      //      product names (which I believe may not be possible), or accept that anything not enumerated will be considered
      //      a non-aggregate product --- edunn 2023-11-30
      return Objects.requireNonNullElse(resultant, ReferencingLogicTransmuter.NonAggregateProduct);
  }

  public static ReferencingLogicTransmuter getByProductClass(String name)
      throws UnknownGroupNameException {
    try {
      return ReferencingLogicTransmuter.get(name, true);
    } catch (NothingFoundException nfe) {
      return ReferencingLogicTransmuter.Any;
      // Set<String> known = new HashSet<String>();
      // for (ReferencingLogicTransmuter pc : ReferencingLogicTransmuter.values())
      // known.add(pc.pds_name);
      // throw new UnknownGroupNameException(name, known);
    }
  }

  public static ReferencingLogicTransmuter getBySwaggerGroup(String name)
      throws UnknownGroupNameException {
    try {
      return ReferencingLogicTransmuter.get(name, false);
    } catch (NothingFoundException nfe) {
      Set<String> known = new HashSet<String>();
      for (ReferencingLogicTransmuter pc : ReferencingLogicTransmuter.values())
        known.add(pc.swagger_name);
      throw new UnknownGroupNameException(name, known);
    }

  }

  public static List<String> getSwaggerNames() {
    List<String> names = new ArrayList<String>();
//    It is arguably not desirable to expose the concept of "non-aggregate products" to the user as a category
//    See: https://github.com/NASA-PDS/registry-api/issues/326
    Set<String> excludedNames = Set.of(ReferencingLogicTransmuter.NonAggregateProduct.swagger_name);
    for (ReferencingLogicTransmuter rlt : ReferencingLogicTransmuter.values())
      if (!excludedNames.contains(rlt.swagger_name)){
        names.add(rlt.swagger_name);
      }

    return names;
  }

  public ReferencingLogic impl() {
    return this.refLogic;
  }
}
