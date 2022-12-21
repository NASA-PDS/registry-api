package gov.nasa.pds.api.registry.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import io.swagger.annotations.Tag;

/**
 * Home redirection to swagger api documentation 
 */
//@Tag(name="API_documentation", description="API Documentation")
@Controller
public class ApiDocumentation {
	
	@Value("${server.contextPath}")
	private String contextPath;
	
    @RequestMapping(method = RequestMethod.GET, produces = { "text/html" }, value = "/")
    public String index() {

    	String contextPath = this.contextPath.endsWith("/")?this.contextPath:this.contextPath+"/";

        System.out.println(contextPath+"swagger-ui/index.html");
        return "redirect:"+contextPath+"swagger-ui/index.html";
    }
}
