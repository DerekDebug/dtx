package com.example.txmanagerserver;

import com.example.txmanagerserver.netty.BootNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TxmanagerServerApplication {

    public static void main(String[] args) throws Exception {
        //这里本可以把springboot给关了，只开netty，但是比较懒
        SpringApplication.run(TxmanagerServerApplication.class, args);
        new BootNettyServer().bind(8055);
    }
}
