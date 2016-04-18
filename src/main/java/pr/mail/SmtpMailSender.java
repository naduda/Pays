package pr.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SmtpMailSender {
	private static final Logger log = LoggerFactory.getLogger(SmtpMailSender.class);
	@Autowired
	private JavaMailSender javaMailSender;
	
	public void send(String recipient, String title, String body) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setSubject(title);
		helper.setTo(recipient);
		helper.setText(body, true);
		
		log.info("Start sending to " + recipient);
		javaMailSender.send(message);
		log.info("Sended!");
	}
}