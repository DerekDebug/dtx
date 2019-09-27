package com.example.consumerserver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class ConsumerServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConsumerServerApplication.class, args);
    }



}
