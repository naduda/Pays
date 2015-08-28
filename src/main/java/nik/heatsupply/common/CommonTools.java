package nik.heatsupply.common;

public class CommonTools {
	public static int toInt(String s) {
		try {return Integer.parseInt(s);} catch (Exception e) {return 0;}
	}
	
	public static String getMonth(int month) {
		String ret = "";
		switch (month) {
		case 1: ret = "січень"; break;
		case 2: ret = "лютий"; break;
		case 3: ret = "березень"; break;
		case 4: ret = "квітень"; break;
		case 5: ret = "травень"; break;
		case 6: ret = "червень"; break;
		case 7: ret = "липень"; break;
		case 8: ret = "серпень"; break;
		case 9: ret = "вересень"; break;
		case 10: ret = "жовтень"; break;
		case 11: ret = "листопад"; break;
		case 12: ret = "грудень"; break;
		default: ret = "_______"; break;
		}
		return ret;
	}
}