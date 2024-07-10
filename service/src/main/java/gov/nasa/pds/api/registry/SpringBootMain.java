package gov.nasa.pds.api.registry;

import java.lang.IllegalArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

// TODO
// add archive status filter
// add other resolver endpoints

@SpringBootApplication
@OpenAPIDefinition
@EnableScheduling
@ComponentScan(basePackages = {"gov.nasa.pds.api.registry.configuration",
    "gov.nasa.pds.api.registry.controllers", "gov.nasa.pds.api.registry.model",
    "gov.nasa.pds.api.registry.search", "javax.servlet.http"})
public class SpringBootMain implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(SpringBootMain.class);



  @Override
  public void run(String... arg0) throws Exception {
    if (arg0.length > 0 && arg0[0].equals("exitcode")) {
      throw new ExitException();
    }
  }

  public static void main(String[] args) throws Exception {
    try {
      AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
      ctx.refresh();

      new SpringApplication(SpringBootMain.class).run(args);
      ctx.close();
    } catch (IllegalArgumentException e) {
      log.error(
          "Illegal springboot argument in the start command. Restart the application differently.",
          e.getMessage(), e);
    }
  }

  class ExitException extends RuntimeException implements ExitCodeGenerator {
    private static final long serialVersionUID = 1L;

    @Override
    public int getExitCode() {
      return 10;
    }

  }
}
