package nik.heatsupply.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class LocaleEdit {
	private static final String PATH = "d:/GIT/NiK/Pays/src/main/resources/lang/";
	private static final String en = "Language_en.properties";
	private static final String ru = "Language_ru.properties";
	private static final String ua = "Language_uk.properties";
	
	public static void main(String[] args) {
		String key = "kTarif";
		String valueEN = "Tarif";
		String valueRU = "Тариф";
		String valueUA = "Тариф";
		
		key = "\n" + key;
		append2File(en, key + "=" + valueEN);
		append2File(ru, key + "=" + valueRU);
		append2File(ua, key + "=" + valueUA);
		
		System.out.println("COMPLETE");
	}
	
	public static void append2File(String fileName, String text) {
		File file = new File(PATH + fileName);
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			if (!file.exists()) file.createNewFile();
			out.print(new String(text.getBytes(), StandardCharsets.UTF_8));
		}catch (IOException e) {
		}
	}
}