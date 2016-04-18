package pr.security.db;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ASecurityDatabaseAPI {
	private static final Logger log = LoggerFactory.getLogger(ASecurityDatabaseAPI.class);
	
	public String getUserFieldsString(Map<String, Object> user) {
		String fields = "id INT PRIMARY KEY auto_increment, login VARCHAR(50), " +
				"password VARCHAR(100), email VARCHAR(50), active BOOLEAN, " +
				"attempts INT, lastmodified TIMESTAMP, maxAttempts INT";
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> customFields = (List<Map<String, Object>>) user.get("customFields");
		if(customFields != null) {
			for (Map<String, Object> f : customFields) {
				String fieldType = f.get("type").toString().toUpperCase();
				String fieldName = f.get("name").toString();
				String fieldValue = f.get("value").toString();
				int dublicateField = 0;
				String originFieldName = fieldName;
				while(fields.indexOf(fieldName) > -1) {
					dublicateField++;
					fieldName = originFieldName + dublicateField;
				}
				switch (fieldType) {
				case "STRING":
					fields += ", " + fieldName + " VARCHAR(250)";
					user.put(fieldName, fieldValue);
					break;
				case "BOOLEAN":
					fields += ", " + fieldName + " BOOLEAN";
					user.put(fieldName, Boolean.parseBoolean(fieldValue));
					break;
				case "TIMESTAMP":
					fields += ", " + fieldName + " TIMESTAMP";
					user.put(fieldName, Timestamp.valueOf(LocalDateTime.now()));
					break;
				case "INTEGER":
					fields += ", " + fieldName + " INT";
					user.put(fieldName, Integer.parseInt(fieldValue));
					break;
				case "DOUBLE":
					fields += ", " + fieldName + " DOUBLE";
					user.put(fieldName, Double.parseDouble(fieldValue));
					break;
				default:
					log.info("Unknown type of field " + fieldType);
					break;
				}
			}
			user.remove("customFields");
			log.debug("=======================================");
			log.debug("\n" + fields + "\n");
			log.debug("=======================================");
		}
		return fields;
	}
}