package gov.nasa.pds.api.registry.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.business.ReferencingLogicTransmuter;
import gov.nasa.pds.api.registry.business.RequestAndResponseContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;

class IdReferencingGroup implements EndpointHandler
{
	@Override
	public ResponseEntity<Object> transmute(ControlContext control, UserContext content)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, NothingFoundException,
			UnknownGroupNameException
	{
		RequestAndResponseContext context = ReferencingLogicTransmuter.getBySwaggerGroup(content.getGroup()).impl()
				.given(control, content);
		return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
	}
}
