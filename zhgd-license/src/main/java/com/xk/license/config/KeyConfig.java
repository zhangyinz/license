package com.xk.license.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class KeyConfig {

//    @Value("${jgt.lic.path}")
//    private static final String BASE_FILE_PATH = "";

    //私钥存放路径
//    public static final String PRIVATE_KEY_FILE_PATH = BASE_FILE_PATH + "/privateKey.keystore";
    //公钥存放路径
    @Value("${jgt.lic.public}")
    public String PUBLICKEY_FILE_PATH ;

    //私钥别名
//    public static final String PRIVATE_ALIAS = "hqprivateKey";
    //公钥别名
    public static final String PUBLIC_ALIAS = "hqpublicKey";
    //获取keystore所需的密码
    public static final String KEYSTORE_PASSWORD = "hq20140729";
    //获取私钥所需密码
//    public static final String KEY_PASSWORD = "hq20140729";

    @Value("${jgt.lic.path}")
    public String LICENSE_FILE ;

    public static final String SUB_PRJ = "hqwl_zhgd";


}

