package pr.pays.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("RestTools")
public class RestTools {
	private static final Logger log = LoggerFactory.getLogger(RestTools.class);
	
	public String getCurrentMonth(int month) {
		String ret = null;
		switch (month) {
		case 0: ret = "січень"; break;
		case 1: ret = "лютий"; break;
		case 2: ret = "березень"; break;
		case 3: ret = "квітень"; break;
		case 4: ret = "травень"; break;
		case 5: ret = "червень"; break;
		case 6: ret = "липень"; break;
		case 7: ret = "серпень"; break;
		case 8: ret = "вересень"; break;
		case 9: ret = "жовтень"; break;
		case 10: ret = "листопад"; break;
		case 11: ret = "грудень"; break;
		}
		return ret;
	}
	
	public double getTarif(List<Map<String, Object>> input, int month, int idservice) {
		double ret = 0;
		try {
			ret = (double) input.stream().filter(f -> {
				if ((int)f.get("IDSERVICE") != idservice) return false;
				String aTime = f.get("ACTUALTIME").toString();
				boolean isInDate = false;
				if(aTime.indexOf(",") > 0) {
					for(String s : aTime.split(",")) {
						if(Integer.parseInt(s) == month) {
							isInDate = true;
							break;
						}
					}
				} else if(aTime.indexOf("-") > 0) {
					String[] s = aTime.split("-");
					int beg = Integer.parseInt(s[0].trim());
					int end = Integer.parseInt(s[1].trim());
					isInDate = month >= beg && month <= end;
				} else isInDate = true;
				return isInDate;
			}).findFirst().get().get("VALUE");
		} catch (NumberFormatException e) {
			log.error("Cann't find actual tarif (month = " + month + ", idservice = " + idservice + ")");
		}
		
		return ret;
	}
	
	public String nameAddress(Map<String, Object> user) {
		Map<String, Object> u = new HashMap<>();
		user.keySet().forEach(k -> u.put(k.toLowerCase(), user.get(k)));
		return u.get("surname") + " " + u.get("name") + " " + u.get("middlename") +
				", " + u.get("address");
	}
}