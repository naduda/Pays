package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.User;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final int ERROR_UPDATE = 1;
	private final int ERROR_PASSWORD = 2;
	private final int SUCCESS = 3;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		String name = request.getParameter("name");
		String login = request.getParameter("login");
		String email = request.getParameter("email");
		String password = request.getParameter("pwd");
		String password1 = request.getParameter("pwd1");
		String password2 = request.getParameter("pwd2");

		Iterator<String> iter = request.getParameterMap().keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			System.out.println(key + " = " + request.getParameter(key));
		}

		System.out.println("ProfileServlet");
		HttpSession session = request.getSession(false);
		if(session != null) {
			if(password1.equals(password2)) {
				String userId = session.getAttribute("userId").toString();
				int idUser = Integer.parseInt(userId);
				User u = ConnectDB.getUser(idUser);
				Encryptor encr = new Encryptor();
				if(u != null && encr.decrypt(u.getPassword()).trim().equals(password)) {
					if(!ConnectDB.updateUser(idUser, name, login, password1, email)) sendMessage(response, ERROR_UPDATE);
					sendMessage(response, SUCCESS);
				}
			} else {
				sendMessage(response, ERROR_PASSWORD);
			}
		} else {
			sendMessage(response, ERROR_UPDATE);
		}
	}
	
	private void sendMessage(HttpServletResponse response, int messageId) {
		JsonObjectBuilder jsn = Json.createObjectBuilder();
		try(PrintWriter out = response.getWriter();) {
			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			switch(messageId) {
			case ERROR_UPDATE: jsn.add("message", "Update error, try again!"); break;
			case ERROR_PASSWORD: jsn.add("message", "Passwords are different!"); break;
			case SUCCESS: jsn.add("message", "success"); break;
			}
			jsn.add("messageId", messageId);

			out.println(jsn.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}