package com.xk.zhgd.lic;

import com.xk.zhgd.config.KeyConfig;
import de.schlichtherle.license.LicenseNotaryException;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;

public class KeyTools
{
    /**
     * 通过keystore获取private key
     *
     */
    public static PrivateKey getPrivateKey() {

        FileInputStream is = null;
        PrivateKey privateKey = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            is = new FileInputStream(new File(KeyConfig.PRIVATE_KEY_FILE_PATH));
            keyStore.load(is, KeyConfig.KEYSTORE_PASSWORD.toCharArray());
            privateKey = (PrivateKey) keyStore.getKey(KeyConfig.PRIVATE_ALIAS, KeyConfig.KEY_PASSWORD.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return privateKey;
    }
       /**
         * 获取公钥
         *
         * @param publicKey 公钥字符串
         * @return
         */
        public static PublicKey getPublicKey(String publicKey) throws Exception {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            return keyFactory.generatePublic(keySpec);
   }
}

