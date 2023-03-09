package gov.nasa.pds.api.registry.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.ReferencingLogicTransmuter;
import gov.nasa.pds.api.registry.model.RequestAndResponseContext;
import gov.nasa.pds.api.registry.search.QuickSearch;

class Member implements EndpointHandler {
	final private boolean offspring, twoSteps;

	public Member(boolean offspring, boolean twoSteps) {
		this.offspring = offspring;
		this.twoSteps = twoSteps;
	}

	@Override
	public ResponseEntity<Object> transmute(ControlContext control, UserContext content)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, MembershipException,
			NothingFoundException, UnknownGroupNameException {
		ReferencingLogic transmuter;

		if (0 < content.getGroup().length())
			transmuter = ReferencingLogicTransmuter.getBySwaggerGroup(content.getGroup()).impl();
		else
			transmuter = ReferencingLogicTransmuter
					.getByProductClass(
							QuickSearch.getValue(control.getConnection(), false, content.getLidVid(), "product_class"))
					.impl();

		RequestAndResponseContext context = this.offspring ? transmuter.member(control, content, this.twoSteps)
				: transmuter.memberOf(control, content, this.twoSteps);
		return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
	}

}
