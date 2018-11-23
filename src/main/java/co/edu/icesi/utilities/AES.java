package co.edu.icesi.utilities;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	private static final IvParameterSpec iv = new IvParameterSpec(hexToBytes("00000000000000000000000000000000"));

	/**
	 * Encripta el texto escrito
	 *
	 * @param textoPlano
	 *            El texto a encriptar
	 * @return mensaje encriptado
	 */
	public static byte[] encriptar(byte[] textoPlano, byte[] llaveSecreta) throws Exception {
		Key SsecretKey = new SecretKeySpec(llaveSecreta, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, SsecretKey,iv);
		return cipher.doFinal(textoPlano);
	}

	/**
	 * Desencripta el mensaje recibido.
	 *
	 * @param mensajeCifrado
	 *            El mensaje que ha sido cifrado con anterioridad.
	 * @return retorna el mensaje descifrado, es decir, el original en texto plano.
	 */
	public static String desencriptar(byte[] mensajeCifrado, byte[] llaveSecreta) throws Exception {
		SecretKeySpec SsecretKey = new SecretKeySpec(llaveSecreta, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, SsecretKey,iv);
		return new String(cipher.doFinal(mensajeCifrado));
	}

	public static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
			}
			return buffer;
		}

	}
	
}