package gov.nasa.pds.api.registry;

import java.io.IOException;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.RequestAndResponseContext;

@Immutable
public interface ReferencingLogic {
  /**
   * Map the set of PDS constraints that define just PDS items that make up this product type/group.
   */
  public GroupConstraint constraints();

  /**
   * Find descendant members of aggregate products, or throw exception if undefined
   */
  public RequestAndResponseContext member(ControlContext context, UserContext input,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      MembershipException, UnknownGroupNameException;

  /**
   * Find ancestor members non-bundle products, or throw exception if undefined
   */
  public RequestAndResponseContext memberOf(ControlContext context, UserContext input,
      boolean twoSteps) throws ApplicationTypeException, IOException, LidVidNotFoundException,
      MembershipException, UnknownGroupNameException;
}
