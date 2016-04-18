package pr.dao;

import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatisImpl {
	private static final Logger log = LoggerFactory.getLogger(BatisImpl.class);
	private static final int MAX_REPET = 5;
	private IBatis iBatis;
	private IBatis[] iCollection;
	private int count;
	private boolean isCommit = true;
	private DataSource dataSource;
	private SqlSession session;
	private Configuration configuration;
	
	private SqlSessionFactory sqlSessionFactory;
	
	public BatisImpl(DataSource dataSource) {
		this.dataSource = dataSource;
		setConfiguration(dataSource);
	}
	
	private void clear() {
		this.iCollection = null;
		this.iBatis = null;
	}
	
	public BatisImpl setIBatis(IBatis iBatis) {
		clear();
		this.iBatis = iBatis;
		return this;
	}
	
	public BatisImpl setIBatis(IBatis[] iCollection) {
		clear();
		this.iCollection = iCollection;
		return this;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	private void setConfiguration(DataSource dataSource) {
		if(dataSource != null) {
			TransactionFactory transactionFactory = new JdbcTransactionFactory();
			Environment environment = new Environment("development", transactionFactory, dataSource);
			configuration = new Configuration(environment);
//			configuration.addMapper(IMapper.class);
//			configuration.addMapper(IMapperCreate.class);
			//configuration.addMappers("jdbc.mappers");
//			sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		}
	}

	public void setCommit(boolean isCommit) {
		this.isCommit = isCommit;
	}

	public Object get() {
		while (count < MAX_REPET) {
			session = null;
			try {
				if(dataSource != null) {
					session = sqlSessionFactory.openSession(isCommit);
					try {
						return iBatis.getResult(session);
					} catch (Exception e) {
						if(count == MAX_REPET - 1) {
							log.error(ExceptionUtils.getStackTrace(e));
							log.error("Bad connection!");
						}
					} finally {
						if (session != null) session.close();
					}
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
			count++;
		}
		return null;
	}
	
	public boolean run() {
		while (count < MAX_REPET) {
			session = null;
			try {
				if(dataSource != null) {
					session = sqlSessionFactory.openSession(isCommit);
					iBatis.getResult(session);
					return true;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				if(count == MAX_REPET - 1) log.error(ExceptionUtils.getStackTrace(e));
			} finally {
				if (session != null) session.close();
			}
			count++;
		}
		return false;
	}
	
	public boolean runCollection() {
		while (count < MAX_REPET) {
			session = null;
			try {
				if(dataSource != null) {
					session = sqlSessionFactory.openSession(false);
					for(int i = 0; i < iCollection.length; i++) {
						iBatis = iCollection[i];
						if(iBatis != null) {
							iBatis.getResult(session);
						}
					}
					session.commit();
					return true;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				if(session != null) session.rollback();
				if(count == MAX_REPET - 1) log.error(ExceptionUtils.getStackTrace(e));
			} finally {
				if (session != null) session.close();
			}
			count++;
		}
		return false;
	}
}