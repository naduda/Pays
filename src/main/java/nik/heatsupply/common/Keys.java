package nik.heatsupply.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Keys {
	private static final String PATH = "d:/GIT/NiK/HeatSupply/src/main/resources/lang/";
	private static final String ru = "Language_ru.properties";

	public static void main(String[] args) {
		String filter = "emov";
		File file = new File(PATH + ru);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if(line.indexOf("=") > 0) {
//					line = line.substring(0, line.indexOf("="));
					if(line.indexOf(filter) > 0)
						System.out.println(line);
					if(filter.length() == 0)
						System.out.println(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}