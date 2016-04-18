package pr.pays.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

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

@Component("DataBaseImpl")
@SuppressWarnings("unchecked")
public class DataBaseImpl implements IMapper {
	private static final Logger log = LoggerFactory.getLogger(DataBaseImpl.class);

	private DataSource dataSource;
	private BatisImpl batis;

	@Autowired
	private void setJdbcTemplate(JdbcTemplate jdbcTemplate) throws ParseException {
		dataSource = jdbcTemplate.getDataSource();
		
		log.debug("Init DataBaseImpl");
		
		batis = new BatisBuilder(dataSource)
				.addMappers(IMapperCreate.class)
				.addMappers(IMapper.class).build();
	}
	
	@Override
	public List<Map<String, Object>> getTarif() {
		return (List<Map<String, Object>>) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getTarif()).get();
	}
	
	@Override
	public boolean insertData(int idservice, Timestamp date, double value) {
		return batis.setIBatis(s -> s.getMapper(IMapper.class)
				.insertData(idservice, date, value)).run();
	}
	
	@Override
	public Map<String, Object> getLastDataByIdService(int idservice) {
		return (Map<String, Object>) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getLastDataByIdService(idservice)).get();
	}
	
	@Override
	public boolean insertTarif(int idservice, Timestamp dtend, String actualtime, double value) {
		return batis.setIBatis(s -> s.getMapper(IMapper.class)
				.insertTarif(idservice, dtend, actualtime, value)).run();
	}
	
	@Override
	public boolean removeTarifByIdService(int idservice) {
		return batis.setIBatis(s -> s.getMapper(IMapper.class)
				.removeTarifByIdService(idservice)).run();
	}
	
	@Override
	public Map<String, Object> getData(int idservice, Timestamp date) {
		return (Map<String, Object>) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getData(idservice, date)).get();
	}
	
	@Override
	public List<Map<String, Object>> getDataPeriod(int idservice, Timestamp dtBeg, Timestamp dtEnd) {
		return (List<Map<String, Object>>) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getDataPeriod(idservice, dtBeg, dtEnd)).get();
	}
	
	@Override
	public Map<String, Object> getService(int idservice) {
		return (Map<String, Object>) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getService(idservice)).get();
	}

	@Override
	public boolean insertService(String name, int idtarif) {
		return batis.setIBatis(s -> s.getMapper(IMapper.class).insertService(name, idtarif)).run();
	}

	@Override
	public boolean insertProfile(int iduser, int idservice, String owneraccount) {
		return batis.setIBatis(s -> s.getMapper(IMapper.class)
				.insertProfile(iduser, idservice, owneraccount)).run();
	}

	@Override
	public List<Map<String, Object>> getProfileByUserId(int iduser) {
		return (List<Map<String, Object>>) batis.setIBatis(s -> s.getMapper(IMapper.class)
				.getProfileByUserId(iduser)).get();
	}
}