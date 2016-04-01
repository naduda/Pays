package pr.pays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import pr.pays.rest.Resources;
import pr.pays.rest.SecureResources;
import pr.pays.security.SecurityConfiguration;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(new Class[]{
				Application.class,
				Resources.class,
				SecureResources.class,
				SecurityConfiguration.class
				}, args);
	}
}