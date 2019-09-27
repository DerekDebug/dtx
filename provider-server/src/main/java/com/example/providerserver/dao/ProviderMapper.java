package com.example.providerserver.dao;

import com.example.providerserver.entity.ProviderUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProviderMapper {

    void insertUser(ProviderUser user);

}
