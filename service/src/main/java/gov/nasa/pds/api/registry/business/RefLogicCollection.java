package gov.nasa.pds.api.registry.business;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.api.registry.ReferencingLogic;
import gov.nasa.pds.api.registry.UserContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;

public class RefLogicCollection implements ReferencingLogic
{
	@Override
    public Map<String,String> constraints()
    {
    	Map<String,String> preset = new HashMap<String,String>();
    	preset.put("product_class", "Product_Collection");
    	return preset;
    }

    @Override
	public RequestAndResponseContext find(UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestAndResponseContext given(UserContext input)
			throws ApplicationTypeException, IOException, LidVidNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}
