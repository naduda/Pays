package nik.heatsupply.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.User;

@WebServlet("/ProfileInfoServlet")
public class ProfileInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try (PrintWriter out = response.getWriter();) {
			JsonObjectBuilder jsn = Json.createObjectBuilder();
			HttpSession session = request.getSession(false);

			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			if(session != null && Boolean.parseBoolean(session.getAttribute("login").toString())) {
				String userId = session.getAttribute("userId").toString();
				User u = ConnectDB.getUser(Integer.parseInt(userId));
				if(u != null) {
					jsn.add("userId", u.getIdUser())
						.add("login", u.getLogin())
						.add("name", u.getUserName())
						.add("email", u.getEmail())
						.add("address", u.getAddress())
						.add("ownerAccount1", u.getOwneraccount1())
						.add("ownerAccount2", u.getOwneraccount2());
				}
			} else {
				jsn.add("loginBad", "");
			}
			out.println(jsn.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}