package com.xk.zhgd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.Scanner;

/**
 * @author zhangyz
 * @date 2020/7/13
 */
@Component
public class LicConfig {

    @Autowired
    private String LIC_PATH = "D:\\Lic.lic";

    public String getSecret(String mingWen){
        //获取cpuid
        //第一MD5加密获取初识密文
        String firstMi = DigestUtils.md5DigestAsHex(mingWen.getBytes());
        //截取初识密文片段
        String jieStr = firstMi.substring(5,16);
        //设置盐值
        String salt = "1234567890qwertyuiopasdfghjklzxcvbnmmmmm][\';/.,~!@#@%%^&**(()";
        //拼成新的字符串并再次进行MD5加密
        String newStr = firstMi+jieStr+salt;
        return DigestUtils.md5DigestAsHex(newStr.getBytes());
    }

    public void getCpuId() {
        try {
            long start = System.currentTimeMillis();
            Process process = Runtime.getRuntime().exec(
                    new String[] { "wmic", "cpu", "get", "ProcessorId" });
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());
            String property = sc.next();
            String serial = sc.next();
            System.out.println(property + ": " + serial);
            System.out.println("time:" + (System.currentTimeMillis() - start));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLic(){
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            String fileName =LIC_PATH;
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            StringBuffer sb = new StringBuffer();
            String line = bufferedReader.readLine();

            while (line!=null){
                System.out.println(line);
                sb.append(line);
                line = bufferedReader.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
                if(fileReader!=null){
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static void main(String[] args){
        LicConfig licConfig = new LicConfig();
        licConfig.getCpuId();
        System.out.println("Lic.lic:"+licConfig.readLic());
        System.out.println(licConfig.getSecret("123456746945632131321"));
    }

}
