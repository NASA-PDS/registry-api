package gov.nasa.pds.api.registry.controller;

import java.util.List;

import gov.nasa.pds.api.registry.model.identifiers.PdsProductIdentifier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import gov.nasa.pds.api.base.BundlesApi;
import gov.nasa.pds.api.base.CollectionsApi;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;

abstract class SwaggerJavaDeprecatedTransmuter extends SwaggerJavaProductsTransmuter
    implements BundlesApi, CollectionsApi /* , ProductsApi */ {
  @Override
  public ResponseEntity<Object> bundleList(@Valid List<String> fields, @Valid List<String> keywords,
      @Min(0) @Valid Integer limit, @Valid String q, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return super.classList("bundles", fields, keywords, limit, q, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvid(String identifier, @Valid List<String> fields) {
    return this.processs(new Standard(),
        this.uriParametersBuilder.setGroup("bundles")
            .setIdentifier(PdsProductIdentifier.fromString(identifier)).setFields(fields)
            .setVerifyClassAndId(true).build());
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Valid List<String> searchAfter) {
    // TODO: Investigate why start/searchAfter is just disregarded for this endpoint
    return this.processs(new Standard(),
        this.uriParametersBuilder.setGroup("bundles")
            .setIdentifier(PdsProductIdentifier.fromString(identifier)).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.ALL).build());
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollections(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return this.classMembers("bundles", identifier, fields, limit, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollectionsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return this.classMembersVers("bundles", identifier, "all", fields, limit, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollectionsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return this.classMembersVers("bundles", identifier, "latest", fields, limit, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidLatest(String identifier, @Valid List<String> fields) {
    return this.processs(new Standard(),
        this.uriParametersBuilder.setGroup("bundles")
            .setIdentifier(PdsProductIdentifier.fromString(identifier)).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.LATEST).build());
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidProducts(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Valid List<String> searchAfter) {
    return this.classMembersMembers("bundles", identifier, fields, limit, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> collectionList(@Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Valid List<String> searchAfter) {
    return super.classList("collections", fields, keywords, limit, q, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvid(String identifier, @Valid List<String> fields) {
    return this.processs(new Standard(),
        this.uriParametersBuilder.setGroup("collections")
            .setIdentifier(PdsProductIdentifier.fromString(identifier)).setFields(fields)
            .setVerifyClassAndId(true).build());
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Valid List<String> searchAfter) {
    // TODO: Investigate why start/searchAfter is disregarded in this case
    return this.processs(new Standard(),
        this.uriParametersBuilder.setGroup("collections")
            .setIdentifier(PdsProductIdentifier.fromString(identifier)).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.ALL).build());
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidBundles(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return this.classMemberOf("collections", identifier, fields, limit, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidLatest(String identifier,
      @Valid List<String> fields) {
    return this.processs(new Standard(),
        this.uriParametersBuilder.setGroup("collections")
            .setIdentifier(PdsProductIdentifier.fromString(identifier)).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.LATEST).build());
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProducts(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return this.classMembers("collections", identifier, fields, limit, sort, searchAfter);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProductsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return this.classMembersVers("collections", identifier, "all", fields, limit, sort,
        searchAfter);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProductsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Valid List<String> searchAfter) {
    return this.classMembersVers("collections", identifier, "latest", fields, limit, sort,
        searchAfter);
  }


  /*
   * @Override public ResponseEntity<Object> productsLidividBundlesAll(String identifier,
   * 
   * @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
   * 
   * @Valid List<String> searchAfter) { return this.classMemberOfOfVers("any", identifier, "all",
   * fields, limit, sort, searchAfter); }
   * 
   * @Override public ResponseEntity<Object> productsLidvidBundles(String identifier, @Valid
   * List<String> fields,
   * 
   * @Min(0) @Valid Integer limit, @Valid List<String> sort, @Valid List<String> searchAfter) {
   * return this.classMemberOfOf("any", identifier, fields, limit, sort, searchAfter); }
   * 
   * @Override public ResponseEntity<Object> productsLidvidBundlesLatest(String identifier,
   * 
   * @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
   * 
   * @Valid List<String> searchAfter) { return this.classMemberOfOfVers("any", identifier, "latest",
   * fields, limit, sort, searchAfter); }
   * 
   * @Override public ResponseEntity<Object> productsLidvidCollections(String identifier,
   * 
   * @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
   * 
   * @Valid List<String> searchAfter) { return this.classMemberOf("any", identifier, fields, limit,
   * sort, searchAfter); }
   * 
   * @Override public ResponseEntity<Object> productsLidvidCollectionsAll(String identifier,
   * 
   * @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
   * 
   * @Valid List<String> searchAfter) { return this.classMemberOfVers("any", identifier, "all",
   * fields, limit, sort, searchAfter); }
   * 
   * @Override public ResponseEntity<Object> productsLidvidCollectionsLatest(String identifier,
   * 
   * @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
   * 
   * @Valid List<String> searchAfter) { return this.classMemberOfVers("any", identifier, "latest",
   * fields, limit, sort, searchAfter); }
   */
}
