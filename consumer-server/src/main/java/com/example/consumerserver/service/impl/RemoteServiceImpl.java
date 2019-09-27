package com.example.consumerserver.service.impl;

import com.example.consumerserver.dtx.txmanager_client.TxProcessor;
import com.example.consumerserver.feign.InvokeService;
import com.example.consumerserver.service.LocalService;
import com.example.consumerserver.service.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoteServiceImpl implements RemoteService {

    @Autowired
    LocalService localService;

    @Autowired
    InvokeService invokeService;

    @Override
    public void GlobalTx(Integer age)  {
        localService.insertUser(age);
        invokeService.addMouse(age,TxProcessor.getXid(),TxProcessor.getTxCount());

    }

}
