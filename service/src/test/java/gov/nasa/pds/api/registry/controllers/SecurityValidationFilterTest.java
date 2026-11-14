package gov.nasa.pds.api.registry.controllers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nasa.pds.api.registry.model.exceptions.BadRequestException;
import gov.nasa.pds.api.registry.model.exceptions.UnknownQueryParameterException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SecurityValidationFilterTest {

  private SecurityValidationFilter filter;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    filter = new SecurityValidationFilter();
    filter.authorizedForwardedHosts = List.of("localhost");
    response = new MockHttpServletResponse();
  }

  private MockHttpServletRequest requestFor(String rawPath) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", rawPath);
    request.setRequestURI(rawPath);
    return request;
  }

  private boolean preHandle(String rawPath) throws Exception {
    return filter.preHandle(requestFor(rawPath), response, new Object());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/products/urn:nasa:pds:context:investigation:mission.mars2020::1.0",
      "/products/urn:nasa:pds:context:investigation:mission.mars2020::1.0/latest",
      "/products/urn:nasa:pds:context:investigation:mission.mars2020/all",
      "/products/urn:nasa:pds:insight_documents:document_hp3rad/members/members",
      "/products/urn%3Anasa%3Apds%3Ax%3A%3A1.0", "/products/urn:nasa:pds:a-b_c.d::10.2",
      "/products/urn:nasa:pds:caf%C3%A9::1.0", "/products/with%20space", "/classes/context",
      "/properties", "/health", "/"})
  void normalPathsAreAccepted(String rawPath) throws Exception {
    assertTrue(preHandle(rawPath));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/products/urn:nasa:pds:x%0d%0a2026-01-01%20INFO%20forged", "/products/%0d",
      "/products/%0a", "/products/urn:nasa:pds:x%09::1.0", "/products/urn:nasa:pds:x%1b%5b31m",
      "/products/urn:nasa:pds:x%00", "/products/urn:nasa:pds:x%7f", "/products/urn:nasa:pds:x%0D%0A",
      "/products/urn:nasa:pds:x%c2%85", "/products/urn:nasa:pds:x%e2%80%a8",
      "/products/urn:nasa:pds:x%e2%80%a9", "/products/urn:nasa:pds:x%0b", "/products/urn:nasa:pds:x%0c",
      "/products/urn:nasa:pds:x%0a/latest", "/%0d%0aproducts", "/products%0a/x"})
  void controlCharactersInPathAreRejected(String rawPath) {
    BadRequestException ex = assertThrows(BadRequestException.class, () -> preHandle(rawPath));

    // the message sent back to the client does not echo the offending input
    assertEquals("BadRequestException: Request path must not contain control characters.",
        ex.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/products/urn:nasa:pds:x%250d%250a", "/products/urn:nasa:pds:x%25",
      "/products/urn:nasa:pds:x%2509", "/products/urn:nasa:pds:x%25c2%2585"})
  void doubleEncodedInputIsNotDecodedIntoControlCharactersAndIsAccepted(String rawPath) {
    assertDoesNotThrow(() -> preHandle(rawPath));
  }

  @Test
  void malformedPercentEncodingIsRejected() {
    assertThrows(BadRequestException.class, () -> preHandle("/products/urn:nasa:pds:x%zz"));
  }

  @Test
  void queryStringIsNotPartOfThePath() throws Exception {
    MockHttpServletRequest request = requestFor("/products");
    request.setQueryString("q=a%0d%0ab");
    request.addParameter("q", "a\r\nb");

    assertTrue(filter.preHandle(request, response, new Object()));
  }

  @Test
  void unknownQueryParameterIsStillRejected() {
    MockHttpServletRequest request = requestFor("/products");
    request.addParameter("unknown", "x");

    assertThrows(UnknownQueryParameterException.class,
        () -> filter.preHandle(request, response, new Object()));
  }
}
