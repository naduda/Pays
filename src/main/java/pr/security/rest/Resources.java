package pr.security.rest;

import java.io.File;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pr.mail.SmtpMailSender;
import pr.security.model.IUser;

@RestController
@RequestMapping("/resources")
public class Resources extends ASecurityRest {
	private static final Logger log = LoggerFactory.getLogger(Resources.class);
	
	@Autowired
	private SmtpMailSender smtpMailSender;
	@Value("${pr.security.block.attempts}")
	private int maxAttempts;
	@Value("${pr.security.block.timeout}")
	private int timeout;
	
	@RequestMapping(value="/user", method=RequestMethod.GET)
	public Principal user(Principal user) {
		if(user != null) {
			IUser u = (IUser) dao.getUserByLogin(null, user.getName());
			dao.updateUserAttempts(null, u.getId(), 0);
		}
		return user;
	}

	@RequestMapping(value="/addUser", method=RequestMethod.PUT)
	public Object addUser(@RequestBody Map<String, Object> user) {
		try {
			return dao.addUser(null, user) ? comm.RESULT_OK : comm.RESULT_BAD;
		} catch (Exception e) {
			e.printStackTrace();
			return comm.resultMessage(e.getMessage());
		}
	}
	
	@RequestMapping(value="/getUserFields", method=RequestMethod.GET)
	public Object getUserFields() {
		try {
			return dao.getUserFields(null);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value="/isUserLock/{login}", method=RequestMethod.GET)
	public Map<String, Object> isBlocked(@PathVariable String login) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			IUser u = (IUser) dao.getUserByLogin(null, login);
			u.setMaxAttempts(maxAttempts);

			model.put("result", u.isLocked());
			model.put("message", "keyUserLock");
			long diff = Timestamp.valueOf(LocalDateTime.now()).getTime() - u.getLastmodified().getTime();
			model.put("wait", (int)(timeout - diff / 1000));
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
	public Object recover(@RequestBody Map<String, String> input) {
		IUser user = null;
		String loginEmail = input.get("loginEmail");
		user = loginEmail.indexOf("@") > 0 ? 
				dao.getUserByEmail(null, loginEmail) : dao.getUserByLogin(null, loginEmail);
		
		if(user == null) return comm.RESULT_BAD;
		
		try {
			String password = RandomStringUtils.random(8, true, true);
			user.setPassword(password);
			dao.updateUser(null, user.toMap());
	
			String emailBody = "<strong>Authentication</strong><hr>" +
					"Your " + "login is <strong>\"" + user.getLogin() + 
					"\"</strong><br> Your password is <strong>\"" + password + "\"" +
					"<br><br><hr><font size=\"0.8em\"><strong>Regards, Pavlo Naduda<br></strong>" +
					"phone: 050 66 22 55 6<br>" +
					"e-mail: naduda.pr@gmail.com (pr@ukreni.com.ua)</font>";
		
			smtpMailSender.send(user.getEmail(), "Authentication", emailBody);
			return comm.RESULT_OK;
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
		return comm.RESULT_BAD;
	}
	
	@RequestMapping(value="/langs", method=RequestMethod.GET)
	public List<?> langs() {
		List<String> model = new ArrayList<>();
		try {
			File folder = comm.getFileFromURL(getClass(), "./static/lang");
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				model.add(file.getName());
			}
		} catch (Exception e) {
			log.info("@RequestMapping /langs Error M " + comm.getFileFromURL(getClass(), "./static/lang"));
		}
		log.info("Exist " + model.size() + " language-files");
		return model;
	}
}