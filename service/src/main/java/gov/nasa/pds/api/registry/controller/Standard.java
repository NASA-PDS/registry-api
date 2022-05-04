package gov.nasa.pds.api.registry.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.business.RequestAndResponseContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.opensearch.SearchRequestFactory;

class Standard implements EndpointHandler {

	@Override
	public ResponseEntity<Object> transmute(ControlContext control, UserContext content)
			throws ApplicationTypeException, IOException, LidVidNotFoundException, NothingFoundException, UnknownGroupNameException
	{
        RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext
        		(control, content, new GroupPresetTransmuter().transform(content.getGroup()));
    	context.setResponse(control.getConnection().getRestHighLevelClient(),
    			new SearchRequestFactory(context, control.getConnection())
    				.build(context, control.getConnection().getRegistryIndex()));
		return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
	}

}
