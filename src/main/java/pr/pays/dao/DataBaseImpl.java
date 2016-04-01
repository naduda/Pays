package pr.pays.dao;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import pr.pays.dao.mappers.IMapper;
import pr.pays.dao.mappers.IMapperCreate;
import pr.pays.model.User;

@Component("DataBaseImpl")
@SuppressWarnings("unchecked")
public class DataBaseImpl implements IDataBase {
	private static final Logger log = LoggerFactory.getLogger(DataBaseImpl.class);
	private DataSource dataSource;

	@Autowired
	private void setJdbcTemplate(JdbcTemplate jdbcTemplate) throws ParseException {
		dataSource = jdbcTemplate.getDataSource();
		
//		DROP TABLE user IF EXISTS;
//		DROP TABLE data IF EXISTS;
//		DROP TABLE profile IF EXISTS;
//		DROP TABLE tarif IF EXISTS;
//		DROP TABLE service IF EXISTS;
//		select * from user
//		select * from tarif
//		select * from service
		boolean isTableUserExist = (int)new BatisImpl(dataSource, 
				s -> s.getMapper(IMapperCreate.class).isDBexist("PAYS", "USER")).get() > 0;
		if(!isTableUserExist) {
			BCryptPasswordEncoder pe = new BCryptPasswordEncoder(12);
			
			IBatis[] queries = new IBatis[17];
			queries[0] = s -> s.getMapper(IMapperCreate.class).createUserTable();
			queries[1] = s -> s.getMapper(IMapper.class).addUser(
					"q",pe.encode("qwe"),"Павло","Романович","Надуда","+380506666666","q@gmail.com",
					"вул. В.Інтернаціоналістів 16-Б, кв.13",1);
			queries[2] = s -> s.getMapper(IMapperCreate.class).createServiceTable();
			queries[3] = s -> s.getMapper(IMapper.class).insertService("water", 1);
			queries[4] = s -> s.getMapper(IMapper.class).insertService("gas", 2);
			queries[5] = s -> s.getMapper(IMapperCreate.class).createTarifTable();
			queries[6] = s -> s.getMapper(IMapper.class).insertTarif(1, null, "", 14.772);
			queries[7] = s -> s.getMapper(IMapper.class).insertTarif(2, null, "11,12,1,2,3", 3.6);
			queries[8] = s -> s.getMapper(IMapper.class).insertTarif(2, null, "4-10", 7.188);
			queries[9] = s -> s.getMapper(IMapperCreate.class).createDataTable();
			queries[10] = s -> s.getMapper(IMapperCreate.class).createProfileTable();
			queries[11] = s -> s.getMapper(IMapper.class).insertProfile(1, 1, "129443");
			queries[12] = s -> s.getMapper(IMapper.class).insertProfile(1, 2, "0800114010");
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			Date date1 = dateFormat.parse("11.02.2016");
			queries[13] = s -> s.getMapper(IMapper.class).insertData(1, new Timestamp(date1.getTime()), 235);
			Date date2 = dateFormat.parse("08.03.2016");
			queries[14] = s -> s.getMapper(IMapper.class).insertData(1, new Timestamp(date2.getTime()), 241);
			queries[15] = s -> s.getMapper(IMapper.class).insertData(2, new Timestamp(date1.getTime()), 3450);
			queries[16] = s -> s.getMapper(IMapper.class).insertData(2, new Timestamp(date2.getTime()), 3465);
			if(new BatisImpl(dataSource, queries).runCollection()) {
				log.info("***************   create table user".toUpperCase());
				log.info("***************   insert user (q / qwe)".toUpperCase());
				log.info("***************   create table service".toUpperCase());
				log.info("***************   insert service (1 / water / 1)".toUpperCase());
				log.info("***************   insert service (2 / gas / 2)".toUpperCase());
				log.info("***************   create table tarif".toUpperCase());
				log.info("***************   insert tarif (1 / null / 14.772)".toUpperCase());
				log.info("***************   insert tarif (2 / null / 3.60 / 11,12,1,2,3)".toUpperCase());
				log.info("***************   insert tarif (2 / null / 7.188 / 4-10)".toUpperCase());
				log.info("***************   create table data".toUpperCase());
				log.info("***************   create table profile".toUpperCase());
				log.info("***************   insert profile (1 / 1 / 129443)".toUpperCase());
				log.info("***************   insert profile (1 / 2 / 0800114010)".toUpperCase());
			}
		} else {
			log.info("***************   table user exist   ***************".toUpperCase());
		}
	}

