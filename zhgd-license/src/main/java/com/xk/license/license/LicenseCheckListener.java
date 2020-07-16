package com.xk.license.license;

import com.xk.license.config.KeyConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * 在项目启动时安装证书
 *
 */
@Component
public class LicenseCheckListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LoggerFactory.getLogger(LicenseCheckListener.class);
    @Autowired
    private KeyConfig keyConfig;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //root application context 没有parent
        ApplicationContext context = event.getApplicationContext().getParent();
        System.out.println(keyConfig.LICENSE_FILE);
        if (context == null) {
            if (StringUtils.isNotBlank(keyConfig.LICENSE_FILE)) {
                //当前服务器真实的参数信息
                LicenseCheckModel serverCheckModel = getServerInfos();
                System.out.println("CPU序列号：" + serverCheckModel.getCpuSerial());
                logger.info("CPU序列号:" + serverCheckModel.getCpuSerial());
                logger.info("++++++++ 开始安装证书 ++++++++");
                LicenseVerifyParam param = new LicenseVerifyParam();
                param.setSubject(KeyConfig.SUB_PRJ);
                param.setPublicAlias(KeyConfig.PUBLIC_ALIAS);
                param.setStorePass(KeyConfig.KEYSTORE_PASSWORD);
                param.setLicensePath(keyConfig.LICENSE_FILE);
                param.setPublicKeysStorePath(keyConfig.PUBLICKEY_FILE_PATH);
                try {
                    KeyTools.getPublicKey(param.getPublicKeysStorePath(),param.getPublicAlias(),param.getStorePass());
                } catch (Exception e) {
                    throw new NullPointerException("证书获取失败");
                }
                LicenseVerify licenseVerify = new LicenseVerify();
                //安装证书
                licenseVerify.install(param);

                logger.info("++++++++ 证书安装结束 ++++++++");
            } else {
                throw new NullPointerException("证书为空");
            }
        } else {
            throw new NullPointerException("启动失败");
        }
    }

    /**
     * 获取当前服务器需要额外校验的License参数
     *
     * @return demo.LicenseCheckModel
     * @author zifangsky
     * @date 2018/4/23 14:33
     * @since 1.0.0
     */
    private LicenseCheckModel getServerInfos() {
        //操作系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        AbstractServerInfos abstractServerInfos = null;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        } else {//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }
        System.out.println("cpu序列号：" + abstractServerInfos.getServerInfos().getCpuSerial());
        logger.info("cpu序列号：" + abstractServerInfos.getServerInfos().getCpuSerial());
        return abstractServerInfos.getServerInfos();

    }

}
