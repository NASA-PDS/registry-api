package gov.nasa.pds.api.registry.model.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class RegistryApiExceptionTest {

  private Logger logger;
  private ListAppender<ILoggingEvent> appender;

  @BeforeEach
  void attachAppender() throws InterruptedException {
    // Tests may run concurrently with the first SLF4J initialization, during which loggers are
    // temporary substitutes: wait until the Logback context is up.
    while (!(LoggerFactory.getILoggerFactory() instanceof LoggerContext)) {
      Thread.sleep(10);
    }
    logger = (Logger) LoggerFactory.getLogger(RegistryApiException.class);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  @AfterEach
  void detachAppender() {
    logger.detachAppender(appender);
  }

  /** Messages logged for the given exception (other tests may log concurrently). */
  private List<String> loggedFor(RegistryApiException ex) {
    return appender.list.stream().map(ILoggingEvent::getFormattedMessage)
        .filter(message -> message.startsWith(ex.getUuid())).collect(Collectors.toList());
  }

  @Test
  void normalIdentifierIsLoggedUnchanged() {
    NotFoundException ex =
        new NotFoundException("No product found with identifier urn:nasa:pds:x::1.0");

    assertEquals(List.of(
        ex.getUuid() + " NotFoundException: No product found with identifier urn:nasa:pds:x::1.0"),
        loggedFor(ex));
  }

  @Test
  void controlCharactersInIdentifierProduceSingleSanitizedLogLine() {
    String identifier = "urn:nasa:pds:x\r\n2026-01-01 00:00:00 INFO forged log line\tend";
    NotFoundException ex = new NotFoundException("No product found with identifier " + identifier);

    List<String> logged = loggedFor(ex);
    assertEquals(1, logged.size());
    assertFalse(logged.get(0).contains("\r"));
    assertFalse(logged.get(0).contains("\n"));
    assertFalse(logged.get(0).contains("\t"));
    assertEquals(
        ex.getUuid() + " NotFoundException: No product found with identifier "
            + "urn:nasa:pds:x__2026-01-01 00:00:00 INFO forged log line_end",
        logged.get(0));
  }

  @Test
  void messageReturnedToClientIsNotModified() {
    String identifier = "urn:nasa:pds:x\r\nforged";
    NotFoundException ex = new NotFoundException("No product found with identifier " + identifier);

    assertEquals("NotFoundException: No product found with identifier " + identifier,
        ex.getMessage());
  }

  @Test
  void nullMessageIsSafe() {
    RegistryApiException ex = new RegistryApiException(null);

    assertEquals(List.of(ex.getUuid() + " null"), loggedFor(ex));
  }
}
