package pr.security.db.mappers;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IMapperCreate {
	@Select("SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_CATALOG = #{dbName} AND TABLE_NAME = #{tableName};")
	int isTableExist(@Param("dbName") String dbName, @Param("tableName") String tableName);
	
	@Select("CREATE TABLE IF NOT EXISTS ${tableName} (${fields});")
	Boolean createUserTable(@Param("tableName") String tableName, @Param("fields") String fields);
	
	@Delete("DROP TABLE ${tableName} IF EXISTS;")
	boolean removeTableUser(@Param("tableName") String tableName);
}