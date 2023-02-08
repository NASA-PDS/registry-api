package gov.nasa.pds.api.registry.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;

abstract class SwaggerJavaProductsTransmuter extends SwaggerJavaClassesTransmuter
    implements ProductsApi {
  @Override
  public ResponseEntity<Object> productList(@Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    SwaggerJavaProductsTransmuter.log.info("request request merde" + keywords.get(0));
    return super.classList("any", fields, keywords, limit, q, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMemberOf(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, false), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start));
  }

  @Override
  public ResponseEntity<Object> productMemberOfOf(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, true), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start));
  }

  @Override
  public ResponseEntity<Object> productMemberOfOfVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, true), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start).setVersion(versions));
  }

  @Override
  public ResponseEntity<Object> productMemberOfVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, false), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start).setVersion(versions));
  }

  @Override
  public ResponseEntity<Object> productMembers(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, false), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start));
  }

  @Override
  public ResponseEntity<Object> productMembersMembers(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, true), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start));
  }

  @Override
  public ResponseEntity<Object> productMembersMembersVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, true), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start).setVersion(versions));
  }

  @Override
  public ResponseEntity<Object> productMembersVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, false), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setLimit(limit).setSort(sort).setStart(start).setVersion(versions));
  }

  @Override
  public ResponseEntity<Object> selectByLidvid(String identifier, @Valid List<String> fields) {
    return this.processs(new Standard(),
        new URIParameters().setIdentifier(identifier).setFields(fields));
  }

  @Override
  public ResponseEntity<Object> selectByLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Standard(),
        new URIParameters().setIdentifier(identifier).setFields(fields).setLimit(limit)
            .setSort(sort).setStart(start).setVersion(ProductVersionSelector.ALL));
  }

  @Override
  public ResponseEntity<Object> selectByLidvidLatest(String identifier,
      @Valid List<String> fields) {
    return this.processs(new Standard(), new URIParameters().setIdentifier(identifier)
        .setFields(fields).setVersion(ProductVersionSelector.LATEST));
  }
}
