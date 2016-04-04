package pr.pays.rest;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pr.pays.dao.DataBaseImpl;
import pr.pays.model.User;
import pr.pays.reports.Report;

@RestController
@RequestMapping("/secureresources")
public class SecureResources {
	private static final Logger log = LoggerFactory.getLogger(SecureResources.class);
	@Resource(name="DataBaseImpl") 
	private DataBaseImpl dao;

	private User getCurrentUser() {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		return dao.getUserByLogin(userName);
	}
	
	@RequestMapping(value="/profileInfo/{login}", method=RequestMethod.GET)
	public Map<String, Object> profileInfo(@PathVariable String login) throws SQLException {
		return profileInfoRez(login);
	}
	
	@RequestMapping(value="/report", method=RequestMethod.GET)
	public @ResponseBody byte[] getReport(HttpServletRequest request) {
		try {
			Report report = new Report();
			User user = getCurrentUser();
			List<Map<String, Object>> profile = dao.getProfileByUserId(user.getId());
			int year = Integer.parseInt(request.getParameter("year"));
			int month = Integer.parseInt(request.getParameter("month"));
			LocalDate date = LocalDate.of(year, month + 1, 1).minusDays(1);

			report.setParameter("prCurMonth", "За " + Common4rest.getCurrentMonth(date.getMonthValue() - 1) + 
					" " + date.getYear() + " p.");
			report.setParameter("prName", Common4rest.nameAddress(user));
			
			profile.forEach(p -> {
				try {
					int idService = Integer.parseInt(p.get("IDSERVICE").toString());
					report.setParameter("prOwnerAccount_" + idService,  p.get("OWNERACCOUNT").toString());
					double tarif = Common4rest.getTarif(dao.getTarif(), date.getMonthValue(), idService);
					report.setParameter("prTarif_" + idService, tarif);
					Map<String, Object> dataBeg = dao.getData(idService, Timestamp.valueOf(date.minusMonths(1).atStartOfDay()));
					Map<String, Object> dataEnd = dao.getData(idService, Timestamp.valueOf(date.atTime(23, 59)));
					if(dataBeg.size() > 0 && dataEnd.size() > 0) {
						report.setParameter("prDataEnd_" + idService, dataEnd.get("VALUE"));
						report.setParameter("prDataBeg_" + idService, dataBeg.get("VALUE"));
					} else {
						log.warn("Data is empty in report " + dataBeg.size());
					}
				} catch (Exception e) {
					log.error("Report cann't create");
					e.printStackTrace();
				}
			});
			return  report.create("./static/reports", "Water", "pdf").toByteArray();
		} catch (Exception e) {
			log.warn("Report cann't create");
		}
		log.info("test");
		return null;
	}
	
	@RequestMapping(value="/lastdata", method=RequestMethod.GET)
	public Map<String, Object> getLastData() {
		Map<String, Object> ret = new HashMap<>();
		User user = getCurrentUser();
		List<Map<String, Object>> profile = dao.getProfileByUserId(user.getId());
		profile.forEach(p -> {
			int idService = Integer.parseInt(p.get("IDSERVICE").toString());
			Map<String, Object> d = dao.getLastDataByIdService(idService);
			ret.put(idService + "", d);
		});
		return ret;
	}
	
	@RequestMapping(value="/dataperiod", method=RequestMethod.GET)
	public Map<String, Object> getDataPeriod(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		long lDtBeg = Long.parseLong(request.getParameter("dtBeg"));
		long lDtEnd = Long.parseLong(request.getParameter("dtEnd"));

		dao.getDataPeriod(Integer.parseInt(request.getParameter("idservice")),
				new Timestamp(lDtBeg), new Timestamp(lDtEnd))
		.forEach(d -> {
			ret.put(((Timestamp)d.get("DATE")).getTime() + "", d.get("VALUE"));
		});
		return ret;
	}
	
	@RequestMapping(value="/services", method=RequestMethod.GET)
	public Map<String, Object> getServices() {
		Map<String, Object> ret = new HashMap<>();
		User u = getCurrentUser();
		dao.getProfileByUserId(u.getId()).stream()
			.forEach(p -> {
				int idservice = Integer.parseInt(p.get("IDSERVICE").toString());
				String name = dao.getService(idservice).get("NAME").toString();
				ret.put(name, idservice);
			});
		return ret;
	}
	
