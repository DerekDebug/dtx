/*
 * Copyright (c) 2001-2019 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package com.example.providerserver.dtx.remote;

/**
 * @author chengxy
 * 2019/10/3
 */
public class ConfigHelper {


    //加载资源文件，按自己需要定义
    public static String getProp(String name) {

        String file = "/" + name + ".properties";
        return file;
    }
}
