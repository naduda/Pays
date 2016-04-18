package pr.security.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pr.security.model.IUser;

@RestController
@RequestMapping("/saferesources")
public class SafeResources extends ASecurityRest {
	private static final Logger log = LoggerFactory.getLogger(SafeResources.class);

	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public Object profileInfo() {
		Map<String, Object> u = dao.getUserProfile(null, getCurrentUser().getId());
		Map<String, Object> ret = new HashMap<>();
		u.keySet().forEach(k -> ret.put(k.toLowerCase(), u.get(k)));
		ret.remove("password");
		ret.remove("lastmodified");
		ret.remove("active");
		ret.remove("maxattempts");
		ret.remove("attempts");
		return ret;
	}
	

	@RequestMapping(value="/delUser", method=RequestMethod.DELETE)
	public Object delUser(HttpServletRequest req) {
		String login = req.getParameter("login");
		String password = req.getParameter("password");
		IUser cu = getCurrentUser();
		try {
			if(cu.getLogin().equals(login) && dao.checkPassword(password, cu.getPassword())) {
				return dao.deleteUserFromDB(null, cu.getId()) ? comm.RESULT_OK : comm.RESULT_BAD;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return comm.RESULT_BAD;
	}
	
	@RequestMapping(value="/updateProfile", method=RequestMethod.POST)
	public Object updateProfile(@RequestBody Map<String, Object> user) {
		try {
			IUser cu = getCurrentUser();
			user.put("id", cu.getId());
			if(cu.getLogin().equals(user.get("login").toString()) &&
					dao.checkPassword(user.get("password").toString(), cu.getPassword())) {
				if(user.get("password1") != null && user.get("password1").toString().length() > 0){
					user.put("password", user.get("password1"));
				}
				user.remove("password1");
				return dao.updateUser(null, user) ? comm.RESULT_OK : comm.RESULT_BAD;
			} else {
				return comm.resultMessage("You can't update this user with current password.");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return comm.RESULT_BAD;
		}
	}
}