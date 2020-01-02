/*
 * Copyright (c) 2001-2019 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package com.example.providerserver.dtx.remote;

import java.io.Serializable;

import lombok.Data;

/**
 * @author chengxy
 * 2019/10/3
 */
@Data
public class ProxyRequest implements Serializable {


    String className;
    String methodName;
    Object[] args;
    Class<?>[] paramTypes;

    String sid;
    Long userId;
    Long orgId;
    String codePrefix;
    String project;

    public ProxyRequest(String className, String methodName, Object[] args, Class<?>[] paramTypes) {
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.paramTypes = paramTypes;
        this.sid = "0";
        this.userId = 0L;
        this.orgId = 0L;
    }
}