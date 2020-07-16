package com.xk.license.license;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;

/**
 * @author zhangyz
 * @date 2020/7/15
 */
public class KeyTools {
    public static PublicKey publicKey;
    private static KeyStore getKeyStore(String keyStorePath, String password)
            throws Exception {
        FileInputStream is = new FileInputStream(keyStorePath);
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, password.toCharArray());
        is.close();
        return ks;

    }

    public static PublicKey getPublicKey(String keyStorePath, String alias, String storePass) throws Exception {
        KeyStore ks = getKeyStore(keyStorePath, storePass);
        PublicKey key = ks.getCertificate(alias).getPublicKey();
        publicKey = key;
        return key;
    }
}
