package gov.nasa.pds.api.registry.configuration;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import gov.nasa.pds.api.registry.view.CsvErrorMessageSerializer;
import gov.nasa.pds.api.registry.view.CsvPluralSerializer;
import gov.nasa.pds.api.registry.view.CsvSingularSerializer;
import gov.nasa.pds.api.registry.view.JsonErrorMessageSerializer;
import gov.nasa.pds.api.registry.view.JsonPluralSerializer;
import gov.nasa.pds.api.registry.view.JsonProductSerializer;
import gov.nasa.pds.api.registry.view.JsonSingularSerializer;
import gov.nasa.pds.api.registry.view.Pds4JsonProductSerializer;
import gov.nasa.pds.api.registry.view.Pds4JsonProductsSerializer;
import gov.nasa.pds.api.registry.view.Pds4XmlProductSerializer;
import gov.nasa.pds.api.registry.view.Pds4XmlProductsSerializer;
import gov.nasa.pds.api.registry.view.PdsProductTextHtmlSerializer;
import gov.nasa.pds.api.registry.view.PdsProductsTextHtmlSerializer;
import gov.nasa.pds.api.registry.view.PdsProductXMLSerializer;
import gov.nasa.pds.api.registry.view.PdsProductsXMLSerializer;
import gov.nasa.pds.api.registry.view.XmlErrorMessageSerializer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"gov.nasa.pds.api.registry.configuration ",
    "gov.nasa.pds.api.registry.controller", "gov.nasa.pds.api.registry.search"})
public class WebMVCConfig implements WebMvcConfigurer {
  private static final Logger log = LoggerFactory.getLogger(WebMVCConfig.class);

  @Value("${server.contextPath}")
  private String contextPath;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String contextPath = this.contextPath.endsWith("/") ? this.contextPath : this.contextPath + "/";

    registry.addResourceHandler(contextPath + "webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    registry.addResourceHandler(contextPath + "static/**")
        .addResourceLocations("classpath:/static/");


    registry.addResourceHandler(contextPath + "swagger-ui/pds.*")
        .addResourceLocations("classpath:/swagger-ui/");
    registry.addResourceHandler(contextPath + "swagger-ui/index.htm*")
        .addResourceLocations("classpath:/swagger-ui/");


  }

  @Override
  @SuppressWarnings("deprecation")
  public void configurePathMatch(PathMatchConfigurer configurer) {
    // this is important to avoid that parameters (e.g lidvid) are truncated after .
    configurer.setUseSuffixPatternMatch(false);
  }

  /**
   * Setup a simple strategy: use all the defaults and return JSON by default when not sure.
   */
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    WebMVCConfig.log
        .info("Number of converters available at start " + Integer.toString(converters.size()));

    // csv converters
    converters.add(new CsvErrorMessageSerializer());
    converters.add(new CsvPluralSerializer());
    converters.add(new CsvSingularSerializer());

    // json+kvp converters
    converters.add(new JsonPluralSerializer());
    converters.add(new JsonSingularSerializer());

    // json+pds4 converters
    converters.add(new Pds4JsonProductSerializer());
    converters.add(new Pds4JsonProductsSerializer());

    // xml+pds4 converters
    converters.add(new Pds4XmlProductSerializer());
    converters.add(new Pds4XmlProductsSerializer());

    // default xml converters
    converters.add(new PdsProductXMLSerializer());
    converters.add(new PdsProductsXMLSerializer());
    converters.add(new XmlErrorMessageSerializer());

    // text converters for PdsProduct(s)
    converters.add(new PdsProductTextHtmlSerializer());
    converters.add(new PdsProductsTextHtmlSerializer());

    // Introduce basics that are not PDS related but may overload MediaType
    // Put them after the PDS so that it always has priority except JSON below because it has */*
    // basic converter for /api-docs end-point, new with springdoc 2
    converters.add(new ByteArrayHttpMessageConverter());

    // basic converter for swagger-ui resources
    converters.add(new StringHttpMessageConverter());

    // default json converters
    converters.add(new JsonErrorMessageSerializer());
    converters.add(new JsonProductSerializer()); // this one must be last because it contains */*

    WebMVCConfig.log.info("Number of converters available after adding locals "
        + Integer.toString(converters.size()));
  }
}
