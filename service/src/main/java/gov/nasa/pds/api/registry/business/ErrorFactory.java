package gov.nasa.pds.api.registry.business;

import javax.servlet.http.HttpServletRequest;

import gov.nasa.pds.model.ErrorMessage;

public class ErrorFactory
{
	public static Object build(Exception err, HttpServletRequest request)
	{
		ErrorMessage em = new ErrorMessage();
		em.setRequest(request.getRequestURI());
		em.setMessage(err.getMessage() == null || err.getMessage().length() == 0 ? err.toString() : err.getMessage());
		return em;
	}
}
