package pr.pays.dao.mappers;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface IMapperCreate {
	@Update("DROP TABLE PROFILE IF EXISTS;"
			+ "DROP TABLE SERVICE IF EXISTS;"
			+ "DROP TABLE TARIF IF EXISTS;"
			+ "DROP TABLE DATA IF EXISTS;")
	boolean dropDataBase();
	
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