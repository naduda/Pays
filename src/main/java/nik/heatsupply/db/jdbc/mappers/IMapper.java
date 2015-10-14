package nik.heatsupply.db.jdbc.mappers;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import nik.heatsupply.socket.model.Data;
import nik.heatsupply.socket.model.Tarif;
import nik.heatsupply.socket.model.User;

public interface IMapper {
	@Select("select * from users where idUser = #{iduser}")
	User getUser(@Param("iduser") int idUser);
	
	@Select("select * from users where login = #{login}")
	User getUserByLogin(@Param("login") String login);
	
	@Insert("insert into users " +
			"(idUser, userName, address, login, password, email, lasttime, owneraccount1, owneraccount2) values " +
			"((select coalesce(max(iduser), 0) + 1 from users), #{userName}, #{address}, #{login}, #{password}, #{email}, " +
			"now(), #{owneraccount1}, #{owneraccount2})")
	Integer addUser(@Param("userName")String userName, @Param("address")String address, @Param("login")String login, 
			@Param("password")String password, @Param("email")String email,
			@Param("owneraccount1")int ownerAccount1, @Param("owneraccount2")int ownerAccount2);
	
	@Update("update users set " +
			"userName = #{userName}, login = #{login}, password = #{password}, email = #{email}, lasttime = now(), " +
			"address = #{address}, owneraccount1 = #{owneraccount1}, owneraccount2 = #{owneraccount2} " +
			"where iduser = #{iduser}")
	Integer updateUser(@Param("iduser")int idUser, @Param("userName")String userName, @Param("login")String login, 
			@Param("password")String password, @Param("email")String email, @Param("address")String address,
			@Param("owneraccount1")int ownerAccount1, @Param("owneraccount2")int ownerAccount2);
	
	@Select("select * from data where iduser = #{iduser} and idtarif = #{idtarif} order by dt desc limit 1")
	Data getDataByUserTarif(@Param("iduser")int idUser, @Param("idtarif")int idTarif);
	
	@Select("select * from data " + 
			"where iduser = #{iduser} and idtarif = #{idtarif} and dt >= #{beg} and dt < #{end} " + 
			"order by dt desc limit 1")
	Data getMonthByUserTarif(@Param("iduser")int idUser, @Param("idtarif")int idTarif,
			@Param("beg")Timestamp beg, @Param("end")Timestamp end);
	
	@Delete("delete from data where iduser = #{iduser} and idtarif = #{idtarif} and dt = #{dt}")
	Integer deleteData(@Param("iduser")int idUser, @Param("idtarif")int idTarif, @Param("dt")Timestamp dt);
	
	@Select("select * from data where iduser = #{iduser} and idtarif = #{idtarif} order by dt desc")
	List<Data> getAllDataByUserTarif(@Param("iduser") int idUser, @Param("idtarif") int idTarif);
	
	@Insert("insert into data " +
			"(dt, idtarif, iduser, value1, value2) values " +
			"(#{dt}, #{idtarif}, #{iduser}, #{value1}, #{value2})")
	Integer addData(@Param("dt")Timestamp dt, @Param("idtarif")int idTarif, 
			@Param("iduser")int idUser, @Param("value1")double value1, @Param("value2")double value2);
	
	@Update("update data set " +
			"dt = #{dt}, value1 = #{value1}, value2 = #{value2} " +
			"where iduser = #{iduser} and idtarif = #{idtarif} and dt = #{olddt}")
	Integer updateData(@Param("dt")Timestamp dt, @Param("olddt")Timestamp oldDT, @Param("idtarif")int idTarif, 
			@Param("iduser")int idUser, @Param("value1")double value1, @Param("value2")double value2);
	
	@Select("select * from tarifs where idtarif = #{idtarif} and dt < now() order by dt desc limit 1")
	Tarif getLastTarif(@Param("idtarif")int idTarif);
	
	@Insert("insert into tarifs (dt, idtarif, tarif1, tarif2) values (#{dt}, #{idtarif}, #{tarif1}, #{tarif2})")
	Integer addTarif(@Param("dt")Timestamp dt, @Param("idtarif")int idTarif, 
			@Param("tarif1")double tarif1, @Param("tarif2")double tarif2);
}