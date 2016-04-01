package pr.pays.dao.mappers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProvider {
	private static final Logger log = LoggerFactory.getLogger(MyProvider.class);

	public static String getAllFilteredAndSortedMeters(Map<String, Object> params) {
		String orderParams = params.get("orderParams") != null ? params.get("orderParams").toString() : "";
		if(orderParams.contains("select") ||
			orderParams.contains("update") ||
			orderParams.contains("delete")) {
			log.error("\n\n\n\t\tSQL injection\n\n\n".toUpperCase());
			orderParams = null;
		}
		if(orderParams != null) orderParams = orderParams.replace("serialnumber", "serialnumber2");

		String query = "select id, name, (CASE WHEN serialnumber~E'^\\\\d+$' THEN serialnumber::numeric ELSE 0 END) as serialnumber2, " +
				"serialnumber, ownername, owneraccount, sumfactor, system_type, con_state from meter " +
				"where cast(id as char(10)) like #{id} and name like #{name} and serialnumber like #{serialnumber} and " +
				"ownername like #{ownername} and owneraccount like #{owneraccount} and cast(sumfactor as char(10)) like #{sumfactor} and " +
				"system_type = any(#{system_type}::int[]) and cast(con_state as char(1)) like #{con_state} " +
				(orderParams != "" ? "order by " + orderParams + " " : "") +
				"limit #{limit} offset #{offset}";
		log.info(query);
		return query;
	}
}