package gov.nasa.pds.api.registry.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import gov.nasa.pds.api.base.BundlesApi;
import gov.nasa.pds.api.base.CollectionsApi;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;

abstract class SwaggerJavaDeprecatedTransmuter extends SwaggerJavaProductsTransmuter
    implements BundlesApi, CollectionsApi, ProductsApi {
  @Override
  public ResponseEntity<Object> bundleList(@Valid List<String> fields, @Valid List<String> keywords,
      @Min(0) @Valid Integer limit, @Valid String q, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return super.classList("bundles", fields, keywords, limit, q, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvid(String identifier, @Valid List<String> fields,
      @Valid String node) {
    return this.processs(new Standard(), new URIParameters().setGroup("bundles")
        .setIdentifier(identifier).setFields(fields).setVerifyClassAndId(true)
        .setNode(node));
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
      @Valid String node) {
    return this.processs(new Standard(),
        new URIParameters().setGroup("bundles").setIdentifier(identifier).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.ALL)
            .setNode(node));
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollections(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMembers("bundles", identifier, fields, limit, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollectionsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMembersVers("bundles", identifier, "all", fields, limit, sort, start,
      node);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollectionsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMembersVers("bundles", identifier, "latest", fields, limit, sort, start,
      node);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidLatest(String identifier, @Valid List<String> fields,
      @Valid String node) {
    return this.processs(new Standard(),
        new URIParameters().setGroup("bundles").setIdentifier(identifier).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.LATEST)
            .setNode(node));
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidProducts(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
      @Valid String node) {
    return this.classMembersMembers("bundles", identifier, fields, limit, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> collectionList(@Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Min(0) @Valid Integer start, @Valid String node) {
    return super.classList("collections", fields, keywords, limit, q, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvid(String identifier, @Valid List<String> fields,
        @Valid String node) {
    return this.processs(new Standard(), new URIParameters().setGroup("collections")
        .setIdentifier(identifier).setFields(fields).setVerifyClassAndId(true)
        .setNode(node));
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
      @Valid String node) {
    return this.processs(new Standard(),
        new URIParameters().setGroup("collections").setIdentifier(identifier).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.ALL)
            .setNode(node));
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidBundles(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMemberOf("collections", identifier, fields, limit, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidLatest(String identifier,
      @Valid List<String> fields, @Valid String node) {
    return this.processs(new Standard(),
        new URIParameters().setGroup("collections").setIdentifier(identifier).setFields(fields)
            .setVerifyClassAndId(true).setVersion(ProductVersionSelector.LATEST)
            .setNode(node));
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProducts(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMembers("collections", identifier, fields, limit, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProductsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMembersVers("collections", identifier, "all", fields, limit, sort, start,
      node);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProductsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMembersVers("collections", identifier, "latest", fields, limit, sort, 
      start, node);
  }

  @Override
  public ResponseEntity<Object> productsLidividBundlesAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMemberOfOfVers("any", identifier, "all", fields, limit, sort, start,
      node);
  }

  @Override
  public ResponseEntity<Object> productsLidvidBundles(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
      @Valid String node) {
    return this.classMemberOfOf("any", identifier, fields, limit, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> productsLidvidBundlesLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMemberOfOfVers("any", identifier, "latest", fields, limit, sort, start,
      node);
  }

  @Override
  public ResponseEntity<Object> productsLidvidCollections(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMemberOf("any", identifier, fields, limit, sort, start, node);
  }

  @Override
  public ResponseEntity<Object> productsLidvidCollectionsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMemberOfVers("any", identifier, "all", fields, limit, sort, start,
      node);
  }

  @Override
  public ResponseEntity<Object> productsLidvidCollectionsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start, @Valid String node) {
    return this.classMemberOfVers("any", identifier, "latest", fields, limit, sort, start,
      node);
  }
}
