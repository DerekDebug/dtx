package com.example.consumerserver.service.impl;

import com.example.consumerserver.dtx.custom_annotation.CustomTransactional;
import com.example.consumerserver.dao.ConsumerMapper;
import com.example.consumerserver.entity.ConsumerUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.consumerserver.service.LocalService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalServiceImpl implements LocalService {

    @Autowired
    ConsumerMapper consumerMapper;

    /**
     * 事务的发起者，一定要声明isStart==true
     */
    @CustomTransactional(isStart = true)
    @Transactional
    @Override
    public void insertUser(Integer age) {
        ConsumerUser consumerUser =new ConsumerUser("tom",age);
        consumerMapper.insertUser(consumerUser);
        errorFunc();
    }

    /**
     * 测试用
     */
    private void errorFunc()   {
        int i = 100 / 0;

    }
}
