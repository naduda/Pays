package nik.heatsupply.socket;

import javax.servlet.http.HttpSession;

public class User {
	private int userId;
	private HttpSession httpSession;
	
	public User(int userId, HttpSession httpSession) {
		this.userId = userId;
		this.httpSession = httpSession;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
}