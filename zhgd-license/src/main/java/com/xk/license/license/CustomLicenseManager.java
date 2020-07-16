package com.xk.license.license;

import com.xk.license.config.KeyConfig;
import de.schlichtherle.license.*;
import de.schlichtherle.xml.GenericCertificate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.XMLDecoder;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.List;

/**
 * 自定义LicenseManager，用于增加额外的服务器硬件信息校验
 *
 */
@Service
public class CustomLicenseManager extends LicenseManager {
    private static Logger logger = LoggerFactory.getLogger(CustomLicenseManager.class);
    @Autowired
    private KeyConfig keyConfig;
    //XML编码
    private static final String XML_CHARSET = "UTF-8";
    //默认BUFSIZE
    private static final int DEFAULT_BUFSIZE = 8 * 1024;
    private static final String KEY_ALGORITHM = "SHA1withRSA";

    public CustomLicenseManager() {

    }

    public CustomLicenseManager(LicenseParam param) {
        super(param);
    }

    /**
     * 复写create方法
     */
    @Override
    protected synchronized byte[] create(
            LicenseContent content,
            LicenseNotary notary)
            throws Exception {
        initialize(content);
        this.validateCreate(content);
        final GenericCertificate certificate = notary.sign(content);
        return getPrivacyGuard().cert2key(certificate);
    }

    /**
     * 复写install方法，其中validate方法调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     */
    @Override
    protected synchronized LicenseContent install(
            final byte[] key,
            final LicenseNotary notary)
            throws Exception {
        final GenericCertificate certificate = getPrivacyGuard().key2cert(key);
        certificate.verify(KeyTools.publicKey, Signature.getInstance(KEY_ALGORITHM));
//        notary.verify(certificate);
        final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
        this.validate(content);
        setLicenseKey(key);
        setCertificate(certificate);
        return content;
    }

    public PublicKey getPublicKeyFromCer() {
        PublicKey publicKey = null;
        FileInputStream in = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            in = new FileInputStream("D:\\KeyStore\\publicCer.cer");
            Certificate c = cf.generateCertificate(in);
            publicKey = c.getPublicKey();

        } catch (CertificateException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return publicKey;
    }

    /**
     * 复写verify方法，调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     *
     */
    @Override
    protected synchronized LicenseContent verify(final LicenseNotary notary)
            throws Exception {
        GenericCertificate certificate = getCertificate();

        // Load license key from preferences,
        final byte[] key = getLicenseKey();
        if (null == key) {
            throw new NoLicenseInstalledException(getLicenseParam().getSubject());
        }

        certificate = getPrivacyGuard().key2cert(key);

        certificate.verify(KeyTools.publicKey, Signature.getInstance(KEY_ALGORITHM));
//        notary.verify(certificate);
        final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
        this.validate(content);
        setCertificate(certificate);

        return content;
    }

    /**
     * 校验生成证书的参数信息
     */
    protected synchronized void validateCreate(final LicenseContent content)
            throws LicenseContentException {
        final LicenseParam param = getLicenseParam();

        final Date now = new Date();
        final Date notBefore = content.getNotBefore();
        final Date notAfter = content.getNotAfter();
        if (null != notAfter && now.after(notAfter)) {
            throw new LicenseContentException("证书失效时间不能早于当前时间");
        }
        if (null != notBefore && null != notAfter && notAfter.before(notBefore)) {
            throw new LicenseContentException("证书生效时间不能晚于证书失效时间");
        }
        final String consumerType = content.getConsumerType();
        if (null == consumerType) {
            throw new LicenseContentException("用户类型不能为空");
        }
    }


    /**
     * 复写validate方法，增加IP地址、Mac地址等其他信息校验
     *
     */
    @Override
    protected synchronized void validate(final LicenseContent content)
            throws LicenseContentException {
        //1. 首先调用父类的validate方法
        super.validate(content);

        //2. 然后校验自定义的License参数
        //License中可被允许的参数信息
        String expectedCheckModel = content.getInfo();
        //当前服务器真实的参数信息
        LicenseCheckModel serverCheckModel = getServerInfos();
        System.out.println("CPU序列号：" + serverCheckModel.getCpuSerial());
        logger.info("CPU序列号" + serverCheckModel.getCpuSerial());
        if (StringUtils.isNotEmpty(expectedCheckModel) && serverCheckModel != null) {
            //校验IP地址
//            if(!checkIpAddress(expectedCheckModel.getIpAddress(),serverCheckModel.getIpAddress())){
//                throw new LicenseContentException("当前服务器的IP没在授权范围内");
//            }

            //校验Mac地址
//            if(!checkIpAddress(expectedCheckModel.getMacAddress(),serverCheckModel.getMacAddress())){
//                throw new LicenseContentException("当前服务器的Mac地址没在授权范围内");
//            }

            //校验主板序列号
//            if(!checkSerial(expectedCheckModel.getMainBoardSerial(),serverCheckModel.getMainBoardSerial())){
//                throw new LicenseContentException("当前服务器的主板序列号没在授权范围内");
//            }

            //校验CPU序列号
            if (!checkSerial(expectedCheckModel, serverCheckModel.getCpuSerial())) {
                throw new LicenseContentException("当前服务器的CPU序列号没在授权范围内");
            }
        } else {
            throw new LicenseContentException("不能获取服务器硬件信息");
        }
    }


    /**
     * 重写XMLDecoder解析XML
     */
    private Object load(String encoded) {
        BufferedInputStream inputStream = null;
        XMLDecoder decoder = null;
        try {
            inputStream = new BufferedInputStream(new ByteArrayInputStream(encoded.getBytes(XML_CHARSET)));

            decoder = new XMLDecoder(new BufferedInputStream(inputStream, DEFAULT_BUFSIZE), null, null);

            return decoder.readObject();
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持解码", e);
        } finally {
            try {
                if (decoder != null) {
                    decoder.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error("XMLDecoder解析XML失败", e);
            }
        }

        return null;
    }

    /**
     * 获取当前服务器需要额外校验的License参数
     *
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

    /**
     * 校验当前服务器的IP/Mac地址是否在可被允许的IP范围内<br/>
     * 如果存在IP在可被允许的IP/Mac地址范围内，则返回true
     *
     */
    private boolean checkIpAddress(List<String> expectedList, List<String> serverList) {
        if (expectedList != null && expectedList.size() > 0) {
            if (serverList != null && serverList.size() > 0) {
                for (String expected : expectedList) {
                    if (serverList.contains(expected.trim())) {
                        return true;
                    }
                }
            }

            return false;
        } else {
            return true;
        }
    }

    /**
     * 校验当前服务器硬件（主板、CPU等）序列号是否在可允许范围内
     *
     */
    private boolean checkSerial(String expectedSerial, String serverSerial) {
        if (StringUtils.isNotBlank(expectedSerial)) {
            if (StringUtils.isNotBlank(serverSerial)) {
                if (expectedSerial.equals(serverSerial)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

}
