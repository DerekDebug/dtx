package com.example.consumerserver.dao;

import com.example.consumerserver.entity.ConsumerUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsumerMapper {

    void insertUser(ConsumerUser user);

}
