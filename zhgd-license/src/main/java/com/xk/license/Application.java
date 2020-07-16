package com.xk.license;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan({"com.xk"})
//@ServletComponentScan
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
	}
}
