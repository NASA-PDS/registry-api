package gov.nasa.pds.api.registry;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "gov.nasa.pds.api.registry.configuration ", "gov.nasa.pds.api.registry.controller",
		"gov.nasa.pds.api.registry.model", "gov.nasa.pds.api.registry.search" })
public class SpringBootMain implements CommandLineRunner {

	@Override
	public void run(String... arg0) throws Exception {
		if (arg0.length > 0 && arg0[0].equals("exitcode")) {
			throw new ExitException();
		}
	}

	public static void main(String[] args) throws Exception {
		new SpringApplication(SpringBootMain.class).run(args);
	}

	class ExitException extends RuntimeException implements ExitCodeGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public int getExitCode() {
			return 10;
		}

	}
}
