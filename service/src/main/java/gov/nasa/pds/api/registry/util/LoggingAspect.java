package gov.nasa.pds.api.registry.util;


import org.springframework.stereotype.Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {
  private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


  public static String sanitizeForLog(String input) {
    if (input == null)
      return null;
    return input.replaceAll("[\n\r\t]", "_") // Replace newlines and tabs
        .replaceAll("[\\x1b\\u001B]\\[[;\\d]*[ -/]*[@-~]", ""); // ANSI escape
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


