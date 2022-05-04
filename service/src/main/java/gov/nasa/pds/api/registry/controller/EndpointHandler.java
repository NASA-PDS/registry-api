package gov.nasa.pds.api.registry.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;

public interface EndpointHandler
{
	public ResponseEntity<Object> transmute (ControlContext control, UserContext content)
			throws ApplicationTypeException,IOException,LidVidNotFoundException,NothingFoundException,UnknownGroupNameException;
}
