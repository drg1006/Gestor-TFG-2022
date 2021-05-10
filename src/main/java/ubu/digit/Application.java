package ubu.digit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import ubu.digit.pesistence.SistInfDataCsv;
import ubu.digit.ui.MainLayout;
import ubu.digit.ui.views.InformationView;

/**
 * The entry point of the Spring Boot application.
 */

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(Application.class);
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
