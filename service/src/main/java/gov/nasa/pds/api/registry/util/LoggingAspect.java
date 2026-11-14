package gov.nasa.pds.api.registry.util;


import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {
  /** A control character (CR, LF, tab, NUL, ESC, NEL, ...) or a Unicode line/paragraph separator. */
  public static final Pattern CONTROL_OR_SEPARATOR = Pattern.compile("[\\p{Cc}\\p{Zl}\\p{Zp}]");

  private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


  /**
   * Neutralize user-controlled text before it is written to the logs so it cannot forge log lines:
   * ANSI escape sequences are removed, and every remaining control character (CR, LF, tab, NEL,
   * ...) and Unicode line/paragraph separator is replaced by an underscore.
   */
  public static String sanitizeForLog(String input) {
    if (input == null)
      return null;
    return CONTROL_OR_SEPARATOR
        .matcher(input.replaceAll("\\u001B\\[[;\\d]*[ -/]*[@-~]", "")) // ANSI escape
        .replaceAll("_");
  }


  @Around("@annotation(gov.nasa.pds.api.registry.util.LogExecutionTime)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    logger.info("Log execution time through decorator");
    long startTime = System.currentTimeMillis();
    Object proceed = joinPoint.proceed();
    long endTime = System.currentTimeMillis();
    logger.info("{} executed in {} ms", joinPoint.getSignature(), endTime - startTime);
    return proceed;
  }
}