	@RequestMapping(value="/tarifs", method=RequestMethod.GET)
	public Map<String, Object> getTarifs() {
		Map<String, Object> ret = new HashMap<>();
		User u = getCurrentUser();
		List<Object> services = dao.getProfileByUserId(u.getId()).stream()
		.map(p -> p.get("IDSERVICE")).collect(Collectors.toList());
		dao.getTarif().stream().filter(f -> {
			return services.stream().filter(s -> s.equals(f.get("IDSERVICE"))).findFirst().isPresent(); 
		}).forEach(t -> {
			Map<String, Object> s = new HashMap<>();
			s.put("actualtime", t.get("ACTUALTIME"));
			s.put("idservice", t.get("IDSERVICE"));
			s.put("value", t.get("VALUE"));
			ret.put(t.get("IDSERVICE") + "_" + ret.size(), s);
		});
		return ret;
	}
	
	@RequestMapping(value="/changetarifs", method=RequestMethod.POST)
	public Map<String, Object> changeTarifs(@RequestBody List<Map<String, Object>> tarif) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if(tarif.size() > 0) 
				dao.removeTarifByIdService(Integer.parseInt(tarif.get(0).get("idservice").toString()));
			tarif.forEach(t -> {
				dao.insertTarif(Integer.parseInt(t.get("idservice").toString()), null,
						t.get("actualtime").toString(), Double.parseDouble(t.get("value").toString()));
			});
			model.put("result", "OK");
			return model;
		} catch (NumberFormatException e) {
			log.error("Error in edit tarifs");
		}
		model.put("result", "BAD");
		return model;
	}
	
	@RequestMapping(value="/adddata", method=RequestMethod.POST)
	public Map<String, Object> addData(@RequestBody Map<String, Object> data) {
		Map<String, Object> model = new HashMap<String, Object>();
		int idService = Integer.parseInt(data.get("idservice").toString());
		if(dao.insertData(idService, new Timestamp(Long.parseLong(data.get("DATE").toString())), 
				Double.parseDouble(data.get("VALUE").toString()))) {
			model.put("result", "OK");
			return model;
		}
		model.put("result", "BAD");
		return model;
	}
	
	@RequestMapping(value="/profileInfo", method=RequestMethod.GET)
	public Map<String, Object> profileInfo() throws SQLException {
		return profileInfoRez(null);
	}
	
	private Map<String, Object> profileInfoRez(String login) throws SQLException {
		Map<String, Object> model = new HashMap<>();
		User u = login == null ? getCurrentUser() : dao.getUserByLogin(login);
		model.put("email", u.getEmail());
		model.put("phone", u.getPhone());
		model.put("name", u.getName());
		model.put("middlename", u.getMiddlename());
		model.put("surname", u.getSurname());
		model.put("login", u.getLogin());
		log.info("@RequestMapping /profileInfo ");
		return model;
	}
	
	@RequestMapping(value="/updateProfile", method=RequestMethod.POST)
	public Map<String, Object> updateProfile(@RequestBody Map<String, Object> user) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			BCryptPasswordEncoder pe = new BCryptPasswordEncoder(12);
			User u = user.get("login") == null ? getCurrentUser() : dao.getUserByLogin(user.get("login").toString());
			if(pe.matches(user.get("password").toString(), u.getPassword())) {
				String langId = user.get("langId").toString();
				int idLang = 3;
				switch (langId.toLowerCase()) {
					case "en": idLang = 1; break;
					case "ru": idLang = 2; break;
					case "uk": idLang = 3; break;
				}
				log.info(user.get("middlename").toString());
				dao.updateUser(u.getId(), 
						user.get("password1") == null ? u.getPassword() : pe.encode(user.get("password1").toString()),
						user.get("phone").toString(), user.get("email").toString(), idLang,
						user.get("name").toString(), user.get("middlename").toString(), user.get("surname").toString());
				model.put("result", "success");
				return model;
			} else {
				model.put("result", "keyWrongPassword");
				return model;
			}
		} catch (Exception e) {
			model.put("result", "INTERNAL_SERVER_ERROR");
			return model;
		}
		
	}
	
	@RequestMapping(value="/removeProfile", method=RequestMethod.POST)
	public  Map<String, Object> removeProfile(@RequestBody Map<String, Object> user) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			BCryptPasswordEncoder pe = new BCryptPasswordEncoder(12);
			User u = user.get("login") == null ? getCurrentUser() : dao.getUserByLogin(user.get("login").toString());
			if(pe.matches(user.get("password").toString(), u.getPassword())) {
				if(dao.deleteUser(u.getId())) {
					model.put("result", "success");
					return model;
				} else {
					model.put("result", "keyTryAgain");
					return model;
				}
			} else {
				model.put("result", "keyWrongPassword");
				return model;
			}
		} catch (Exception e) {
			model.put("result", "INTERNAL_SERVER_ERROR");
			return model;
		}
	}
}