package pr.pays.dao;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import pr.dao.BatisBuilder;
import pr.dao.BatisImpl;
import pr.pays.dao.mappers.IMapper;
import pr.pays.dao.mappers.IMapperCreate;
import pr.security.db.SecureDatabaseAPI;
import pr.security.model.IUser;
import pr.security.model.User;

@Component("CreateDatabase")
public class CreateDatabase implements IMapperCreate {
	private static final Logger log = LoggerFactory.getLogger(CreateDatabase.class);

	private static final String FIRST_DATE = "08.03.2016";
	private static final String SECOND_DATE = "12.04.2016";
	
	private DataSource dataSource;
	private BatisImpl batis;
	@Resource(type = SecureDatabaseAPI.class)
	private SecureDatabaseAPI sdao;
	@Resource(type = DataBaseImpl.class)
	private DataBaseImpl dao;
	
	@Autowired
	private void setJdbcTemplate(JdbcTemplate jdbcTemplate) throws ParseException {
		dataSource = jdbcTemplate.getDataSource();
		
		log.debug("Create database");
		
		batis = new BatisBuilder(dataSource)
				.addMappers(IMapperCreate.class)
				.addMappers(IMapper.class).build();
		
		sdao.removeTableUser();
		dropDataBase();
		
		IUser user = new User("q", "qwe", "naduda.pr@gmail.com", 0);
		Map<String, Object> mapUser = user.toMap();
		List<Map<String, Object>> customFields = new ArrayList<>();
		customFields.add(createField("name", "Павло"));
		customFields.add(createField("middlename", "Романович"));
		customFields.add(createField("surname", "Надуда"));
		customFields.add(createField("address", "вул. В.Інтернаціоналістів 16-Б, кв.13"));
		
		mapUser.put("customFields", customFields);
		Boolean result = sdao.addUser(null, mapUser);
		log.info(result ? "User added." : "Something wrong!!!");
		
		createDataTable();
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		dao.insertData(1, new Timestamp(dateFormat.parse(FIRST_DATE).getTime()), 315);
		dao.insertData(1, new Timestamp(dateFormat.parse(SECOND_DATE).getTime()), 325);
		dao.insertData(2, new Timestamp(dateFormat.parse(FIRST_DATE).getTime()), 4150);
		dao.insertData(2, new Timestamp(dateFormat.parse(SECOND_DATE).getTime()), 4225);
		
		createProfileTable();
		dao.insertProfile(1, 1, "129443");
		dao.insertProfile(1, 2, "0800114010");
		
		createServiceTable();
		dao.insertService("water", 1);
		dao.insertService("gas", 2);
		
		createTarifTable();
		dao.insertTarif(1, null, "", 15.64);
		dao.insertTarif(2, null, "11,12,1,2,3", 3.6);
		dao.insertTarif(2, null, "4-10", 7.188);
	}
	
	private Map<String, Object> createField(String key, Object value) {
		Map<String, Object> m = new HashMap<>();
		m.put("name", key);
		m.put("type", value.getClass().getSimpleName());
		m.put("value", value);
		return m;
	}

	@Override
	public boolean dropDataBase() {
		return batis.setIBatis(s -> s.getMapper(IMapperCreate.class).dropDataBase()).run();
	}

	@Override
	public Object createTarifTable() {
		return batis.setIBatis(s -> s.getMapper(IMapperCreate.class).createTarifTable()).run(); 
	}

	@Override
	public Object createServiceTable() {
		return batis.setIBatis(s -> s.getMapper(IMapperCreate.class).createServiceTable()).run();
	}

	@Override
	public Object createDataTable() {
		return batis.setIBatis(s -> s.getMapper(IMapperCreate.class).createDataTable()).run();
	}

	@Override
	public Object createProfileTable() {
		return batis.setIBatis(s -> s.getMapper(IMapperCreate.class).createProfileTable()).run();
	}

}