	@Override
	public Integer getMaxUserId() {
		return (Integer) new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).getMaxUserId()).get();
	}
	
	@Override
	public String addUser(String login, String password, String phone, String email, 
			int languageid, String owneraccount, String meterId, int typeDevice, String lastcash) {

		return "0";
	}
	
	@Override
	public boolean deleteUser(int idUser) {
		return new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).deleteUser(idUser)).run();
	}
	
	@Override
	public boolean deleteUserFromDB(int idUser) {
		return new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).deleteUserFromDB(idUser)).run();
	}
	
	@Override
	public boolean updateUser(int idUser, String password, String phone, String email, int languageid,
							  String name, String middlename, String surname) {
		IBatis updateUser =  s -> s.getMapper(IMapper.class)
				.updateUser(idUser, password, phone, email, languageid, name, middlename, surname);
		
		return new BatisImpl(dataSource, updateUser).run();
	}
	
	@Override
	public boolean updateUserAttempts(int idUser, int attempts) {
		IBatis updateUser =  s -> s.getMapper(IMapper.class).updateUserAttempts(idUser, attempts);
		return new BatisImpl(dataSource, updateUser).run();
	}
	
	public boolean updateUser(User user) {
		IBatis updateUser =  s -> s.getMapper(IMapper.class)
				.updateUser(user.getId(), user.getPassword(), user.getPhone(), user.getEmail(), user.getLanguageid(),
							user.getName(), user.getMiddlename(), user.getSurname());
		
		return new BatisImpl(dataSource, updateUser).run();
	}
	
	@Override
	public User getUser(int idUser) {
		return (User) new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).getUser(idUser)).get();
	}
	
	public User getUserByLogin(String login) {
		return (User) new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).getUserByLogin(login)).get();
	}
	
	public User getUserByEmail(String email) {
		return (User) new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).getUserByEmail(email)).get();
	}
	
	public List<Map<String, Object>> getProfileByUserId(int iduser) {
		return (List<Map<String, Object>>) new BatisImpl(dataSource, 
				s -> s.getMapper(IMapper.class).getProfileByUserId(iduser)).get();
	}
	
	public List<Map<String, Object>> getTarif() {
		return (List<Map<String, Object>>) new BatisImpl(dataSource, 
				s -> s.getMapper(IMapper.class).getTarif()).get();
	}
	
	public boolean insertData(int idservice, Timestamp date, double value) {
		return new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).insertData(idservice, date, value)).run();
	}
	
	public Map<String, Object> getLastDataByIdService(int idservice) {
		return (Map<String, Object>) new BatisImpl(dataSource, 
				s -> s.getMapper(IMapper.class).getLastDataByIdService(idservice)).get();
	}
	
	public boolean insertTarif(int idservice, Timestamp dtend, String actualtime, double value) {
		return new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).insertTarif(idservice, dtend, actualtime, value)).run();
	}
	
	public boolean removeTarifByIdService(int idservice) {
		return new BatisImpl(dataSource, s -> s.getMapper(IMapper.class).removeTarifByIdService(idservice)).run();
	}
	
	public List<Map<String, Object>> getData(int idservice, Timestamp date) {
		return (List<Map<String, Object>>) new BatisImpl(dataSource, 
				s -> s.getMapper(IMapper.class).getData(idservice, date)).get();
	}
	
	public List<Map<String, Object>> getDataPeriod(int idservice, Timestamp dtBeg, Timestamp dtEnd) {
		return (List<Map<String, Object>>) new BatisImpl(dataSource, 
				s -> s.getMapper(IMapper.class).getDataPeriod(idservice, dtBeg, dtEnd)).get();
	}
	
	public Map<String, Object> getService(int idservice) {
		return (Map<String, Object>) new BatisImpl(dataSource, 
				s -> s.getMapper(IMapper.class).getService(idservice)).get();
	}
}