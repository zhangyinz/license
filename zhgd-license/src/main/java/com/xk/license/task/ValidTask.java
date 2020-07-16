package com.xk.license.task;

import com.xk.license.license.LicenseVerify;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zhangyz
 * @date 2020/7/14
 */
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class ValidTask {

    @Scheduled(cron = "10 0/1 * * * ?")
    public void validLicense() {
        LicenseVerify licenseVerify = new LicenseVerify();
        licenseVerify.verify();
    }
}
