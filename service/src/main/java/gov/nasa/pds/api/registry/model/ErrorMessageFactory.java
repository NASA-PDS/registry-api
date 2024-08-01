package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import gov.nasa.pds.model.ErrorMessage;

@Component
// TODO replace that with a default error manangement in spring-boot,
// see https://github.com/NASA-PDS/registry-api/issues/286
public class ErrorMessageFactory {

  @Autowired
  HttpServletRequest request;

  public Object get(Exception err) {
    ErrorMessage em = new ErrorMessage();

    ArrayList<String> requestArray = new ArrayList<String>();

    requestArray.add(this.request.getRequestURL().toString());

    String queryString;
    if ((queryString = this.request.getQueryString()) != null) {
      requestArray.add(queryString);
    }

    em.setRequest(String.join("?", requestArray));

    em.setMessage(err.getMessage() == null || err.getMessage().length() == 0 ? err.toString()
        : err.getMessage());
    return em;
  }

}
