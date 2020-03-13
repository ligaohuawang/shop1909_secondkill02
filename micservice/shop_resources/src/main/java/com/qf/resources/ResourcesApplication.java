package com.qf.resources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ResourcesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourcesApplication.class, args);
    }

}
