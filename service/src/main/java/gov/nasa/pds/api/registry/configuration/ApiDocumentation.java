package gov.nasa.pds.api.registry.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Home redirection to swagger api documentation
 */
@Tag(name = "Registry Search API Documentation")
@Controller
public class ApiDocumentation {

  @Value("${server.contextPath}")
  private String contextPath;

  @RequestMapping(method = RequestMethod.GET, produces = {"text/html"}, value = "/")
  public RedirectView index() {

    String contextPath = this.contextPath.endsWith("/") ? this.contextPath : this.contextPath + "/";

    System.out.println(contextPath + "swagger-ui/index.html");
    // A RedirectView is rendered directly: no ViewResolver is needed (the "redirect:" view name
    // used to be resolved by the Thymeleaf view resolver, removed in the Spring Boot 4 migration).
    // contextRelative=true is the behavior of the "redirect:" prefix.
    return new RedirectView(contextPath + "swagger-ui/index.html", true);
  }
}
