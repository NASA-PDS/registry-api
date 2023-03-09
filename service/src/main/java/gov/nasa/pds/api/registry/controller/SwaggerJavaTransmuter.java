package gov.nasa.pds.api.registry.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.api.base.BundlesApi;
import gov.nasa.pds.api.base.ClassesApi;
import gov.nasa.pds.api.base.CollectionsApi;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.LidVidMismatchException;
import gov.nasa.pds.api.registry.exceptions.LidVidNotFoundException;
import gov.nasa.pds.api.registry.exceptions.MembershipException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.exceptions.UnknownGroupNameException;
import gov.nasa.pds.api.registry.model.ErrorFactory;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;

@Controller
public class SwaggerJavaTransmuter extends SwaggerJavaDeprecatedTransmuter
    implements ControlContext, BundlesApi, CollectionsApi, ClassesApi, ProductsApi {


  private static final Logger log = LoggerFactory.getLogger(SwaggerJavaTransmuter.class);
  private final ObjectMapper objectMapper;
  private final HttpServletRequest request;

  @Value("${server.contextPath}")
  protected String contextPath;

  @Autowired
  protected HttpServletRequest context;

  @Autowired
  ConnectionContext connection;

  @org.springframework.beans.factory.annotation.Autowired
  public SwaggerJavaTransmuter(ObjectMapper objectMapper, HttpServletRequest context) {
    this.objectMapper = objectMapper;
    this.request = context;
  }

  @Override
  public URL getBaseURL() {
    try {

      URL baseURL;

      String proxyContextPath = this.context.getContextPath();
      SwaggerJavaTransmuter.log.debug("contextPath is: '" + proxyContextPath + "'");

      if (this.proxyRunsOnDefaultPort()) {
        baseURL = new URL(this.context.getScheme(), this.context.getServerName(), proxyContextPath);
      } else {
        baseURL = new URL(this.context.getScheme(), this.context.getServerName(),
            this.context.getServerPort(), proxyContextPath);
      }

      log.debug("baseUrl is " + baseURL.toString());
      return baseURL;

    } catch (MalformedURLException e) {
      log.error("Server URL was not retrieved");
      return null;
    }
  }

  @Override
  public ConnectionContext getConnection() {
    return this.connection;
  }

  @Override
  public ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  protected ResponseEntity<Object> processs(EndpointHandler handler, URIParameters parameters) {
    long begin = System.currentTimeMillis();
    try {
      parameters.setAccept(this.request.getHeader("Accept")).setProductIdentifier(this);
      if (parameters.getVerifyClassAndId())
        LidVidUtils.verify(this, parameters);
      return handler.transmute(this, parameters);
    } catch (ApplicationTypeException e) {
      log.error("Application type not implemented", e);
      return new ResponseEntity<Object>(ErrorFactory.build(e, this.request),
          HttpStatus.NOT_ACCEPTABLE);
    } catch (IOException e) {
      log.error(
          "Couldn't get or serialize response for content type " + this.request.getHeader("Accept"),
          e);
      return new ResponseEntity<Object>(ErrorFactory.build(e, this.request),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (LidVidMismatchException e) {
      log.warn("The lid(vid) '" + parameters.getIdentifier()
          + "' in the data base type does not match given type '" + parameters.getGroup() + "'");
      return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
    } catch (LidVidNotFoundException e) {
      log.warn("Could not find lid(vid) in database: " + parameters.getIdentifier());
      return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
    } catch (MembershipException e) {
      log.warn("The given lid(vid) does not support the requested membership.");
      return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
    } catch (NothingFoundException e) {
      log.warn("Could not find any matching reference(s) in database.");
      return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
    } catch (NoViableAltException | ParseCancellationException e) {
      log.warn("The given search string '" + parameters.getQuery() + "' cannot be parsed.");
      return new ResponseEntity<Object>(ErrorFactory.build(
          new ParseCancellationException(
              "The given search string '" + parameters.getQuery() + "' cannot be parsed."),
          this.request), HttpStatus.BAD_REQUEST);
    } catch (UnknownGroupNameException e) {
      log.error("Group name not implemented", e);
      return new ResponseEntity<Object>(ErrorFactory.build(e, this.request),
          HttpStatus.NOT_ACCEPTABLE);
    } finally {
      log.info(
          "Transmuter processing of request took: " + (System.currentTimeMillis() - begin) + " ms");
    }
  }

  private boolean proxyRunsOnDefaultPort() {
    return (("https".equals(this.context.getScheme()) && (this.context.getServerPort() == 443))
        || ("http".equals(this.context.getScheme()) && (this.context.getServerPort() == 80)));
  }

  @Override
  public ResponseEntity<Object> bundleList(@Valid List<String> fields, @Valid List<String> keywords,
      @Min(0) @Valid Integer limit, @Valid String q, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.bundleList(fields, keywords, limit, q, sort, start);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvid(String identifier, @Valid List<String> fields) {
    // TODO Auto-generated method stub
    return super.bundlesLidvid(identifier, fields);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.bundlesLidvidAll(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollections(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.bundlesLidvidCollections(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollectionsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.bundlesLidvidCollectionsAll(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidCollectionsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.bundlesLidvidCollectionsLatest(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidLatest(String identifier, @Valid List<String> fields) {
    // TODO Auto-generated method stub
    return super.bundlesLidvidLatest(identifier, fields);
  }

  @Override
  public ResponseEntity<Object> bundlesLidvidProducts(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.bundlesLidvidProducts(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> collectionList(@Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.collectionList(fields, keywords, limit, q, sort, start);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvid(String identifier, @Valid List<String> fields) {
    // TODO Auto-generated method stub
    return super.collectionsLidvid(identifier, fields);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.collectionsLidvidAll(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidBundles(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.collectionsLidvidBundles(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidLatest(String identifier,
      @Valid List<String> fields) {
    // TODO Auto-generated method stub
    return super.collectionsLidvidLatest(identifier, fields);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProducts(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.collectionsLidvidProducts(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProductsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.collectionsLidvidProductsAll(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> collectionsLidvidProductsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.collectionsLidvidProductsLatest(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productList(@Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productList(fields, keywords, limit, q, sort, start);
  }

  @Override
  public ResponseEntity<Object> productsLidividBundlesAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productsLidividBundlesAll(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productsLidvidBundles(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productsLidvidBundles(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productsLidvidBundlesLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productsLidvidBundlesLatest(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productsLidvidCollections(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productsLidvidCollections(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productsLidvidCollectionsAll(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productsLidvidCollectionsAll(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productsLidvidCollectionsLatest(String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productsLidvidCollectionsLatest(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMemberOf(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMemberOf(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMemberOfOf(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMemberOfOf(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMemberOfOfVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMemberOfOfVers(identifier, versions, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMemberOfVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMemberOfVers(identifier, versions, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMembers(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMembers(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMembersMembers(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMembersMembers(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMembersMembersVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMembersMembersVers(identifier, versions, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> productMembersVers(String identifier, String versions,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.productMembersVers(identifier, versions, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> selectByLidvid(String identifier, @Valid List<String> fields) {
    // TODO Auto-generated method stub
    return super.selectByLidvid(identifier, fields);
  }

  @Override
  public ResponseEntity<Object> selectByLidvidAll(String identifier, @Valid List<String> fields,
      @Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.selectByLidvidAll(identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> selectByLidvidLatest(String identifier,
      @Valid List<String> fields) {
    // TODO Auto-generated method stub
    return super.selectByLidvidLatest(identifier, fields);
  }

  @Override
  public ResponseEntity<List<String>> classes() {
    // TODO Auto-generated method stub
    return super.classes();
  }

  @Override
  public ResponseEntity<Object> classList(String propertyClass, @Valid List<String> fields,
      @Valid List<String> keywords, @Min(0) @Valid Integer limit, @Valid String q,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classList(propertyClass, fields, keywords, limit, q, sort, start);
  }

  @Override
  public ResponseEntity<Object> classMemberOf(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMemberOf(propertyClass, identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> classMemberOfOf(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMemberOfOf(propertyClass, identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> classMemberOfOfVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMemberOfOfVers(propertyClass, identifier, versions, fields, limit, sort,
        start);
  }

  @Override
  public ResponseEntity<Object> classMemberOfVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMemberOfVers(propertyClass, identifier, versions, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> classMembers(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMembers(propertyClass, identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> classMembersMembers(String propertyClass, String identifier,
      @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
      @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMembersMembers(propertyClass, identifier, fields, limit, sort, start);
  }

  @Override
  public ResponseEntity<Object> classMembersMembersVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMembersMembersVers(propertyClass, identifier, versions, fields, limit, sort,
        start);
  }

  @Override
  public ResponseEntity<Object> classMembersVers(String propertyClass, String identifier,
      String versions, @Valid List<String> fields, @Min(0) @Valid Integer limit,
      @Valid List<String> sort, @Min(0) @Valid Integer start) {
    // TODO Auto-generated method stub
    return super.classMembersVers(propertyClass, identifier, versions, fields, limit, sort, start);
  }
}
