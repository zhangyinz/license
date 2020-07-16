package com.xk.zhgd.lic;
 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
 
import org.apache.commons.codec.binary.Base64;
 
//将私钥文件,公钥文件转换成PrivateKey,PublicKey对象或获取公私钥内容
//仅供参考，不提供性能,bug等问题保障
 
public class LoadCertUtil {
	public static PrivateKey getPriKey(KeyStore keyStore,String password){
		try {
			Enumeration<String> aliasenum  = keyStore.aliases();
			if (aliasenum.hasMoreElements()) {
				String keyAlias = aliasenum.nextElement();
				if (keyStore.isKeyEntry(keyAlias)) {
					PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias,password.toCharArray());
					return privateKey;
				}
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PublicKey getPubKey(KeyStore keyStore){
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
				if (keyStore.isKeyEntry(keyAlias)) {
					X509Certificate  x509Certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
					PublicKey publicKey = x509Certificate.getPublicKey();
					return publicKey;
				}
			}
		  } catch (KeyStoreException e) {
			  e.printStackTrace();
		  }
		return null;
	}
	
	
	public static KeyStore loadKeyStore(String pfxkeyfile,String password){
		System.out.println("加载签名证书==>" + pfxkeyfile);
		FileInputStream fis = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			fis = new FileInputStream(pfxkeyfile);
			char[] nPassword = password.toCharArray();
			if (null != keyStore) {
				keyStore.load(fis, nPassword);
			}
			return keyStore;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null!=fis)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public static PublicKey loadPubkey(String pubkeyfile){
		System.out.println("加载验签证书==>" + pubkeyfile);
		CertificateFactory cf = null;
		FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			in = new FileInputStream(pubkeyfile);
			X509Certificate validateCert = (X509Certificate) cf.generateCertificate(in);
			return validateCert.getPublicKey();
		}catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
 
	public static void main(String[] args) {
		String pfxkeyfile ="D:\\certs\\allcerts\\00010000new2048.pfx";
		String password = "000000";
		String pubkeyfile ="D:\\certs\\acp_test_verify_sign.cer";
		
		KeyStore keyStore = loadKeyStore(pfxkeyfile,password);
		PrivateKey privateKey = getPriKey(keyStore,password);
		System.out.println("签名私钥-私钥内容：" + Base64.encodeBase64String(privateKey.getEncoded()));
		PublicKey priPublicKey = getPubKey(keyStore);
		System.out.println("签名私钥-公钥内容："+Base64.encodeBase64String(priPublicKey.getEncoded()));
		
		PublicKey publicKey = loadPubkey(pubkeyfile);
		System.out.println("公钥内容："+Base64.encodeBase64String(publicKey.getEncoded()));
	}
}