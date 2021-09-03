package gov.nasa.pds.api.engineering.configuration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import gov.nasa.pds.api.engineering.serializer.Pds4JsonProductSerializer;
import gov.nasa.pds.api.engineering.serializer.Pds4XmlProductSerializer;
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
	@Override
	@SuppressWarnings("deprecation")
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);

		/*
    	// For path content negotiation , .json. .xml ...
    	// that does not work on its own, I guess I also need to update the swagger definition
    	configurer.favorParameter(false).
    	ignoreAcceptHeader(false).
    	defaultContentType(MediaType.APPLICATION_JSON).
    	mediaType("xml", MediaType.APPLICATION_XML).
    	mediaType("json", MediaType.APPLICATION_JSON);
		 */ 
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		WebMVCConfig.log.info("Number of converters available " + Integer.toString(converters.size()));
		converters.add(new Pds4JsonProductSerializer());
		converters.add(new Pds4XmlProductSerializer()); // Product class, application/pds4+xml

		/* JILT: never want to see html/xml again!
		 * To find the other places that need to also be changed if this is ever desired again,
		 * search the code for JILT. Should be unique enough to not cross over. 
		converters.add(new XmlProductSerializer()); // Product class, application/xml
	  	converters.add(new Jaxb2RootElementHttpMessageConverter()); // other classes, application/xml
		 */
	}  
}
