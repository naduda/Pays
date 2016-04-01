package pr.pays;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import pr.pays.rest.Resources;
import pr.pays.rest.SecureResources;
import pr.pays.security.SecurityConfiguration;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(new Class[] {
				Application.class,
				Resources.class,
				SecureResources.class,
				SecurityConfiguration.class
		});
	}

}