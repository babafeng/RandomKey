package babafeng;
import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
* @author babafeng
* @date : 2016年12月28日 下午2:09:05
*/
public class AESCrypto {
	private static final String SHA_MODE = "SHA-1";
	private static final String AES_MODE = "AES/CBC/PKCS5Padding";
	private static final byte[] ivBytes = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x00, 0x14, 0x15};
	
	public static SecretKeySpec initKey(String mkey) {
		byte[] key;
		MessageDigest sha = null;
		SecretKeySpec secretKey = null;
		try {
			key = mkey.getBytes("UTF-8");

			sha = MessageDigest.getInstance(SHA_MODE);

			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);

			secretKey = new SecretKeySpec(key, "AES");
			return secretKey;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return secretKey;
	}

	public static String encrypt(String key, String message) {
		String encode = null;
		try {
			SecretKeySpec secretKey = AESCrypto.initKey(key);
			Cipher cipher = Cipher.getInstance(AES_MODE);
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			 
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
			encode = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
			return encode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encode;
	}

	public static String decrypt(String key, String enMessage) {
		String message = null;
		try {
			SecretKeySpec secretKey = AESCrypto.initKey(key);
			Cipher cipher = Cipher.getInstance(AES_MODE);
			
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
			
			message = new String(cipher.doFinal(Base64.getDecoder().decode(enMessage)));
			return message;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
}
