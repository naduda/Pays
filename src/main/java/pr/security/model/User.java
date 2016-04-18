package pr.security.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class User implements IUser {
	private int id;
	private String login;
	private String password;
	private String email;
	private boolean active;
	private int attempts;
	private Timestamp lastmodified;
	private int maxAttempts;
	
	public User() {}
	
	public User(String login, String password, String email, int maxAttempts) {
		this.login = login;
		this.password = password;
		this.email = email;
		this.maxAttempts = maxAttempts;
		this.active = true;
		this.attempts = 0;
		this.lastmodified = Timestamp.valueOf(LocalDateTime.now());
	}

	@Override
	public String toString() {
		return "id = " + id + ", login = " + login + ", email = " + email;
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> ret = new HashMap<>();
		ret.put("login", login);
		ret.put("password", password);
		ret.put("email", email);
		ret.put("active", active);
		ret.put("attempts", attempts);
		ret.put("lastmodified", lastmodified);
		ret.put("maxattempts", maxAttempts);
		return ret;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public Timestamp getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(Timestamp lastmodified) {
		this.lastmodified = lastmodified;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	@Override
	public boolean isLocked() {
		return getAttempts() >= maxAttempts;
	}
}