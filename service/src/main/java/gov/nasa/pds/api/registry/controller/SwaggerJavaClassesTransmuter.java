package gov.nasa.pds.api.registry.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import gov.nasa.pds.api.base.ClassesApi;
import gov.nasa.pds.api.registry.model.ReferencingLogicTransmuter;

abstract class SwaggerJavaClassesTransmuter extends SwaggerJavaBaseTransmuter
    implements ClassesApi {
  @Override
  public ResponseEntity<List<String>> classes() {
    return new ResponseEntity<List<String>>(ReferencingLogicTransmuter.getSwaggerNames(),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Object> classList(String propertyClass, @Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Standard(),
        this.uriParametersBuilder.setGroup(propertyClass).setFields(fields).setKeywords(keywords)
            .setLimit(limit).setQuery(q).setSort(sort).setStart(start).build());
  }

  @Override
  public ResponseEntity<Object> classMemberOf(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, false),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).build());
  }

  @Override
  public ResponseEntity<Object> classMemberOfOf(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, true),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).build());
  }

  @Override
  public ResponseEntity<Object> classMemberOfOfVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, true),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).setVersion(versions).build());
  }

  @Override
  public ResponseEntity<Object> classMemberOfVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(false, false),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).setVersion(versions).build());
  }

  @Override
  public ResponseEntity<Object> classMembers(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, false),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).build());
  }

  @Override
  public ResponseEntity<Object> classMembersMembers(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, true),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).build());
  }

  @Override
  public ResponseEntity<Object> classMembersMembersVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, true),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).setVersion(versions).build());
  }

  @Override
  public ResponseEntity<Object> classMembersVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    return this.processs(new Member(true, false),
        this.uriParametersBuilder.setGroup(propertyClass).setIdentifier(identifier)
            .setFields(fields).setLimit(limit).setSort(sort).setStart(start)
            .setVerifyClassAndId(true).setVersion(versions).build());
  }
}
