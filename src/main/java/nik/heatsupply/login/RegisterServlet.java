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

import nik.heatsupply.common.CommonTools;
import nik.heatsupply.db.ConnectDB;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final int ERROR_LOGIN = 1;
	private final int ERROR_PASSWORD = 2;
	private final int SUCCESS = 3;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try {
			Iterator<String> iter = request.getParameterMap().keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				System.out.println(key + " = " + request.getParameter(key));
			}

			String userName = request.getParameter("name");
			String address = request.getParameter("address");
			String ownerAccount1 = request.getParameter("ownerAccount1");
			String ownerAccount2 = request.getParameter("ownerAccount2");
			String login = request.getParameter("login");
			String password = request.getParameter("password");
			String password2 = request.getParameter("password2");
			String email = request.getParameter("email");

			if(password.equals(password2)) {
				if(ConnectDB.addUser(userName, address, login, password, email, 
						CommonTools.toInt(ownerAccount1), CommonTools.toInt(ownerAccount2))) {
					sendMessage(response, SUCCESS);
				} else {
					sendMessage(response, ERROR_LOGIN);
				}
			} else {
				sendMessage(response, ERROR_PASSWORD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("#/");
		}
	}
	
	private void sendMessage(HttpServletResponse response, int messageId) {
		JsonObjectBuilder jsn = Json.createObjectBuilder();
		try(PrintWriter out = response.getWriter();) {
			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			switch(messageId) {
			case ERROR_LOGIN: jsn.add("message", "Change login please!"); break;
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