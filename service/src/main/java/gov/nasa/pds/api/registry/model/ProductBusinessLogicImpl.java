package gov.nasa.pds.api.registry.model;

import java.net.MalformedURLException;
import java.net.URL;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequestScope
public abstract class ProductBusinessLogicImpl implements ProductBusinessLogic {
  private static final Logger log = LoggerFactory.getLogger(ProductBusinessLogicImpl.class);

  protected URL baseURL;


  public ProductBusinessLogicImpl() {
    try {

      HttpServletRequest request =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

      String proxyContextPath = request.getContextPath();
      ProductBusinessLogicImpl.log.debug("contextPath is: '" + proxyContextPath + "'");

      if (ProductBusinessLogicImpl.proxyRunsOnDefaultPort(request)) {
        this.baseURL = new URL(request.getScheme(), request.getServerName(), proxyContextPath);
      } else {
        this.baseURL = new URL(request.getScheme(), request.getServerName(),
            request.getServerPort(), proxyContextPath);
      }

      log.debug("baseUrl is " + this.baseURL.toString());


    } catch (MalformedURLException e) {
      log.error("Server URL was not retrieved");

    }
  }



  private static boolean proxyRunsOnDefaultPort(HttpServletRequest request) {
    return (("https".equals(request.getScheme()) && (request.getServerPort() == 443))
        || ("http".equals(request.getScheme()) && (request.getServerPort() == 80)));
  }



}
