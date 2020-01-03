package babafeng;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author babafeng
 * @date : 2016年12月28日 下午2:34:35
 */
public class SocketServer {
	private static final String MODULUS = "123922356621773892093290279002883432724294869090195479756888583193788713815017178049839844595149749802113856496772314283819876699596170658659825021554476288066087009817109373394420242349885777384281101354176289954207605455724337655644518451156223477355327320779930137961364056252146197709014891936102776300129";
	private static final String PRIVATEEXPONENT = "107795073108230252230866414779382314307133403986967116437748208656354532510894598261520513003040983516470188308405879010208027997781077817246430334448900360981169201378007078007708763289047881290160768075448608428012788134216632554529206658989420142124737071729079580924404442297508331080658448917364217491457";
	private static String randomKey = "";
	private static String key = "";

	public static void main(String[] args) throws IOException {

		runserver(65534);
	}

	@SuppressWarnings("resource")
	private static void runserver(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Server is started:" + new Date());

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						// read from client
						DataInputStream datainputstream = new DataInputStream(socket.getInputStream());

						String fromClientMessage = datainputstream.readUTF();

						String enReturnMessage = responseCheck(fromClientMessage);

						// write to client
						DataOutputStream dataoutputstream = new DataOutputStream(socket.getOutputStream());
						dataoutputstream.writeUTF(enReturnMessage);

						datainputstream.close();
						dataoutputstream.close();
						socket.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

	}

	private static String responseCheck(String message) {
		System.out.println("收到客户端消息: " + message);

		String fromClientMessage = message;
		
		if (message.equals("getRandomKey") ){
			// 生成随机密钥
			randomKey = genRandomKey(); 
			String rsaEnKey = RSAEncrypt(randomKey);

			key = randomKey;
			System.out.println("获得一个随机密钥: " + key);
			return rsaEnKey;
		}
		else{
			System.out.println("本轮随机密钥: " + key);
			String defromClientMessage = AESDecrypt(key, fromClientMessage);
			System.out.println("客户端消息解密后: " + defromClientMessage);
			
			// 生成新的随机密钥
			randomKey = genRandomKey(); 
			String rsaEnKey = RSAEncrypt(randomKey);
			
			// 携带着rsa加密的随机密钥， 回复客户端
			String enReturnMessage = AESEncrypt(key, "return:I'm Accept Message: " + defromClientMessage + "&randomkey:" + rsaEnKey);
			
			// 将新生成的随机密钥作为下一轮加解密密钥
			key = randomKey;
			System.out.println("下一轮的随机密钥: " + key);
			
		
			return enReturnMessage;
		}
	}

	private static String genRandomKey() {
		String key = RandomKey.getRandomKey();
		return key;
	}

	private static String RSAEncrypt(String message) {
		String enMessage = null;

		try {
			RSAPrivateKey privatekey = RSACrypto.getPrivateKey(MODULUS, PRIVATEEXPONENT);
			enMessage = RSACrypto.encrypt(privatekey, message);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return enMessage;
	}

	private static String AESEncrypt(String key, String message) {
		System.out.println("AESEncrypt key: " + key);
		String enMessage = AESCrypto.encrypt(key, message);
		return enMessage;
	}

	private static String AESDecrypt(String key, String message) {
		System.out.println("AESDecrypt key: " + key);
		String deMessage = AESCrypto.decrypt(key, message);
		return deMessage;
	}

}
