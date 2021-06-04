package gov.nasa.pds.api.engineering.configuration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.api.engineering.serializer.Pds4JsonProductSerializer;
import gov.nasa.pds.api.engineering.serializer.Pds4XmlProductSerializer;
import gov.nasa.pds.api.engineering.serializer.Pds4XmlProductsSerializer;
import gov.nasa.pds.api.engineering.serializer.XmlProductSerializer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "gov.nasa.pds.api.engineering.configuration ",  "gov.nasa.pds.api.engineering.controllers", "gov.nasa.pds.api.engineering.elasticsearch"})
public class WebMVCConfig implements WebMvcConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(WebMVCConfig.class);
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

	       registry.addResourceHandler("swagger-ui.html")
	                .addResourceLocations("classpath:/META-INF/resources/");

	        registry.addResourceHandler("/webjars/**")
	                .addResourceLocations("classpath:/META-INF/resources/webjars/");

	}
	
	
	 @Override
	   public void configurePathMatch(PathMatchConfigurer configurer) {
		 // this is important to avoid that parameters (e.g lidvid) are truncated after .
	       configurer.setUseSuffixPatternMatch(false);
	   }
	
	
  /**
   * Setup a simple strategy: use all the defaults and return XML by default when not sure. 
 */
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
    
    
  }
  
  
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
   
	  WebMVCConfig.log.info("Number of converters available " + Integer.toString(converters.size()));
	  converters.add(new Pds4JsonProductSerializer());
	  converters.add(new Pds4XmlProductSerializer()); // Product class, application/pds4+xml
	  converters.add(new XmlProductSerializer()); // Product class, application/xml
	  converters.add(new Jaxb2RootElementHttpMessageConverter()); // other classes, application/xml
	  
	  
	  
  }
  

  
  
  

  
}
