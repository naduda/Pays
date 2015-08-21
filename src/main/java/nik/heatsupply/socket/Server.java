package nik.heatsupply.socket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import nik.heatsupply.reports.Report;
import nik.heatsupply.socket.messages.CommandMessage;
import nik.heatsupply.socket.messages.Message;
import nik.heatsupply.socket.messages.coders.MessageDecoder;
import nik.heatsupply.socket.messages.coders.MessageEncoder;

@ServerEndpoint(value = "/socketServer", configurator = GetHttpSessionConfigurator.class,
encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class Server {
	private static final Map<Session, User> users = Collections.synchronizedMap(new HashMap<>());

	private void sender(Session ss) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			if(!ss.isOpen()) break;
			Iterator<Session> iter = users.keySet().iterator();
			while (iter.hasNext()) {
				Session s = (Session) iter.next();
				try {
					CommandMessage cm = new CommandMessage("user " + i);
					cm.setParameters("value", "test");
					s.getBasicRemote().sendObject(cm);
				} catch (Exception e) {
					break;
				}
			}
			Thread.sleep(1000);
		}
	}

	@OnOpen
	public void handlerOpen(Session session, EndpointConfig config) throws IOException, EncodeException {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

		session.setMaxIdleTimeout(1000 * httpSession.getMaxInactiveInterval());
		int userId = Integer.parseInt(httpSession.getAttribute("userId").toString());
		users.put(session, new User(userId, httpSession));
		System.out.println("Socket connected - " + session.getId());
		if(httpSession != null){
			CommandMessage cm = new CommandMessage("user");
			cm.setParameters("value", httpSession.getAttribute("user").toString());
			session.getBasicRemote().sendObject(cm);
			new Thread(() -> {try {sender(session);} catch (Exception e) {e.printStackTrace();}}).start();
		}
		new AutoCloseSession(session).start();
	}

	@OnMessage
	public void handlerMessage(final Session session, Message message) throws IOException, EncodeException {
		if (message.getType().equals(CommandMessage.class.getSimpleName())) {
			CommandMessage cm = (CommandMessage) message;
			switch (cm.getCommand().toLowerCase()) {
			case "getreport":
				String reportName = cm.getParameters().get("reportName");
				Report report = new Report();
				report.setParameter("CHERRY_IMG", "images/cherry.jpg");
				String reportContent = 
						new String(report.create("/reports/", reportName + ".jrxml", "HTML").toByteArray(), StandardCharsets.UTF_8);
				CommandMessage retMessage = new CommandMessage("reportHTML");
				retMessage.setParameters("content", reportContent);
				session.getBasicRemote().sendObject(retMessage);
				break;
			case "savereport":
				reportName = cm.getParameters().get("reportName");
				String ext = cm.getParameters().get("ext");

				report = new Report();
				report.setParameter("CHERRY_IMG", "images/cherry.jpg");
				ByteArrayOutputStream reportContent4Save = report.create("/reports/", reportName + ".jrxml", ext);
				File file = new File("d:/" + reportName + "_." + ext);
				try(FileOutputStream fos = new FileOutputStream(file);){
					if (!file.exists()) {
						file.createNewFile();
					}
					fos.write(reportContent4Save.toByteArray());
				} catch(Exception e){
					e.printStackTrace();
				}

				retMessage = new CommandMessage("report4Save");
				retMessage.setParameters("name", reportName + "." + ext.toLowerCase());
				session.getBasicRemote().sendBinary(ByteBuffer.wrap(reportContent4Save.toByteArray()));
				break;
			default:
				break;
			}
		}
	}

	@OnClose
	public void handlerClose(Session session, CloseReason closeReason) {
		users.remove(session);
		System.out.println("Socket disconnected - " + session.getId() + ".\n\t Reason = " + closeReason.getReasonPhrase());
	}
	
	@OnError
	public void handlerError(Session session, Throwable thr) {
		System.out.println("Error - " + thr.getMessage());
	}

	public static Map<Session, User> getUsers() {
		return users;
	}
	
	public static void clearUsers() {
		Iterator<Session> iter = users.keySet().iterator();
		while (iter.hasNext()) {
			Session session = (Session) iter.next();
			users.remove(session);
		}
	}
	
	private class AutoCloseSession extends Thread {
		private HttpSession httpSession;
		private Session session;

		public AutoCloseSession(Session session) {
			this.setName("AutoClose_Session");
			this.session = session;
			httpSession = users.get(session).getHttpSession();
		}

		@Override
		public void run() {
			try {
				boolean isWait = true;
				while(isWait && httpSession != null && session.isOpen() && !httpSession.isNew()) {
					long sessionTime = System.currentTimeMillis() - httpSession.getLastAccessedTime();
					isWait = sessionTime < (httpSession.getMaxInactiveInterval() - 1) * 1000;
					Thread.sleep(1000);
				}
				if(session.isOpen())
					session.close(new CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "HttpSession is closed"));
			} catch (InterruptedException | IOException e) {
				throw new Error(e);
			}
		}
	}
}