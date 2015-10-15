package nik.heatsupply.db;

import java.sql.Timestamp;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import nik.heatsupply.common.Encryptor;
import nik.heatsupply.db.jdbc.BatisJDBC;
import nik.heatsupply.db.jdbc.mappers.IMapper;
import nik.heatsupply.socket.model.Data;
import nik.heatsupply.socket.model.Tarif;
import nik.heatsupply.socket.model.User;

public class ConnectDB {
	private DataSource dsLocal;
	private SqlSessionFactory sqlSessionFactory;
	private Context context = null;
	
	public ConnectDB() {
		System.out.println("create ConnectDB " + this.toString());
	}
	
	private void setMappers(DataSource dataSource) {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(IMapper.class);
		//configuration.addMappers("jdbc.mappers");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	
	public User getUser(int idUser) {
		return (User) new BatisJDBC(s -> s.getMapper(IMapper.class).getUser(idUser)).get();
	}
	
	public User getUser(String userName) {
		return (User) new BatisJDBC(s -> s.getMapper(IMapper.class).getUserByLogin(userName)).get();
	}
	
	public boolean addUser(String userName, String address, String login, String password, String email, 
			int ownerAccount1, int ownerAccount2) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);
		return new BatisJDBC(s -> s.getMapper(IMapper.class)
				.addUser(userName, address, login, passwordE, email, ownerAccount1, ownerAccount2)).run();
	}
	
	public boolean updateUser(int idUser, String userName, String login, String password, String email,
									String address, int ownerAccount1, int ownerAccount2) {
		Encryptor encr = new Encryptor();
		while(password.length() < 12) password += " ";
		String passwordE = encr.encrypt(password);
		return new BatisJDBC(s -> s.getMapper(IMapper.class)
				.updateUser(idUser, userName, login, passwordE, email, address, ownerAccount1, ownerAccount2)).run();
	}
	
	public Data getDataByUserTarif(int idUser, int idTarif) {
		return (Data) new BatisJDBC(s -> s.getMapper(IMapper.class).getDataByUserTarif(idUser, idTarif)).get();
	}
	
	public Data getMonthByUserTarif(int idUser, int idTarif, Timestamp beg, Timestamp end) {
		Timestamp end2 = end == null ? new Timestamp(System.currentTimeMillis()) : end;
		return (Data) new BatisJDBC(s -> s.getMapper(IMapper.class).getMonthByUserTarif(idUser, idTarif, beg, end2)).get();
	}
	
	@SuppressWarnings("unchecked")
	public List<Data> getAllDataByUserTarif(int idUser, int idTarif) {
		return (List<Data>) new BatisJDBC(s -> s.getMapper(IMapper.class).getAllDataByUserTarif(idUser, idTarif)).get();
	}
	
	public boolean addData(Timestamp dt, int idTarif, int idUser, double value1, double value2) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).addData(dt, idTarif, idUser, value1, value2)).run();
	}
	
	public boolean updateData(Timestamp dt, Timestamp oldDT, int idTarif, int idUser, double value1, double value2) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).updateData(dt, oldDT, idTarif, idUser, value1, value2)).run();
	}
	
	public boolean deleteData(int idUser, int idTarif, Timestamp dt) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).deleteData(idUser, idTarif, dt)).run();
	}
	
	public Tarif getLastTarif(int idTarif) {
		return (Tarif) new BatisJDBC(s -> s.getMapper(IMapper.class).getLastTarif(idTarif)).get();
	}
	
	public boolean addTarif(Timestamp dt, int idTarif, double tarif1, double tarif2) {
		return new BatisJDBC(s -> s.getMapper(IMapper.class).addTarif(dt, idTarif, tarif1, tarif2)).run();
	}

	public DataSource getDataSource() {
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
		setMappers(dsLocal);
		return dsLocal;
	}
	
	public void setDataSource(DataSource ds) {
		if(dsLocal == null) {
			dsLocal = ds;
			setMappers(dsLocal);
		}
	}
}