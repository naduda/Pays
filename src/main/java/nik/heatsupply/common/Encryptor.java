package nik.heatsupply.common;

import java.lang.reflect.Field;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encryptor {
	private final static byte[] key = new byte[32];;
	private final static byte[] iv = new byte[16];;

	public Encryptor() {
		try { 
			Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
			field.setAccessible(true);
			field.set(null, false); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		iv[0] = 29;
		iv[1] = 34;
		iv[2] = (byte) 203;
		iv[3] = 102;
		iv[4] = 49;
		iv[5] = 91;
		iv[6] = 126;
		iv[7] = 78;
		iv[8] = (byte) 242;
		iv[9] = 95;
		iv[10] = (byte) 167;
		iv[11] = (byte) 248;
		iv[12] = 35;
		iv[13] = (byte) 222;
		iv[14] = (byte) 193;
		iv[15] = (byte) 137;
		
		key[0] = 122;
		key[1] = (byte) 147;
		key[2] = 27;
		key[3] = 49;
		key[4] = (byte) 192;
		key[5] = (byte) 238;
		key[6] = (byte) 145;
		key[7] = 88;
		key[8] = (byte) 152;
		key[9] = 63;
		key[10] = 5;
		key[11] = 101;
		key[12] = 39;
		key[13] = (byte) 203;
		key[14] = (byte) 189;
		key[15] = 45;
		key[16] = (byte) 217;
		key[17] = 78;
		key[18] = 82;
		key[19] = (byte) 177;
		key[20] = 58;
		key[21] = 90;
		key[22] = 53;
		key[23] = 106;
		key[24] = (byte) 211;
		key[25] = 125;
		key[26] = 98;
		key[27] = 34;
		key[28] = 79;
		key[29] = (byte) 222;
		key[30] = 82;
		key[31] = 22;
	}
	
	public String encrypt(String key1, String key2, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

			SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String encrypt(byte[] key, byte[] iv, String value) {
		try {
			IvParameterSpec params = new IvParameterSpec(iv);

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, params);
			byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String encrypt(String value) {
		try {
			IvParameterSpec params = new IvParameterSpec(iv);

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, params);
			byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String decrypt(String key1, String key2, String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

			SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String decrypt(byte[] key, byte[] iv, String encrypted) {
		try {
			IvParameterSpec params = new IvParameterSpec(iv);

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, params);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String decrypt(String encrypted) {
		try {
			IvParameterSpec params = new IvParameterSpec(iv);

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, params);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Encryptor encryptor = new Encryptor();
		String str = "video    ";
		System.out.println(encryptor.encrypt(key, iv, str));
		
		System.out.println(encryptor.decrypt(key, iv, encryptor.encrypt(key, iv, str)));
		String en = "oOjGgbdVhjxi8VpMjqtWtA==";
		System.out.println(encryptor.decrypt(key, iv, en) + "|");
		
		System.out.println("==============");
		String test = "test";
		String tE = encryptor.encrypt(test);
		System.out.println(test + " - " + tE);
		System.out.println(encryptor.decrypt(tE));
	}
}
