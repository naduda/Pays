package pr.security.db.mappers;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import pr.security.model.User;

public interface IMapper {
	@Select("select * from ${tableName} limit 1;")
	Map<String, Object> getUserFields(@Param("tableName") String tableName);
	
	@Select("select count(*) from ${tableName};")
	int getUsersSize(@Param("tableName") String tableName);
	
	@Select("select * from ${tableName} where id = #{id};")
	Map<String, Object> getUserProfile(@Param("tableName") String tableName, @Param("id") int idUser);
	
	@SelectProvider(type=SecurityProvider.class, method="addUser")
	Boolean addUser(@Param("tableName") String tableName, @Param("user") Map<String, Object> user);
	
	@SelectProvider(type=SecurityProvider.class, method="updateUser")
	Boolean updateUser(@Param("tableName") String tableName, @Param("user") Map<String, Object> user);
	
	@Select("DELETE FROM ${tableName} where id = #{id};")
	Boolean deleteUserFromDB(@Param("tableName") String tableName, @Param("id")int id);
	
	@Select("select * from ${tableName} where login = #{login}")
	User getUserByLogin(@Param("tableName") String tableName, @Param("login")String login);
	
	@Select("select * from ${tableName} where email = #{email}")
	User getUserByEmail(@Param("tableName") String tableName, @Param("email")String email);
	
	@Select("select * from ${tableName} where id = #{id}")
	User getUser(@Param("tableName") String tableName, @Param("id")int idUser);
	
	@Update("update ${tableName} set " +
			"attempts = #{attempts}, lastModified = now() " +
			"where id = #{id}")
	boolean updateUserAttempts(@Param("tableName") String tableName, @Param("id") int idUser, @Param("attempts") int attempts);
}