package gov.nasa.pds.api.registry.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import gov.nasa.pds.api.registry.model.exceptions.BadRequestException;
import gov.nasa.pds.api.registry.model.exceptions.UnauthorizedForwardedHostException;
import gov.nasa.pds.api.registry.model.exceptions.UnknownQueryParameterException;
import gov.nasa.pds.api.registry.util.LoggingAspect;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UriUtils;

@Component
public class SecurityValidationFilter implements HandlerInterceptor {
  private static final Logger log = LoggerFactory.getLogger(SecurityValidationFilter.class);

  private static final List<String> ALLOWED_QUERY_PARAMETERS = Arrays.asList("q", "fields", "limit",
      "sort", "search-after", "keywords", "facet-fields", "facet-limit");
  private static final List<String> CACHE_POISONING_HEADER_TARGETS =
      Arrays.asList("x-forwarded-host", "x-host", "x-forwarded-server"); // in lower case to
                                                                         // make
  // test cases insensitive

  @Value("#{'${server.authorizedForwardedHost}'.split(',')}")
  List<String> authorizedForwardedHosts;

  private boolean authorizedServerName(String serverName) {
    return ((this.authorizedForwardedHosts == null) || (this.authorizedForwardedHosts.size() == 0)
        || (authorizedForwardedHosts.contains(serverName)));
  }


  /**
   * Reject a request whose path, once percent-decoded the way the servlet container and Spring MVC
   * decode it for path variables, contains a control character or a Unicode line/paragraph
   * separator. Input that stays encoded after one decoding (e.g. %250d) is not rejected.
   */
  private void rejectControlCharactersInPath(HttpServletRequest request)
      throws BadRequestException {
    String rawPath = request.getRequestURI();
    String decodedPath;
    try {
      decodedPath = UriUtils.decode(rawPath, StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      log.warn("Rejected request with malformed percent-encoding in path {}",
          LoggingAspect.sanitizeForLog(rawPath));
      throw new BadRequestException("Request path is not correctly percent-encoded.");
    }
    if (LoggingAspect.CONTROL_OR_SEPARATOR.matcher(decodedPath).find()) {
      log.warn("Rejected request with control characters in path {}",
          LoggingAspect.sanitizeForLog(rawPath));
      throw new BadRequestException("Request path must not contain control characters.");
    }
  }


  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    // path variables are the decoded request path: refuse control characters, e.g. from %0d%0a
    rejectControlCharactersInPath(request);

    // check authorized parameters
    for (String paramName : request.getParameterMap().keySet()) {
      if (!ALLOWED_QUERY_PARAMETERS.contains(paramName)) {
        throw new UnknownQueryParameterException(
            "Query parameter not enumerated in SecurityValidationFilter.ALLOWED_QUERY_PARAMETERS: "
                + Encode.forHtml(paramName));
      }
    }


    // safe request we don't analyze headers
    if (request.getRequestURI().equals("/health")) {
      return true;
    }

    // check cache poisoning targets
    Enumeration<String> headerNames = request.getHeaderNames();
    if (headerNames != null) {
      String headerName;
      String proxyHostName;
      while (headerNames.hasMoreElements()) {
        headerName = headerNames.nextElement();
        log.debug("investigating header {} for cache poisoning risks", headerName);
        if (CACHE_POISONING_HEADER_TARGETS.contains(headerName.toLowerCase())) {
          proxyHostName = request.getHeader(headerName);
          String loggableProxyHostName = LoggingAspect.sanitizeForLog(proxyHostName);
          log.debug("Risk of cache poisoning on header with value {}", loggableProxyHostName);
          if (!authorizedServerName(proxyHostName)) {
            log.error("Server cannot be proxied from {} but from {}", loggableProxyHostName,
                this.authorizedForwardedHosts);
            throw new UnauthorizedForwardedHostException(
                "Server cannot be proxied from " + proxyHostName);
          } else {
            log.debug("Value is considered safe.");
          }

        }
      }

      // since the forwarded-headers might be consumed by spring-boot at earlier stage we also
      // control the following value
      String serverName = request.getServerName();
      String loggableServerName = LoggingAspect.sanitizeForLog(serverName);
      log.debug("Servername is {}", loggableServerName);
      if (!authorizedServerName(serverName)) {
        log.error("Server cannot be proxied from {} but from {}", loggableServerName,
            this.authorizedForwardedHosts);
        throw new UnauthorizedForwardedHostException("Server cannot be proxied from " + serverName);
      }
    }

    return true;
  }



}
