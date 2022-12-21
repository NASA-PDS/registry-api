package gov.nasa.pds.api.registry.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-07-24T09:48:37.812-07:00[America/Los_Angeles]")
@Configuration
public class SwaggerDocumentationConfig {
	
	

	@Value("${registry.service.version:undefined}")
	private String version;

	@Bean
	public OpenAPI RegistryOpenAPI(){
	      return new OpenAPI()
	              .info(new Info().title("PDS Registry API")
	              .description("PDS Registry API proposed for Searching the registry")
	              .version(this.version)
	              .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")))
	              .externalDocs(new ExternalDocumentation()
	            		  .description("PDS Search API Documentation")
	            		  .url("https://nasa-pds.github.io/pds-api/guides/search.html"));
	  }
	
	/* obsolete
	
    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("gov.nasa.pds.api.registry"))
                    .build()
                .directModelSubstitute(org.joda.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.joda.time.DateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }
    */
    
   

}
