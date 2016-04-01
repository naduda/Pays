package pr.pays.dao.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IMapperCreate {
	@Select("SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_CATALOG = #{dbName} AND TABLE_NAME = #{tableName};")
	int isDBexist(@Param("dbName") String dbName, @Param("tableName") String tableName);
	
	@Select("CREATE TABLE IF NOT EXISTS user(id INT PRIMARY KEY auto_increment, login VARCHAR(50), "
			+ "password VARCHAR(100), name VARCHAR(50), middlename VARCHAR(50), surname VARCHAR(50), "
			+ "phone VARCHAR(50), email VARCHAR(50), address VARCHAR(200), languageid INT, active BOOLEAN, "
			+ "attempts INT, lastmodified TIMESTAMP, maxAttempts INT);")
	Object createUserTable();
	
	@Select("CREATE TABLE IF NOT EXISTS tarif(idservice INT, dtend TIMESTAMP, "
			+ "actualtime VARCHAR(50), value DOUBLE);")
	Object createTarifTable();
	
	@Select("CREATE TABLE IF NOT EXISTS service(idservice INT auto_increment, name VARCHAR(50), idtarif INT);")
	Object createServiceTable();
	
	@Select("CREATE TABLE IF NOT EXISTS data(idservice INT, date TIMESTAMP, value DOUBLE);")
	Object createDataTable();
	
	@Select("CREATE TABLE IF NOT EXISTS profile(iduser INT, idservice INT, owneraccount VARCHAR(50));")
	Object createProfileTable();
}