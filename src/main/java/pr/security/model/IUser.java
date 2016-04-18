package pr.security.model;

import java.sql.Timestamp;
import java.util.Map;

public interface IUser {
	int getId();
	String getLogin();
	String getPassword();
	void setPassword(String value);
	String getEmail();
	void setEmail(String value);
	int getAttempts();
	void setAttempts(int value);
	void setMaxAttempts(int value);
	Timestamp getLastmodified();
	boolean isLocked();
	boolean isActive();
	void setActive(boolean value);
	Map<String, Object> toMap();
}