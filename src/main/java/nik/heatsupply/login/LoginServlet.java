package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.User;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int SESSION_TIMIOUT = 5 * 60;
	private static final int MAX_LOGIN_TRY = 3;
	private int logCounter = 0;
	private long lastTryLogin;
	private boolean isLock;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		isLock = session.getAttribute("lock") != null ? Boolean.parseBoolean(session.getAttribute("lock").toString()) : false;
		lastTryLogin = session.getAttribute("lastTryLogin") != null ? 
				Long.parseLong(session.getAttribute("lastTryLogin").toString()) : System.currentTimeMillis();
		if(isLock && System.currentTimeMillis() - lastTryLogin < SESSION_TIMIOUT * 1000) {
			session.setAttribute("logCounter", 0);
			response.setContentType("text/html");
			
			String url = request.getRequestURL().toString();
			url = url.substring(0, url.lastIndexOf("/"));
			try(PrintWriter out = response.getWriter();){
				int time = SESSION_TIMIOUT - (int) ((System.currentTimeMillis() - lastTryLogin) / 1000);
				out.println("<h1 style='color:red;'>Lock</h1>");
				out.println("<a href='" + url + "/login.html'>Try again after " + time + " s</a>");
			} catch(Exception e) {
				e.printStackTrace();
			}
			return;
		} else {
			session.setAttribute("lock", "false");
		}

		String user = request.getParameter("user");
		String pwd = request.getParameter("pwd");
		session.setAttribute("login", "false");
		if(isChecked(user, pwd, session)){
			session.setMaxInactiveInterval(SESSION_TIMIOUT);
//			Cookie userName = new Cookie("user", user);
//			userName.setMaxAge(SESSION_TIMIOUT);
//			response.addCookie(userName);
			response.sendRedirect("main.html");
		} else {
			session.setAttribute("login", "false");
			session.setAttribute("lastTryLogin", System.currentTimeMillis());
			String uri = request.getRequestURI();
			System.out.println(uri);

			if(session.getAttribute("logCounter") == null || session.isNew()) {
				logCounter = 0;
			} else {
				logCounter = Integer.parseInt(session.getAttribute("logCounter").toString());
			}
			session.setAttribute("logCounter", ++logCounter);
			if(logCounter == MAX_LOGIN_TRY){
				session.setAttribute("lock", "true");
			}
			response.sendRedirect("#/login");
		}
		System.out.println(user + " === " + pwd);
	}
	
	private boolean isChecked(String userName, String psw, HttpSession session) {
		try {
			Encryptor encr = new Encryptor();
			ConnectDB condb = new ConnectDB();
			User u = condb.getUser(userName);
			if(u == null) return false;
			if(encr.decrypt(u.getPassword()).trim().equals(psw)) {
				session.setAttribute("user", u.getLogin());
				session.setAttribute("userId", u.getIdUser());
				session.setAttribute("login", "true");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}