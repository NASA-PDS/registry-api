package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gov.nasa.pds.api.base.PropertiesApi;
import gov.nasa.pds.model.ProductPropertiesList200ResponseInner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.client.indices.GetIndexResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import gov.nasa.pds.api.base.ClassesApi;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;

abstract class SwaggerJavaProductsTransmuter extends SwaggerJavaClassesTransmuter
    implements ControlContext, ProductsApi, ClassesApi, PropertiesApi {

  public Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @Override
  public ResponseEntity<Object> productList(@Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return super.classList("any", fields, keywords, limit, q, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMemberOf(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, false),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).build());
  }

  @Override
  public ResponseEntity<Object> productMemberOfOf(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, true),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).build());
  }

  @Override
  public ResponseEntity<Object> productMemberOfOfVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, true),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).setVersion(versions).build());
  }

  @Override
  public ResponseEntity<Object> productMemberOfVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, false),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).setVersion(versions).build());
  }

  @Override
  public ResponseEntity<Object> productMembers(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, false),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).build());
  }

  @Override
  public ResponseEntity<Object> productMembersMembers(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, true), this.uriParametersBuilder.setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start).build());
  }

  @Override
  public ResponseEntity<Object> productMembersMembersVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, true),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).setVersion(versions).build());
  }

  @Override
  public ResponseEntity<Object> productMembersVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, false),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).setVersion(versions).build());
  }

  @Override
  public ResponseEntity<Object> selectByLidvid(String identifier, @Valid List<String> fields) {
    return this.processs(new Standard(),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).build());
  }

  @Override
  public ResponseEntity<Object> selectByLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Standard(),
        this.uriParametersBuilder.setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).setVersion(ProductVersionSelector.ALL).build());
  }

  @Override
  public ResponseEntity<Object> selectByLidvidLatest(String identifier,
      @Valid List<String> fields) {
    return this.processs(new Standard(), this.uriParametersBuilder.setIdentifier(identifier)
        .setFields(fields).setVersion(ProductVersionSelector.LATEST).build());
  }

  @Override
  public ResponseEntity<List<ProductPropertiesList200ResponseInner>> productPropertiesList() {

    try {
      String registryIndexName = this.getConnection().getRegistryIndex();

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

      GetIndexRequest req = new GetIndexRequest(registryIndexName);
      RestHighLevelClient client = this.getConnection().getRestHighLevelClient();
      GetIndexResponse response = client.indices().get(req, RequestOptions.DEFAULT);

      JsonNode content =
          mapper
              .valueToTree(response.getMappings().get(registryIndexName).getSourceAsMap())
              .get("properties");

      Map<String, String> displayTypesByDbType =
          Map.of(
              "keyword", "string",
              "text", "string",
              "date", "timestamp",
              "integer", "integer",
              "long", "integer",
              "float", "float",
              "double", "float");

      List<ProductPropertiesList200ResponseInner> results = new ArrayList<>();
      content
          .fieldNames()
          .forEachRemaining(
              (String propertyName) -> {
                ProductPropertiesList200ResponseInner propertyElement =
                    new ProductPropertiesList200ResponseInner();

                propertyElement.setProperty(propertyName);

                String rawType = content.get(propertyName).get("type").asText();
                String displayType = displayTypesByDbType.getOrDefault(rawType, "unsupported");
                ProductPropertiesList200ResponseInner.TypeEnum enumType =
                    ProductPropertiesList200ResponseInner.TypeEnum.fromValue(displayType);
                propertyElement.setType(enumType);

                results.add(propertyElement);
              });

      return new ResponseEntity<>(results, HttpStatus.OK);
    } catch (IOException err) {
      log.error("SwaggerJavaProductsTransmuter.productPropertiesList() failed", err);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
