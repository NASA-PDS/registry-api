package gov.nasa.pds.api.registry.model;

import javax.servlet.http.HttpServletRequest;

import gov.nasa.pds.model.ErrorMessage;

public class ErrorFactory {
	public static Object build(Exception err, HttpServletRequest request) {
		ErrorMessage em = new ErrorMessage();
		em.setRequest(request.getRequestURI());
		em.setMessage(err.getMessage() == null || err.getMessage().length() == 0 ? err.toString() : err.getMessage());
		return em;
	}

	public static Object build(HttpServletRequest request) {
		ErrorMessage em = new ErrorMessage();
		em.setRequest(request.getRequestURI());
		em.setMessage("Something went wrong on the server, please report the error to pds-operator@jpl.nasa.gov");
		return em;
	}
}
