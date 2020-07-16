package com.xk.zhgd.lic;

import com.xk.zhgd.config.KeyConfig;
import de.schlichtherle.license.*;

import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * License生成类
 *
 * @author zifangsky
 * @date 2018/4/19
 * @since 1.0.0
 */
public class LicenseCreator {
    private final static X500Principal DEFAULT_HOLDER_AND_ISSUER = new X500Principal("CN=mine,OU=test,O=test,L=gz,ST=gd,C=CN");

    public static void main(String[] args){
        LicenseCreator licenseCreator = new LicenseCreator();
        licenseCreator.generateLicense();
    }

    /**
     * 生成License证书
     * @author zifangsky
     * @date 2018/4/20 10:58
     * @since 1.0.0
     * @return boolean
     */
    public boolean generateLicense(){
        try {
            LicenseManager licenseManager = new CustomLicenseManager(initLicenseParam());
            LicenseContent licenseContent = initLicenseContent();
            licenseManager.store(licenseContent,new File("D:\\lic\\locasx.lic"));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 初始化证书生成参数
     * @author zifangsky
     * @date 2018/4/20 10:56
     * @since 1.0.0
     * @return de.schlichtherle.license.LicenseParam
     */
    private LicenseParam initLicenseParam() throws NoSuchPaddingException, NoSuchAlgorithmException {
        Preferences preferences = Preferences.userNodeForPackage(LicenseCreator.class);

        //设置对证书内容加密的秘钥
        CipherParam cipherParam = new DefaultCipherParam(KeyConfig.KEYSTORE_PASSWORD);

        KeyStoreParam privateStoreParam = new CustomKeyStoreParam(LicenseCreator.class
                ,KeyConfig.PRIVATE_KEY_FILE_PATH
                ,KeyConfig.PRIVATE_ALIAS
                ,KeyConfig.KEYSTORE_PASSWORD
                ,KeyConfig.KEY_PASSWORD);

        LicenseParam licenseParam = new DefaultLicenseParam("hqwl_zhgd"
                ,preferences
                ,privateStoreParam
                ,cipherParam);

        return licenseParam;
    }

    /**
     * 设置证书生成正文信息
     * @author zifangsky
     * @date 2018/4/20 10:57
     * @since 1.0.0
     * @return de.schlichtherle.license.LicenseContent
     */
    private LicenseContent initLicenseContent(){
        LicenseContent licenseContent = new LicenseContent();
        licenseContent.setHolder(DEFAULT_HOLDER_AND_ISSUER);
        licenseContent.setIssuer(DEFAULT_HOLDER_AND_ISSUER);

        licenseContent.setSubject("hqwl_zhgd");
        licenseContent.setIssued(new Date());
        licenseContent.setNotBefore(new Date());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 5);
        licenseContent.setNotAfter(c.getTime());
        licenseContent.setConsumerType("User");
        licenseContent.setConsumerAmount(1);
        LicenseCheckModel model = getServerInfos();
        licenseContent.setInfo("F1 06 04 00 FF FB 8B 0F");
        //扩展校验服务器硬件信息
        return licenseContent;
    }

    private LicenseCheckModel getServerInfos(){
        //操作系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        AbstractServerInfos abstractServerInfos = null;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        }else{//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }

        return abstractServerInfos.getServerInfos();
    }

}