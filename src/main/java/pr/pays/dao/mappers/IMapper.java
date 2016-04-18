package pr.pays.dao.mappers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IMapper {
	@Insert("insert into tarif (idservice,dtend,actualtime,value) "
			+ "values (#{idservice},#{dtend},#{actualtime},#{value});")
	boolean insertTarif(@Param("idservice") int idservice, @Param("dtend") Timestamp dtend, 
			@Param("actualtime") String actualtime, @Param("value") double value);
	
	@Delete("delete from tarif where dtend is null and idservice = #{idservice}")
	boolean removeTarifByIdService(@Param("idservice") int idservice);
	
	@Insert("insert into service (name,idtarif) values (#{name},#{idtarif});")
	boolean insertService(@Param("name") String name, @Param("idtarif") int idtarif);
	
	@Insert("insert into data (idservice,date,value) values (#{idservice},#{date},#{value});")
	boolean insertData(@Param("idservice") int idservice, @Param("date") Timestamp date, @Param("value") double value);
	
	@Select("select * from data where idservice = #{idservice} order by date desc limit 1")
	Map<String, Object> getLastDataByIdService(@Param("idservice") int idservice);
	
	@Insert("insert into profile (iduser,idservice,owneraccount) "
			+ "values (#{iduser},#{idservice},#{owneraccount});")
	boolean insertProfile(@Param("iduser") int iduser, @Param("idservice") int idservice,
			@Param("owneraccount") String owneraccount);
	
	@Select("select * from profile where iduser = #{iduser} order by idservice;")
	List<Map<String, Object>> getProfileByUserId(@Param("iduser") int iduser);
	
	@Select("select * from service where idservice = #{idservice};")
	Map<String, Object> getService(@Param("idservice") int idservice);
	
	@Select("select * from tarif where dtend is null;")
	List<Map<String, Object>> getTarif();
	
	@Select("select * from data where idservice = #{idservice} and date < #{date} order by date desc limit 1;")
	Map<String, Object> getData(@Param("idservice") int idservice, @Param("date") Timestamp date);
	
	@Select("select * from data where idservice = #{idservice} and "
			+ "date between #{dtBeg} and #{dtEnd} order by date desc;")
	List<Map<String, Object>> getDataPeriod(@Param("idservice") int idservice,
			@Param("dtBeg") Timestamp dtBeg, @Param("dtEnd") Timestamp dtEnd);
}