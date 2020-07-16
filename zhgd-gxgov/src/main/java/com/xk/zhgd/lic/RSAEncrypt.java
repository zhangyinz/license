package com.xk.zhgd.lic;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {

	//公钥
	private final static String gongyao = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFGs2LhX3EsDBIwj49uzX86LbbVe7iV6Nv+7eVKmDRmPLqO8cHYgsPHtCNeWs22Xy4ggkm/9EP3oRF49WN3FmsBUFJzxPnaNYOFaVbKoXlTvZT/x4CGYCmP0MGtm/Z2X+luARE9ryxwwT3oIlHAXwnG0V0dATwwNdpxGYojsKYdQIDAQAB";
	//私钥
	private final static String siyao = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIUazYuFfcSwMEjCPj27NfzotttV7uJXo2/7t5UqYNGY8uo7xwdiCw8e0I15azbZfLiCCSb/0Q/ehEXj1Y3cWawFQUnPE+do1g4VpVsqheVO9lP/HgIZgKY/Qwa2b9nZf6W4BET2vLHDBPegiUcBfCcbRXR0BPDA12nEZiiOwph1AgMBAAECgYAiF5ViERT/lekoGah7g0H2v1xmnYIMM0pHWn/REwOta7F8UUdPGsueWdQtCFZvvBD36UaS2J8ho2rZw9tk9HV6+N7OWAra1LtYm0G1OdDCC2g106yk6NkBSAhBefoQhL7L/mWyKNA6Smux0wCX7FrdlYpCg5HcFX9JoMD4vh+F2QJBAPs8EZG2MnY+4A3PwqeVas+XYIPRYPjwvRKlhsxkLNUhIGfaC9HF7q3WQDZtJyHJkHoJCdB3aXdY0wPyLSpkNbMCQQCHoSAZmZFzNo+QyCMeOSp9Qy0Cag329ZIbW77lggO4haxtsfo2OM/Kwj++/U3Dp5QH7/K9WfxsTnCvZL0m7zU3AkEAz41afmRk3+cltoNTLptz+Df41i2dzUslCEiTSjvgBdsuKF2ssgqxGhAMUGpwj6sznn3lC8H68UwCWBo0UfcjnQJAXM6fdYUE506lbI/Wn/7EGVTXPritd5jQcpIUDV4rB5/IvoMz0NiNL5Q+JIWWKdTI0amac4FY3g/KwenwGO1EfwJANQY9A4MSknnhi5HsuOuXZNfrYZt43ixpomBCUNNqv0PdFmMCxht5o7tHTOyGllC5CFEAU3n2dFwqervVuTH98w==";
//	private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
	public static void main(String[] args) throws Exception {
		//生成公钥和私钥
		genKeyPair();
		//加密字符串
		String message = "BFEBFBFF000906EA:test:20200714000000";
		System.out.println("随机生成的公钥为:" + gongyao);
		System.out.println("随机生成的私钥为:" + siyao);
		String messageEn = encrypt(message,gongyao);
		System.out.println(message + "\t加密后的字符串为:" + messageEn);
		String messageDe = decrypt(messageEn,siyao);
		System.out.println("还原后的字符串为:" + messageDe);
	}

	/** 
	 * 随机生成密钥对 
	 * @throws NoSuchAlgorithmException 
	 */  
	public static void genKeyPair() throws NoSuchAlgorithmException {  
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象  
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");  
		// 初始化密钥对生成器，密钥大小为96-1024位  
		keyPairGen.initialize(1024,new SecureRandom());  
		// 生成一个密钥对，保存在keyPair中  
		KeyPair keyPair = keyPairGen.generateKeyPair();  
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥  
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥  
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));  
		// 得到私钥字符串  
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
		System.out.println("公钥："+publicKeyString);
		System.out.println("私钥："+privateKeyString);
		// 将公钥和私钥保存到Map
//		keyMap.put(0,publicKeyString);  //0表示公钥
//		keyMap.put(1,privateKeyString);  //1表示私钥
	}  
	/** 
	 * RSA公钥加密 
	 *  
	 * @param str 
	 *            加密字符串
	 * @param publicKey 
	 *            公钥 
	 * @return 密文 
	 * @throws Exception 
	 *             加密过程中的异常信息 
	 */  
	public static String encrypt( String str, String publicKey ) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		return outStr;
	}

	/** 
	 * RSA私钥解密
	 *  
	 * @param str 
	 *            加密字符串
	 * @param privateKey 
	 *            私钥 
	 * @return 铭文
	 * @throws Exception 
	 *             解密过程中的异常信息 
	 */  
	public static String decrypt(String str, String privateKey) throws Exception{
		//64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		//base64编码的私钥
		byte[] decoded = Base64.decodeBase64(privateKey);  
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));  
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}

}

