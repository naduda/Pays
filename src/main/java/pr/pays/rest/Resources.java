package pr.pays.rest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pr.pays.dao.DataBaseImpl;
import pr.pays.mail.SmtpMailSender;
import pr.pays.model.User;

@RestController
@RequestMapping("/resources")
public class Resources {
	private static final Logger log = LoggerFactory.getLogger(Resources.class);
	@Resource(name="DataBaseImpl")
	private DataBaseImpl dao;
	@Autowired
	private SmtpMailSender smtpMailSender;
	@Value("${nik.security.block.attempts}")
	private int maxAttempts;
	@Value("${nik.security.block.timeout}")
	private int timeout;
	
	
	@RequestMapping(value="/user", method=RequestMethod.GET)
	public Principal user(Principal user) {
		if(user != null) {
			User u = dao.getUserByLogin(user.getName());
			dao.updateUserAttempts(u.getId(), 0);
		}
		return user;
	}

	@RequestMapping(value="/isUserLock/{login}", method=RequestMethod.GET)
	public Map<String, Object> isBlocked(@PathVariable String login) {
		Map<String, Object> model = new HashMap<String, Object>();
		
		try {
			User u = dao.getUserByLogin(login);
			u.setMaxAttempts(maxAttempts);

			model.put("result", u.isLocked());
			model.put("message", "keyUserLock");
			model.put("wait", (int)(1000 * timeout - (Timestamp.valueOf(LocalDateTime.now()).getTime() - u.getLastmodified().getTime()))/1000);
		} catch (Exception e) {
			model.put("result", false);
		}
		return model;
	}
	
	@RequestMapping("/detect-device")
	public Map<String, Object> detectDevice(Device device) {
		Map<String, Object> model = new HashMap<String, Object>();
		String deviceType = "unknown";
		if (device.isNormal()) {
			deviceType = "normal";
		} else if (device.isMobile()) {
			deviceType = "mobile";
		} else if (device.isTablet()) {
			deviceType = "tablet";
		}
		model.put("deviceType", deviceType);
		return model;
	}
	
	@RequestMapping(value="/recover", method=RequestMethod.POST)
	public  Map<String, Object> recover(@RequestBody Map<String, Object> input) {
		log.info(input.toString());
		Map<String, Object> model = new HashMap<String, Object>();
		String mailAddress = input.get("login_email").toString();
		String login = null;
		User user = null;
		String password = null;
		if(mailAddress.indexOf("@") < 0) {
			login = mailAddress;
			user = dao.getUserByLogin(login);
		} else {
			user = dao.getUserByEmail(mailAddress);
		}
		
		if(user != null) {
			mailAddress = user.getEmail();
			login = user.getLogin();
			password = RandomStringUtils.random(8, true, true);
			BCryptPasswordEncoder pe = new BCryptPasswordEncoder(12);
			user.setPassword(pe.encode(password));
			dao.updateUser(user);
		} else {
			log.info("User " + input + " not exist.");
			model.put("result", "keyTryAgain");
			return model;
		}

		String emailBody = "<strong>Authentication</strong><hr>" +
				"Your " + "login is <strong>\"" + login + "\"</strong><br> Your password is <strong>\"" + password + "\"" +
				"<br><br><hr><font size=\"0.8em\"><strong>Regards, Pavlo Naduda<br></strong>" +
				"phone: 050 66 22 55 6<br>" +
				"e-mail: naduda.pr@gmail.com (pr@ukreni.com.ua)</font>";

		try {
			smtpMailSender.send(mailAddress, "Authentication", emailBody);
			model.put("result", "success");
		} catch (MessagingException e) {
			model.put("result", "keyTryAgain");
		}
		
		return model;
	}
	
	@RequestMapping(value="/langs", method=RequestMethod.GET)
	public List<?> langs() throws SQLException {
		List<String> model = new ArrayList<>();
		try {
			File folder = getFileFromURL("./static/lang");
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				model.add(file.getName());
			}
		} catch (Exception e) {
			log.info("@RequestMapping /langs Error M " + getFileFromURL("./static/lang"));
		}
		log.info("Exist " + model.size() + " language-files");
		return model;
	}
	
	private File getFileFromURL(String path) {
		URL url = this.getClass().getClassLoader().getResource(path);
		
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}
		return file;
	}
}