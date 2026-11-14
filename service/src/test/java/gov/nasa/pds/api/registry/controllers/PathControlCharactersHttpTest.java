package gov.nasa.pds.api.registry.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nasa.pds.api.registry.util.LoggingAspect;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Runs the real SecurityValidationFilter and exception handler behind an embedded Tomcat and the
 * Spring MVC dispatcher, to check what a client really sends: percent-encoded control characters
 * in the URL path are decoded before they reach a controller, and must be refused before that.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = PathControlCharactersHttpTest.TestConfig.class,
    properties = {"server.authorizedForwardedHost=localhost"})
class PathControlCharactersHttpTest {

  /** Path variable values seen by the controller. */
  static final List<String> RECEIVED = new CopyOnWriteArrayList<>();

  @RestController
  static class RecordingController {
    @GetMapping("/products/{identifier}")
    String product(@PathVariable("identifier") String identifier) {
      RECEIVED.add(identifier);
      return "reached";
    }

    @GetMapping("/products/{identifier}/latest")
    String latest(@PathVariable("identifier") String identifier) {
      RECEIVED.add(identifier);
      return "reached";
    }
  }

  @Configuration
  @EnableWebMvc
  @ImportAutoConfiguration({TomcatServletWebServerAutoConfiguration.class,
      DispatcherServletAutoConfiguration.class})
  @Import({RecordingController.class, SecurityValidationFilter.class,
      RegistryApiResponseEntityExceptionHandler.class})
  static class TestConfig implements WebMvcConfigurer {
    @Autowired
    private SecurityValidationFilter securityValidationFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(securityValidationFilter);
    }
  }

  @LocalServerPort
  private int port;

  private final HttpClient client = HttpClient.newHttpClient();

  private HttpResponse<String> get(String rawPathAndQuery) throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + rawPathAndQuery)).build();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  /** Values received by the controller that start with the given prefix (tests may run concurrently). */
  private List<String> receivedStartingWith(String prefix) {
    return RECEIVED.stream().filter(value -> value.startsWith(prefix)).toList();
  }

  @Test
  void lidvidReachesTheControllerUnchanged() throws Exception {
    HttpResponse<String> response = get("/products/urn:nasa:pds:context:target:planet.mars::1.0");

    assertEquals(200, response.statusCode());
    assertEquals(List.of("urn:nasa:pds:context:target:planet.mars::1.0"),
        receivedStartingWith("urn:nasa:pds:context:target:planet.mars"));
  }

  @Test
  void percentEncodedLidvidReachesTheControllerDecoded() throws Exception {
    HttpResponse<String> response = get("/products/urn%3Anasa%3Apds%3Aencoded%3A%3A1.0/latest");

    assertEquals(200, response.statusCode());
    assertEquals(List.of("urn:nasa:pds:encoded::1.0"), receivedStartingWith("urn:nasa:pds:encoded"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"%0d%0aforged", "%0a", "%09", "%1b", "%c2%85", "%e2%80%a8", "%e2%80%a9"})
  void controlCharactersInPathVariableGetBadRequestAndNeverReachTheController(String encoded)
      throws Exception {
    HttpResponse<String> response = get("/products/urn:nasa:pds:rejected" + encoded + "::1.0");

    assertEquals(400, response.statusCode());
    assertTrue(response.body().contains("Request path must not contain control characters."),
        response.body());
    // the error body only echoes the still-encoded URI; the template's own line feeds are legitimate
    assertFalse(
        LoggingAspect.CONTROL_OR_SEPARATOR.matcher(response.body().replace("\n", "")).find());
    assertTrue(receivedStartingWith("urn:nasa:pds:rejected").isEmpty());
  }

  @Test
  void controlCharactersInPathBeforeASubresourceAreRejected() throws Exception {
    HttpResponse<String> response = get("/products/urn:nasa:pds:rejected%0d%0a/latest");

    assertEquals(400, response.statusCode());
    assertTrue(receivedStartingWith("urn:nasa:pds:rejected").isEmpty());
  }

  @Test
  void unmappedPathWithControlCharactersIsNotFoundWithoutEchoingThem() throws Exception {
    // no handler matches, so the interceptor is not involved
    HttpResponse<String> response = get("/nothing%0d%0ahere");

    assertEquals(404, response.statusCode());
    assertFalse(
        LoggingAspect.CONTROL_OR_SEPARATOR.matcher(response.body().replace("\n", "")).find());
  }

  @Test
  void nulInPathNeverReachesTheController() throws Exception {
    // Tomcat itself may already refuse %00 before the application is involved
    HttpResponse<String> response = get("/products/urn:nasa:pds:rejected%00::1.0");

    assertEquals(400, response.statusCode());
    assertTrue(receivedStartingWith("urn:nasa:pds:rejected").isEmpty());
  }

  @Test
  void doubleEncodedInputIsNotRejectedAndStaysEncoded() throws Exception {
    HttpResponse<String> response = get("/products/urn:nasa:pds:double%250d%250a");

    assertEquals(200, response.statusCode());
    assertEquals(List.of("urn:nasa:pds:double%0d%0a"), receivedStartingWith("urn:nasa:pds:double"));
  }

  @Test
  void queryValuesAreNotCheckedByThePathRule() throws Exception {
    HttpResponse<String> response = get("/products/urn:nasa:pds:query?q=a%0d%0ab");

    assertEquals(200, response.statusCode());
    assertEquals(List.of("urn:nasa:pds:query"), receivedStartingWith("urn:nasa:pds:query"));
  }
}
