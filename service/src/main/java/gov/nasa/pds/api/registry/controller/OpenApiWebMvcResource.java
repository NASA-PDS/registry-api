package gov.nasa.pds.api.registry.controller;


import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.AbstractRequestService;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.OperationService;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocProviders;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.RouterOperationCustomizer;
import org.springdoc.core.filters.OpenApiMethodFilter;
import org.springdoc.core.providers.SpringWebProvider;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springdoc.core.Constants.API_DOCS_URL;
import static org.springdoc.core.Constants.APPLICATION_OPENAPI_YAML;
import static org.springdoc.core.Constants.DEFAULT_API_DOCS_URL_YAML;

import io.swagger.v3.oas.models.OpenAPI;

import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
public class OpenApiWebMvcResource extends  org.springdoc.webmvc.api.OpenApiWebMvcResource  {
	
	private static final Logger log = LoggerFactory.getLogger(OpenApiWebMvcResource.class);  
 	

	@Autowired
	public OpenApiWebMvcResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder,
			GenericResponseService responseBuilder, OperationService operationParser,
			Optional<List<OperationCustomizer>> operationCustomizers,
			Optional<List<OpenApiCustomiser>> openApiCustomisers,
			Optional<List<RouterOperationCustomizer>> routerOperationCustomizers,
			Optional<List<OpenApiMethodFilter>> methodFilters,
			SpringDocConfigProperties springDocConfigProperties,
			SpringDocProviders springDocProviders) {
		super(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, operationCustomizers, openApiCustomisers, routerOperationCustomizers, methodFilters, springDocConfigProperties, springDocProviders);
	}
	
	/**
	 * Openapi json string.
	 *
	 * @param request the request
	 * @param apiDocsUrl the api docs url
	 * @param locale the locale
	 * @return the string
	 * @throws JsonProcessingException the json processing exception
	 */
	@Operation(hidden = true)
	@GetMapping(value = API_DOCS_URL, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public String openapiJson(HttpServletRequest request,
			String apiDocsUrl, Locale locale)
			throws JsonProcessingException {
		
		this.log.debug("request");
		this.log.debug((request == null)?"null":"not null " + request.getRequestURL().toString());
		this.log.debug("locale");
		this.log.debug((locale == null)?"null":"not null " + locale.getDisplayName());
		this.log.debug("apiDocsUrl");
		this.log.debug((apiDocsUrl == null)?"null":"not null " + apiDocsUrl);
		
		if (apiDocsUrl == null) {
			apiDocsUrl = "/v3/api-docs";
		}
		calculateServerUrl(request, apiDocsUrl, locale);
		
		OpenAPI openAPI = this.getOpenApi(locale);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		String openApiString = objectMapper.writeValueAsString(openAPI);
		this.log.debug("Return OpenAPI");
		this.log.debug(openApiString);
		
		return openApiString;

	}


}

