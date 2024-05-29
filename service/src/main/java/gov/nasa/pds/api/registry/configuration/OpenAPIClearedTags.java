package gov.nasa.pds.api.registry.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;

@Component
// this component is needed until https://github.com/OpenAPITools/openapi-generator/pull/13434 is
// fixed/merged
public class OpenAPIClearedTags implements OpenApiCustomizer {
  protected static final Logger log = LoggerFactory.getLogger(OpenAPIClearedTags.class);

  private static final String FILTERED_TAGS =
      "collections|bundles|products|classes|healthcheck|properties|docs";

  @Override
  public void customise(OpenAPI openApi) {
    OpenAPIClearedTags.log.info("Removing products tag from operations");



    List<Tag> tags = openApi.getTags();
    ArrayList<Tag> filteredTags = new ArrayList<Tag>();


    for (Tag tag : tags) {
      if (!tag.getName().matches(OpenAPIClearedTags.FILTERED_TAGS)) {
        filteredTags.add(tag);
      }
    }
    openApi.setTags(filteredTags);


    Paths paths = openApi.getPaths();
    for (Map.Entry<String, PathItem> path : paths.entrySet()) {
      PathItem pathItem = path.getValue();
      ArrayList<String> filteredTagStrings = new ArrayList<String>();
      for (Operation operation : pathItem.readOperations()) {
        for (String tag : operation.getTags()) {
          if (!tag.matches(OpenAPIClearedTags.FILTERED_TAGS)) {
            filteredTagStrings.add(tag);
          }
        }
        operation.setTags(filteredTagStrings);
      }

    }

  }



}
