package nik.heatsupply.db;

import static org.junit.Assert.*;

import org.apache.tomcat.dbcp.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.tomcat.dbcp.dbcp2.datasources.SharedPoolDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import nik.heatsupply.socket.Server;

public class ConnectDBTest {
	private static ConnectDB condb;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SharedPoolDataSource tds = null;
		condb = new ConnectDB();
		try {
			DriverAdapterCPDS cpds = new DriverAdapterCPDS();
	        cpds.setDriver("org.postgresql.Driver");
	        String dbConnect = String.format("jdbc:postgresql://%s:%s/%s", "127.0.0.1", "5432", "pays");

	        cpds.setUrl(dbConnect);
	        cpds.setUser("postgres");
	        cpds.setPassword("110488ng");

	        tds = new SharedPoolDataSource();
	        tds.setConnectionPoolDataSource(cpds);
	        tds.setMaxTotal(20);
	        tds.setDefaultMaxWaitMillis(50);
	        tds.setValidationQuery("select 1");
	        tds.setDefaultTestOnBorrow(true); 
	        tds.setDefaultTestOnReturn(true);
	        tds.setDefaultTestWhileIdle(true);
		} catch(Exception e) {
			System.err.println("\n\n\t Error in create datasource");
		}
		condb.setDataSource(tds);
		Server.condb = condb;
	}

	@Test
	public void test() {
		boolean isOK = false;
		System.out.println("\n\n\tBegin " + condb.getUser(1).getLogin() +
							" (" + condb.getUser(1).getUserName() + ")\n\n\n");
		
		
		isOK = condb.getLastTarif(1) != null &&
				condb.getAllDataByUserTarif(1, 1).size() > 0;
		
		assertTrue(isOK);
		System.out.println("-------------------------------------------------------");
		System.out.println("---------------------- TEST DONE ----------------------");
		System.out.println("-------------------------------------------------------");
		//fail("Not yet implemented");
	}

}
