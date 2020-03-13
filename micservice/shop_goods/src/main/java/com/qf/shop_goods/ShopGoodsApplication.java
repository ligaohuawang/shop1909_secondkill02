package com.qf.shop_goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.qf")
@EnableEurekaClient
@EnableTransactionManagement
@MapperScan("com.qf.dao")
@EnableCaching
public class ShopGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopGoodsApplication.class, args);
    }

}
