package nik.heatsupply.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
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

import nik.heatsupply.common.CommonTools;
import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.reports.Report;
import nik.heatsupply.socket.messages.CommandMessage;
import nik.heatsupply.socket.messages.Message;
import nik.heatsupply.socket.messages.coders.MessageDecoder;
import nik.heatsupply.socket.messages.coders.MessageEncoder;
import nik.heatsupply.socket.model.Data;
import nik.heatsupply.socket.model.Tarif;
import nik.heatsupply.socket.model.User;

@ServerEndpoint(value = "/socketServer", configurator = GetHttpSessionConfigurator.class,
encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class Server {
	private static final Map<Session, WebUser> users = Collections.synchronizedMap(new HashMap<>());

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
		users.put(session, new WebUser(userId, httpSession));
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
				String userId = cm.getParameters().get("idUser");
				String format = cm.getParameters().get("format").toLowerCase();
				Report report = new Report();
//				report.setParameter("CHERRY_IMG", "images/cherry.jpg");
				if(reportName.equals("Water")) {
					LocalDate dt = LocalDate.now();
					int idUser = Integer.parseInt(userId);
					User user = ConnectDB.getUser(idUser);
					Tarif t1 = ConnectDB.getLastTarif(1);
					Tarif t2 = ConnectDB.getLastTarif(2);
					report.setParameter("prName", user.getUserName() + ", " + user.getAddress());
					report.setParameter("prOwnerAccountWater", user.getOwneraccount1());
					report.setParameter("prOwnerAccountGas", user.getOwneraccount2());
					report.setParameter("prCurMonth", 
							String.format("За %s %s р.",CommonTools.getMonth(dt.minusMonths(1).getMonthValue()), dt.getYear()));
					report.setParameter("prTarifWater", t1 != null ? t1.getTarif1() : 0);
					report.setParameter("prTarifGas", t2 != null ? t2.getTarif1() : 0);
					
					Timestamp dtBeg = Timestamp.valueOf(dt.minusMonths(1).withDayOfMonth(1).atStartOfDay());
					Timestamp dtEnd = Timestamp.valueOf(dt.withDayOfMonth(1).atStartOfDay());
					Data monthData = ConnectDB.getMonthByUserTarif(idUser, 1, dtBeg, dtEnd);
					report.setParameter("prWaterBeg", monthData.getValue1());
					monthData = ConnectDB.getMonthByUserTarif(idUser, 1, dtEnd, null);
					report.setParameter("prWaterEnd", monthData.getValue1());
					
					monthData = ConnectDB.getMonthByUserTarif(idUser, 2, dtBeg, dtEnd);
					report.setParameter("prGasBeg", monthData.getValue1());
					monthData = ConnectDB.getMonthByUserTarif(idUser, 2, dtEnd, null);
					report.setParameter("prGasEnd", monthData.getValue1());
				}

				if(format.equals("html")) {
					String reportContent = 
							new String(report.create("/reports/", reportName + ".jrxml", "HTML").toByteArray(), StandardCharsets.UTF_8);
					CommandMessage retMessage = new CommandMessage("reportHTML");
					retMessage.setParameters("content", reportContent);
					session.getBasicRemote().sendObject(retMessage);
				} else {
					ByteArrayOutputStream reportContent4Save = report.create("/reports/", reportName + ".jrxml", format);
//					File file = new File("d:/" + reportName + "_." + format);
//					try(FileOutputStream fos = new FileOutputStream(file);){
//						if (!file.exists()) {
//							file.createNewFile();
//						}
//						fos.write(reportContent4Save.toByteArray());
//					} catch(Exception e){
//						e.printStackTrace();
//					}

					CommandMessage retMessage = new CommandMessage("report4Save");
					retMessage.setParameters("name", reportName + "." + format);
					session.getBasicRemote().sendBinary(ByteBuffer.wrap(reportContent4Save.toByteArray()));
				}
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

	public static Map<Session, WebUser> getUsers() {
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