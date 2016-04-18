package pr.pays;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import pr.mail.SmtpMailSender;
import pr.pays.rest.RestTools;
import pr.pays.rest.SecureResources;
import pr.security.SecurityConfiguration;
import pr.security.rest.Common4rest;
import pr.security.rest.Resources;
import pr.security.rest.SafeResources;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(new Class[] {
				Application.class,
				RestTools.class,
				Common4rest.class,
				Resources.class,
				SafeResources.class,
				SmtpMailSender.class,
				SecureResources.class,
				SecurityConfiguration.class
		});
	}

}