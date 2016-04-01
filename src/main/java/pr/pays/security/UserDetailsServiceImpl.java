package pr.pays.security;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pr.pays.dao.DataBaseImpl;
import pr.pays.model.User;

@Service("UserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
	@Resource(name="DataBaseImpl")
	private DataBaseImpl dao;
	@Value("${nik.security.block.attempts}")
	private int maxAttempts;
	@Value("${nik.security.block.timeout}")
	private int timeout;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = dao.getUserByLogin(username);
		if(user == null) throw new UsernameNotFoundException("User " + username + " not found");
		user.setMaxAttempts(maxAttempts);

		if(Timestamp.valueOf(LocalDateTime.now()).getTime() - user.getLastmodified().getTime() > 1000 * timeout) {
			dao.updateUserAttempts(user.getId(), 1);
			user.setAttempts(1);
		} else if(user.getAttempts() < maxAttempts) {
			dao.updateUserAttempts(user.getId(), user.getAttempts() + 1);
		}
		
		return new UserDetailsImpl(user);
	}
}