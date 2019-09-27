package com.example.consumerserver.controller;

import com.example.consumerserver.feign.InvokeService;
import com.example.consumerserver.service.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.consumerserver.service.LocalService;

@RestController
public class ConsumerController {

    @Autowired
    LocalService localService;

    @Autowired
    InvokeService invokeService;

    @Autowired
    RemoteService remoteService;

    @GetMapping("/consumer/{age}")
    public Integer addCat(@PathVariable("age") Integer age) {
        localService.insertUser(age);
        return 0;
    }

    /**
     *分布式事务接口，在consumerdb & providerdb 各自插入一条同年龄的动物：tom & jerry
     */
    @GetMapping("/txinvoke/{age}")
    public Integer txAddPair(@PathVariable("age")Integer age){
        remoteService.GlobalTx(age);
        return 0;
    }

}
