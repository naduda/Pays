package nik.heatsupply.db.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import nik.heatsupply.db.jdbc.mappers.IMapper;
import nik.heatsupply.socket.Server;

public class PostgresDB {
	private SqlSessionFactory sqlSessionFactory;
	private String connStr;
	
	public PostgresDB () {
		setMappers(Server.condb.getDataSource());
		
		try {
			Connection conn = Server.condb.getDataSource().getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			connStr = dbmd.getURL();
			connStr = connStr.substring(connStr.indexOf("://") + 3);
			String ip = connStr.substring(0, connStr.indexOf("/"));
			if(connStr.indexOf("?") > 0) {
				connStr = connStr.substring(connStr.indexOf("/") + 1);
				connStr = ip + "_" + connStr.substring(0, connStr.indexOf("?"));
			} else {
				connStr = connStr.replace("/", "_");
			}
		} catch (SQLException e) {}
	}
	
	private void setMappers(DataSource dataSource) {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(IMapper.class);
		//configuration.addMappers("jdbc.mappers");
		configuration.addMapper(BaseMapper.class);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	
	public String getConnStr() {
		return connStr;
	}

	//	=============================================================================================
	public static String getQuery(Map<String, Object> params) {
		return params.get("query").toString();
	}

	public interface BaseMapper {
		@SelectProvider(type = PostgresDB.class, method = "getQuery")
		void update(@Param("query") String query);
	}
}