/*
 * Copyright (c) 2001-2019 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package com.example.providerserver.dtx.remote;

import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;

import lombok.Data;

/**
 * @author chengxy
 * 2019/10/3
 */
@Data
public class ProxyResponse  implements Serializable {
    Object data;
    Class<?> returnType;
    boolean success;
    JSONArray logs;
    String errorMsg;

    public static ProxyResponse success(Object data, JSONArray logs){
        ProxyResponse response = new ProxyResponse();
        response.setSuccess(true);
        response.setData(data);
        response.setLogs(logs);
        return response;
    }

    public static ProxyResponse error(String errorMsg){
        ProxyResponse response = new ProxyResponse();
        response.setSuccess(false);
        response.setErrorMsg(errorMsg);
        return response;
    }
}
