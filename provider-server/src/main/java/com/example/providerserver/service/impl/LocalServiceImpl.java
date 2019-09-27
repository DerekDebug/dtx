package com.example.providerserver.service.impl;

import com.example.providerserver.dao.ProviderMapper;
import com.example.providerserver.dtx.custom_annotation.CustomTransactional;
import com.example.providerserver.entity.ProviderUser;
import com.example.providerserver.service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalServiceImpl implements LocalService {

    @Autowired
    ProviderMapper providerMapper;

    //本来再多加一个provider的，但是比较懒
    @CustomTransactional(isEnd = true)
    @Transactional
    @Override
    public void insertUser(Integer age) {
        ProviderUser providerUser = new ProviderUser();
        providerUser.setName("jerry");
        providerUser.setAge(age);
        providerMapper.insertUser(providerUser);
    }
}
