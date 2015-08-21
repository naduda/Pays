package nik.heatsupply.rest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import nik.heatsupply.db.ConnectDB;
import nik.heatsupply.socket.model.Data;

@Path("/db")
public class DataBase {
	private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	private JsonObjectBuilder j = Json.createObjectBuilder();
	
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{comand}")
	@GET
	public String getDataById(@PathParam("comand") String comand, 
			@QueryParam("params") String params) throws ParseException {

		String ret = "";
		switch (comand.toLowerCase()) {
		case "filesindir":
			JsonArrayBuilder jsn = Json.createArrayBuilder();
			File folder = getFileFromURL("/lang");
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				jsn.add(Json.createObjectBuilder().add("name", file.getName()));
			}
			ret = jsn.build().toString();
			break;
		case "getalldata":
			try {
				String[] pars = params.split(";");
				int idUser = Integer.parseInt(pars[0]);
				int idTarif = Integer.parseInt(pars[1]);
				List<Data> dataList = ConnectDB.getAllDataByUserTarif(idUser, idTarif);

				JsonArrayBuilder jArr = Json.createArrayBuilder();
				for(Data data : dataList) {
					j = Json.createObjectBuilder();
					j.add("dt", data == null ? "-": df.format(data.getDt()))
					 .add("idTarif", idTarif)
					 .add("idUser", idUser)
					 .add("value1", data == null ? "-": data.getValue1() + "")
					 .add("value2", data == null ? "-": data.getValue2() + "");
					jArr.add(j);
				}
				ret = jArr.build().toString();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		case "getdata":
			try {
				String[] pars = params.split(";");
				int idUser = Integer.parseInt(pars[0]);
				int idTarif = Integer.parseInt(pars[1]);
				Data data = ConnectDB.getDataByUserTarif(idUser, idTarif);

				j.add("dt", data == null ? "-": df.format(data.getDt()))
				 .add("idTarif", idTarif)
				 .add("idUser", idUser)
				 .add("value1", data == null ? "-": data.getValue1() + "")
				 .add("value2", data == null ? "-": data.getValue2() + "");
				ret = j.build().toString();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		case "setdata":
			try {
				String[] pars = params.split(";");
				int idUser = Integer.parseInt(pars[0]);
				int idTarif = Integer.parseInt(pars[1]);
				Date dtd = df.parse(pars[2]);
				Calendar c = Calendar.getInstance();
				c.setTime(df.parse(pars[2]));
				c.add(Calendar.MONTH, 1);
				Timestamp dt = new Timestamp(dtd.getTime());
				double val1 = Double.parseDouble(pars[3]);
				double val2 = Double.parseDouble(pars[4]);
				
				if(ConnectDB.addData(dt, idTarif, idUser, val1, val2))
					j.add("dt", df.format(dt))
						.add("value1", val1 + "")
						.add("value2", val2 + "")
						.add("dtEnd", df.format(c.getTime()));
			} catch (NumberFormatException e) {
				j.add("bad", "notok");
				e.printStackTrace();
			}
			ret = j.build().toString();
			break;
		case "deletedata":
			try {
				String[] pars = params.split(";");
				int idUser = Integer.parseInt(pars[0]);
				int idTarif = Integer.parseInt(pars[1]);
				Date dtd = df.parse(pars[2]);
				Timestamp dt = new Timestamp(dtd.getTime());
				
				if(ConnectDB.deleteData(idUser, idTarif, dt))
					j.add("remove", df.format(dt));
			} catch (NumberFormatException e) {
				j.add("bad", "notok");
				e.printStackTrace();
			}
			ret = j.build().toString();
			break;		
		default: ret = "Get: > Comand <" + comand + "> not found"; break;
		}
		return ret;
	}
	
	private File getFileFromURL(String path) {
		URL url = this.getClass().getClassLoader().getResource(path);
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}
		return file;
	}
}
