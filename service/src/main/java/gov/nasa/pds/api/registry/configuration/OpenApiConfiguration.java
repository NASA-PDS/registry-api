package gov.nasa.pds.api.registry.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;



@Configuration
public class OpenApiConfiguration {

  @Value("${registry.service.version:undefined}")
  private String version;


  @Bean
  public OpenAPI customOpenAPI() {
    OpenAPI customOpenAPI = new OpenAPI()
        .info(new Info().title("PDS Registry Search API")
            .description(
                "RestFul web API provided to search all classes of products in the PDS registries.")
            .version(this.version)
            .contact(new Contact().name("Contact PDS Engineering Node Support")
                .email("pds_operator@jpl.nasa.gov"))
            .license(new License().name("Apache 2.0")
                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
        .externalDocs(new ExternalDocumentation().description("User's Guide")
            .url("https://nasa-pds.github.io/pds-api/guides/search.html"));


    return customOpenAPI;
  }



  @Bean
  ForwardedHeaderFilter forwardedHeaderFilter() {
    return new ForwardedHeaderFilter();
  }



}


