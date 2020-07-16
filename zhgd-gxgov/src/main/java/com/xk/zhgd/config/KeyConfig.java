package com.xk.zhgd.config;

public class KeyConfig {

    private static final String BASE_FILE_PATH = "D:/KeyStore";

    //私钥存放路径
    public static final String PRIVATE_KEY_FILE_PATH = BASE_FILE_PATH + "/privateKey.keystore";
    //公钥存放路径
    public static final String PUBLICKEY_FILE_PATH = BASE_FILE_PATH + "/publicKey.keystore";
    //Cer证书存放路径
    public static final String CER_FILE_PATH = BASE_FILE_PATH + "/publicCer.cer";


    //私钥别名
    public static final String PRIVATE_ALIAS = "hqprivateKey";
    //公钥别名
    public static final String PUBLIC_ALIAS = "hqpublicKey";
    //获取keystore所需的密码
    public static final String KEYSTORE_PASSWORD = "hq20140729";
    //获取私钥所需密码
    public static final String KEY_PASSWORD = "hq20140729";


}

