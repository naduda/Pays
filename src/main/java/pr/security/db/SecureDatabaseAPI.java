package pr.security.db;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import pr.dao.BatisBuilder;
import pr.dao.BatisImpl;
import pr.dao.IBatis;
import pr.security.db.mappers.IMapper;
import pr.security.db.mappers.IMapperCreate;
import pr.security.model.User;

@SuppressWarnings("unchecked")
@Component("SecureDatabaseAPI")
public class SecureDatabaseAPI extends ASecurityDatabaseAPI implements IMapper {
	private static final Logger log = LoggerFactory.getLogger(SecureDatabaseAPI.class);
	
	private DataSource dataSource;
	private BatisImpl batis;
	@Value("${pr.security.databaseName}")
	private String dbName;
	@Value("${pr.security.userTableName}")
	private String tableName;
	@Value("${pr.security.block.attempts}")
	private int maxAttempts;
	@Value("${pr.security.cryptPasswordLength}")
	private int cryptPasswordLength;
	private BCryptPasswordEncoder pe;

	@Autowired
	private void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		dataSource = jdbcTemplate.getDataSource();
		pe = new BCryptPasswordEncoder(cryptPasswordLength);
		batis = new BatisBuilder(dataSource)
				.addMappers(IMapperCreate.class)
				.addMappers(IMapper.class)
				.build();
	}
	
	/**
	 * @param fields Fields of table (<b>databaseName</b> = ${pr.security.databaseName}, 
	 * <b>tableName</b> = ${pr.security.userTableName})
	 * @return Create table. If success return <b>true</b> else return <b>false</b>
	 */
	public Boolean createUserTable(String fields) {
		return batis.setIBatis(s -> s.getMapper(IMapperCreate.class).createUserTable(tableName, fields)).run();
	}
	
	/** 
	 * Remove table ${pr.security.userTableName}
	 * @return If success return true else return false
	 */
	public Boolean removeTableUser() {
		return batis.setIBatis(s -> s.getMapper(IMapperCreate.class).removeTableUser(tableName)).run();
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @return If table exist return true else return false
	 */
	public boolean isTableExist(String tableName){
		IBatis isExist = s -> s.getMapper(IMapperCreate.class).isTableExist(dbName.toUpperCase(), tableName);
		return (int)batis.setIBatis(isExist).get() > 0;
	}
	
	/** 
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @return Size of user's table
	 */
	@Override
	public int getUsersSize(String tableName) {
		return (int) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getUsersSize(tableName == null ? this.tableName : tableName)).get();
	}
	
	/** 
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param idUser Field id from user table
	 * @return Size of user's table
	 */
	@Override
	public Map<String, Object> getUserProfile(String tableName, int idUser) {
		return (Map<String, Object>) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getUserProfile(tableName == null ? this.tableName : tableName, idUser)).get();
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @return If table USER exist then return <b>table's fields</b> else return <b>null</b>
	 */
	@Override
	public Map<String, Object> getUserFields(String tableName) {
		Map<String, Object> ret = null;
		if(isTableExist(tableName == null ? this.tableName : tableName)) {
			ret = (Map<String, Object>) batis.setIBatis(s -> s.getMapper(IMapper.class)
					.getUserFields(tableName == null ? this.tableName : tableName)).get();
			if(ret == null) return ret;
			String[] privateFields = {"login", "email", "password"};
			String keyID = "";
			for(String k : ret.keySet()) {
				if(Stream.of(privateFields).anyMatch(x -> x.equals(k.toLowerCase()))) {
					ret.put(k, null);
				} else {
					Object value = ret.get(k);
					switch (value.getClass().getSimpleName()) {
					case "String":
						ret.put(k, "");
						break;
					case "Integer":
						ret.put(k, 0);
						break;
					case "Double":
						ret.put(k, 0.123456789);
						break;
					case "Boolean":
						ret.put(k, true);
						break;
					case "Timestamp":
						ret.put(k, Timestamp.valueOf(LocalDateTime.now()).getTime());
						break;
					default:
						log.error("Unknown class " + value.getClass().getSimpleName());
						break;
					}
				}
				if("id".equals(k.toLowerCase())) {
					keyID = k;
				}
			}
			ret.remove(keyID);
		} else {
			log.info("EMPTY");
		}
		return ret;
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param user 
	 * @return If success return true else return false
	 */
	@Override
	public Boolean addUser(String tableName, Map<String, Object> user) {
		user.put("password", pe.encode(user.get("password").toString()));
		user.put("maxattempts", maxAttempts);
		user.put("attempts", 0);
		user.put("active", true);
		user.put("lastmodified", Timestamp.valueOf(LocalDateTime.now()));
		if(!isTableExist(tableName)) {
			createUserTable(getUserFieldsString(user));
		}
		return batis.setIBatis(s -> s.getMapper(IMapper.class)
				.addUser(tableName == null ? this.tableName : tableName, user)).run();
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param login Field login from user table
	 * @return If user exist return User else return null
	 */
	@Override
	public User getUserByLogin(String tableName, String login) {
		return (User) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getUserByLogin(tableName == null ? this.tableName : tableName, login)).get();
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param email Field email from user table
	 * @return If user exist return User else return null
	 */
	@Override
	public User getUserByEmail(String tableName, String email) {
		return (User) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getUserByEmail(tableName == null ? this.tableName : tableName, email)).get();
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param idUser Field id from user table
	 * @param attempts Field attempts from user table
	 * @return If success return true else return false
	 */
	@Override
	public boolean updateUserAttempts(String tableName, int idUser, int attempts) {
		IBatis updateUser =  s -> s.getMapper(IMapper.class)
				.updateUserAttempts(tableName == null ? this.tableName : tableName, idUser, attempts);
		return batis.setIBatis(updateUser).run();
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param idUser Field id from user table
	 * @return If user exist return User else return null
	 */
	@Override
	public User getUser(String tableName, int idUser) {
		return (User) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getUser(tableName == null ? this.tableName : tableName, idUser)).get();
	}
	
	/**
	 * @param password simple password (For example 'qwe')
	 * @param dbPassword encoded password (For example '$2a$12$/eoCRNBJTcOoAOFKbwYgXOc4loMaughZFNzQ7j9Tu53ir3K5qv.mi')
	 * @return Return true if password is correct
	 */
	public boolean checkPassword(String password, String dbPassword) {
		return pe.matches(password, dbPassword);
	}
	
	/**
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param user 
	 * @return If success return true else return false
	 */
	@Override
	public Boolean updateUser(String tableName, Map<String, Object> user) {
		log.info("\n\n\n" + user.get("password").toString());
		user.put("password", pe.encode(user.get("password").toString()));
		user.remove("maxattempts");
		user.remove("attempts");
		user.remove("lastmodified");
		return batis.setIBatis(s -> s.getMapper(IMapper.class)
				.updateUser(tableName == null ? this.tableName : tableName, user)).run();
	}

	/**
	 * Remove user by id. If it is last user then remove table user
	 * @param tableName If null then tableName = ${pr.security.userTableName}
	 * @param id Field id in user table 
	 * @return If success return true else return false
	 */
	@Override
	public Boolean deleteUserFromDB(String tableName, int id) {
		return getUsersSize(null) == 1 ? removeTableUser() :
			batis.setIBatis(s -> s.getMapper(IMapper.class)
					.deleteUserFromDB(tableName == null ? this.tableName : tableName, id)).run();
	}
}