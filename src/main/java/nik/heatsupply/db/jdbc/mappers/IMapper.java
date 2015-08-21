package nik.heatsupply.db.jdbc.mappers;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import nik.heatsupply.socket.model.Data;
import nik.heatsupply.socket.model.User;

public interface IMapper {
//	==============================================================================
	void update(String query);
//	==============================================================================
	@Select("select * from users where idUser = #{iduser}")
	User getUser(@Param("iduser") int idUser);
	
	@Select("select * from users where login = #{login}")
	User getUserByLogin(@Param("login") String login);
	
	@Insert("insert into users " +
			"(idUser, userName, login, password, email, lasttime) values " +
			"((select coalesce(max(iduser), 0) + 1 from users), #{userName}, #{login}, #{password}, #{email}, now())")
	Integer addUser(@Param("userName")String userName, @Param("login")String login, 
			@Param("password")String password, @Param("email") String email);
	
	@Update("update users set " +
			"userName = #{userName}, login = #{login}, password = #{password}, email = #{email}, lasttime = now() " +
			"where iduser = #{iduser}")
	Integer updateUser(@Param("iduser") int idUser, @Param("userName")String userName, @Param("login")String login, 
			@Param("password")String password, @Param("email") String email);
	
	@Select("select * from data where iduser = #{iduser} and idtarif = #{idtarif} order by dt desc limit 1")
	Data getDataByUserTarif(@Param("iduser") int idUser, @Param("idtarif") int idTarif);
	
	@Delete("delete from data where iduser = #{iduser} and idtarif = #{idtarif} and dt = #{dt}")
	Integer deleteData(@Param("iduser")int idUser, @Param("idtarif")int idTarif, @Param("dt")Timestamp dt);
	
	@Select("select * from data where iduser = #{iduser} and idtarif = #{idtarif} order by dt desc")
	List<Data> getAllDataByUserTarif(@Param("iduser") int idUser, @Param("idtarif") int idTarif);
	
	@Insert("insert into data " +
			"(dt, idtarif, iduser, value1, value2) values " +
			"(#{dt}, #{idtarif}, #{iduser}, #{value1}, #{value2})")
	Integer addData(@Param("dt")Timestamp dt, @Param("idtarif")int idTarif, 
			@Param("iduser")int idUser, @Param("value1")double value1, @Param("value2")double value2);
}