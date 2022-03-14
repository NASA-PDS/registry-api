package gov.nasa.pds.api.registry.configuration;

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

import gov.nasa.pds.api.registry.serializer.CsvErrorMessageSerializer;
import gov.nasa.pds.api.registry.serializer.CsvPluralSerializer;
import gov.nasa.pds.api.registry.serializer.CsvSingularSerializer;
import gov.nasa.pds.api.registry.serializer.HtmlErrorMessageSerializer;
import gov.nasa.pds.api.registry.serializer.JsonErrorMessageSerializer;
import gov.nasa.pds.api.registry.serializer.JsonPluralSerializer;
import gov.nasa.pds.api.registry.serializer.JsonProductSerializer;
import gov.nasa.pds.api.registry.serializer.JsonSingularSerializer;
import gov.nasa.pds.api.registry.serializer.Pds4JsonProductSerializer;
import gov.nasa.pds.api.registry.serializer.Pds4JsonProductsSerializer;
import gov.nasa.pds.api.registry.serializer.Pds4XmlProductSerializer;
import gov.nasa.pds.api.registry.serializer.Pds4XmlProductsSerializer;
import gov.nasa.pds.api.registry.serializer.PdsProductTextHtmlSerializer;
import gov.nasa.pds.api.registry.serializer.PdsProductXMLSerializer;
import gov.nasa.pds.api.registry.serializer.PdsProductsTextHtmlSerializer;
import gov.nasa.pds.api.registry.serializer.PdsProductsXMLSerializer;
import gov.nasa.pds.api.registry.serializer.XmlErrorMessageSerializer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "gov.nasa.pds.api.registry.configuration ",  "gov.nasa.pds.api.registry.controllers", "gov.nasa.pds.api.registry.opensearch"})
public class WebMVCConfig implements WebMvcConfigurer
{   
	private static final Logger log = LoggerFactory.getLogger(WebMVCConfig.class);
 
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		registry.addResourceHandler("swagger-ui.html")
		.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
		.addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

	@Override
	@SuppressWarnings("deprecation")
	public void configurePathMatch(PathMatchConfigurer configurer)
	{
		// this is important to avoid that parameters (e.g lidvid) are truncated after .
		configurer.setUseSuffixPatternMatch(false);
	}

	/**
	 * Setup a simple strategy: use all the defaults and return JSON by default when not sure. 
	 */
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
	{
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
	{
		WebMVCConfig.log.info("Number of converters available " + Integer.toString(converters.size()));
		converters.add(new CsvErrorMessageSerializer());
		converters.add(new CsvPluralSerializer());
		converters.add(new CsvSingularSerializer());
		converters.add(new HtmlErrorMessageSerializer());
		converters.add(new JsonErrorMessageSerializer());
		converters.add(new JsonPluralSerializer());
		converters.add(new JsonSingularSerializer());
		converters.add(new JsonProductSerializer());
		converters.add(new Pds4JsonProductSerializer());
		converters.add(new Pds4JsonProductsSerializer());
		converters.add(new Pds4XmlProductSerializer());
		converters.add(new Pds4XmlProductsSerializer());
		converters.add(new PdsProductTextHtmlSerializer());
		converters.add(new PdsProductXMLSerializer());
		converters.add(new PdsProductsTextHtmlSerializer());
		converters.add(new PdsProductsXMLSerializer());
		converters.add(new XmlErrorMessageSerializer());
	}
}
