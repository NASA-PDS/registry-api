package gov.nasa.pds.api.registry.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import gov.nasa.pds.api.registry.model.exceptions.UnauthorizedForwardedHostException;
import gov.nasa.pds.api.registry.model.exceptions.UnknownQueryParameterException;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityValidationFilter implements HandlerInterceptor {
  private static final Logger log = LoggerFactory.getLogger(SecurityValidationFilter.class);

  private static final List<String> ALLOWED_QUERY_PARAMETERS =
      Arrays.asList("q", "fields", "limit", "sort", "search-after", "keywords", "facet-fields", "facet-limit");

  @Value("#{'${server.authorizedForwardedHost:}'.split(',')}")
  List<String> authorizedForwardedHosts;

  private boolean authorizedServerName(String serverName) {
    return ((this.authorizedForwardedHosts == null) || (this.authorizedForwardedHosts.size() == 0)
        || (authorizedForwardedHosts.contains(serverName)));
  }


  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    for (String paramName : request.getParameterMap().keySet()) {
      if (!ALLOWED_QUERY_PARAMETERS.contains(paramName)) {
        throw new UnknownQueryParameterException("Query parameter not enumerated in SecurityValidationFilter.ALLOWED_QUERY_PARAMETERS: " + paramName);
      }
    }


    String serverName = request.getServerName();

    if (!authorizedServerName(serverName)) {
      log.error("Server cannot be proxied from " + serverName + " but from "
          + this.authorizedForwardedHosts);
      throw new UnauthorizedForwardedHostException("Server cannot be proxied from " + serverName);
    }


    return true;
  }



}
