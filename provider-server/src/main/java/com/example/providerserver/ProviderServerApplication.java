package com.example.providerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ProviderServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ProviderServerApplication.class, args);
    }

}
