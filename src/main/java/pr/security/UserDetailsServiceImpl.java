package pr.security;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pr.security.db.SecureDatabaseAPI;
import pr.security.model.User;

@Service("UserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
	private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Resource(name="SecureDatabaseAPI")
	private SecureDatabaseAPI dao;
	@Value("${pr.security.block.attempts}")
	private int maxAttempts;
	@Value("${pr.security.block.timeout}")
	private int timeout;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("before");
		User user = (User) dao.getUserByLogin(null, username);
		if(user == null) throw new UsernameNotFoundException("User " + username + " not found");
		log.debug(user.toString());
		user.setMaxAttempts(maxAttempts);
		
		if(Timestamp.valueOf(LocalDateTime.now()).getTime() - user.getLastmodified().getTime() > 1000 * timeout) {
			dao.updateUserAttempts(null, user.getId(), 1);
			user.setAttempts(1);
		} else if(user.getAttempts() < maxAttempts) {
			dao.updateUserAttempts(null, user.getId(), user.getAttempts() + 1);
		}
		return new UserDetailsImpl(user);
	}
}