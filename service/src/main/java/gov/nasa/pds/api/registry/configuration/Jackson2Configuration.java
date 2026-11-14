package gov.nasa.pds.api.registry.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Spring Boot 4 auto-configures a Jackson 3 mapper, not a Jackson 2 one. The application code is
 * still on Jackson 2 (com.fasterxml.jackson), so this configuration provides the Jackson 2
 * {@link ObjectMapper} which is injected in the controllers.
 */
@Configuration
public class Jackson2Configuration {

  /**
   * Same defaults as the mapper auto-configured by Spring Boot 3: unknown properties are ignored
   * (the OpenSearch documents are converted to classes which only declare some of their
   * properties) and dates are not written as timestamps.
   */
  @Bean
  public ObjectMapper objectMapper() {
    return JsonMapper.builder().findAndAddModules()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();
  }
}
