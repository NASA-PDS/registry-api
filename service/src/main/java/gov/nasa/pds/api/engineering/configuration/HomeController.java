package gov.nasa.pds.api.engineering.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home redirection to swagger api documentation 
 */
@Controller
public class HomeController {
	
	@Value("${server.contextPath}")
	private String contextPath;
	
    @RequestMapping(value = "/")
    public String index() {

    	String contextPath = this.contextPath.endsWith("/")?this.contextPath:this.contextPath+"/";

        // Leave this redirect in place even if swagger is turned off since it is used for
        // AWS healthchecks (i.e. GET / -> 302)
        System.out.println(contextPath+"swagger-ui.html");
        return "redirect:"+contextPath+"swagger-ui.html";
    }
}
