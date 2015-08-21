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

@WebServlet("/StartServlet")
public class StartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try(PrintWriter out = response.getWriter();) {
			response.setContentType("text/html");
			response.setHeader("Cache-control", "no-cache, no-store");

			JsonObjectBuilder jsn = Json.createObjectBuilder();
			HttpSession session = request.getSession(false);

			if(session != null && Boolean.parseBoolean(session.getAttribute("login").toString())) {
				String user = session.getAttribute("user").toString();
				String userId = session.getAttribute("userId").toString();
				jsn.add("isLogin", "true")
				   .add("user", user)
				   .add("userId", userId);
			} else {
				jsn.add("isLogin", "false");
			}
			out.println(jsn.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}