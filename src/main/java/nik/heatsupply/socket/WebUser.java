package nik.heatsupply.socket;

import javax.servlet.http.HttpSession;

public class WebUser {
	private int userId;
	private HttpSession httpSession;
	
	public WebUser(int userId, HttpSession httpSession) {
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