package nik.heatsupply.db;

import java.sql.Timestamp;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.jdbc.BatisJDBC;
import nik.heatsupply.db.jdbc.PostgresDB;
import nik.heatsupply.db.jdbc.mappers.IMapper;
import nik.heatsupply.socket.Server;
import nik.heatsupply.socket.model.Data;
import nik.heatsupply.socket.model.User;

public class ConnectDB {
	private static DataSource dsLocal;
	private static Context context = null;
	
	public ConnectDB() {
		System.out.println("create ConnectDB " + this.toString());
	}
	
	public static User getUser(int idUser) {
		return (User) new BatisJDBC(s -> s.getMapper(IMapper.class).getUser(idUser)).get();
	}
	
	public static User getUser(String userName) {
		return (User) new BatisJDBC(s -> s.getMapper(IMapper.class).getUserByLogin(userName)).get();
	}
	
	public static boolean addUser(String userName, String login, String password, String email) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);
		return new BatisJDBC(s -> s.getMapper(IMapper.class).addUser(userName, login, passwordE, email)).run();
	}
	
	public static boolean updateUser(int idUser, String userName, String login, String password, String email) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);
		return new BatisJDBC(s -> s.getMapper(IMapper.class).updateUser(idUser, userName, login, passwordE, email)).run();
	}
	
	public static Data getDataByUserTarif(int idUser, int idTarif) {
		return (Data) new BatisJDBC(s -> s.getMapper(IMapper.class).getDataByUserTarif(idUser, idTarif)).get();
	}
	
	@SuppressWarnings("unchecked")
	public static List<Data> getAllDataByUserTarif(int idUser, int idTarif) {
		return (List<Data>) new BatisJDBC(s -> s.getMapper(IMapper.class).getAllDataByUserTarif(idUser, idTarif)).get();
	}
	
	public static boolean addData(Timestamp dt, int idTarif, int idUser, double value1, double value2) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).addData(dt, idTarif, idUser, value1, value2)).run();
	}
	
	public static boolean deleteData(int idUser,int idTarif, Timestamp dt) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).deleteData(idUser, idTarif, dt)).run();
	}

	public static DataSource getDataSource() {
		if (dsLocal != null) return dsLocal;
		try {
			if (context == null) {
				context = new InitialContext();
			}
			dsLocal = (DataSource) context.lookup("localPayDS");
		} catch (Exception e) {
			try {
				dsLocal = (DataSource) context.lookup("java:comp/env/localPayDS");
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
			}
		}
		return dsLocal;
	}
	
	private static PostgresDB postgressDB;
	private static boolean isFirstStart = true;
	public static PostgresDB getPostgressDB() {
		if (postgressDB == null) {
			synchronized (ConnectDB.class) {
				if (!isFirstStart) Server.clearUsers();
				postgressDB = new PostgresDB();
				isFirstStart = false;
				System.out.println("New connection");
			}
		}
		return postgressDB;
	}
	
	public static void setPostgressDB(PostgresDB value) {
		postgressDB = value;
	}
}