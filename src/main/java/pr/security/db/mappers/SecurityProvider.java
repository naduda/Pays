package pr.security.db.mappers;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class SecurityProvider {
	private static final Logger log = LoggerFactory.getLogger(SecurityProvider.class);
	
	public String addUser(Map<String, Object> params) {
		Map<String, Object> user = (Map<String, Object>) params.get("user");
		String tableName = params.get("tableName").toString();
		String query = "INSERT INTO " + tableName + " (#{fields}) VALUES (";
		
		String keys = "";
		Iterator<String> iterator = user.keySet().iterator();
		while(iterator.hasNext()) {
			String field = iterator.next();
			keys += field + ", ";
			Object value = user.get(field);
			switch (value.getClass().getSimpleName()) {
			case "String":
			case "Timestamp":
				query += "'" + value + "', ";
				break;
			case "Long":
				query += "'" + (new Timestamp(Long.parseLong(value.toString()))) + "', ";
				break;
			case "Integer":
			case "Double":
			case "Boolean":
				query += value + ", ";
				break;
			default:
				log.error("Unknown class " + value.getClass().getSimpleName());
				break;
			}
		}
		keys = keys.substring(0, keys.length() - 2);
		query = query.substring(0, query.length() - 2);
		query += ");";
		query = query.replace("#{fields}", keys);
		log.debug("\n\n\n" + query + "\n\n\n");
		return query;
	}

	public String updateUser(Map<String, Object> params) {
		Map<String, Object> user = (Map<String, Object>) params.get("user");
		String tableName = params.get("tableName").toString();
		String query = "UPDATE " + tableName + " set ";
		
		String login = null;
		int id = 0;
		Iterator<String> iterator = user.keySet().iterator();
		while(iterator.hasNext()) {
			String field = iterator.next();
			if(field.toLowerCase().equals("login")) {
				login = user.get(field).toString();
			} else if(field.toLowerCase().equals("id")) {
				id = Integer.parseInt(user.get(field).toString());
			} else {
				Object value = user.get(field);
				switch (value.getClass().getSimpleName()) {
				case "String":
				case "Timestamp":
					query += field + " = '" + value + "', ";
					break;
				case "Long":
					query += field +  " = '" + (new Timestamp(Long.parseLong(value.toString()))) + "', ";
					break;
				case "Integer":
				case "Double":
				case "Boolean":
					query += field + " = " + value + ", ";
					break;
				default:
					log.error("Unknown class " + value.getClass().getSimpleName());
					break;
				}
			}
		}
		query = query.substring(0, query.length() - 2);
		query += " where login = '" + login + "'";
		if(id != 0) query +=  " and id = " + id;
		query += ";";
		log.debug("\n" + query);
		return query;
	}
}