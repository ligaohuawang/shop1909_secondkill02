package com.qf.shop_auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = "com.qf")
@EnableEurekaClient
@MapperScan("com.qf.dao")
public class ShopAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopAuthApplication.class, args);
    }

}
