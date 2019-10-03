/*
 * Copyright (c) 2001-2019 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package com.example.providerserver.dtx.remote;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author chengxy
 * 2019/10/3
 */
public class SerializeUtils {
    public static Object hessianDeserialize(byte[] by) throws IOException {
        if(by==null) throw new NullPointerException();
        return hessianDeserialize(new ByteArrayInputStream(by));
    }

    public static Object hessianDeserialize(InputStream input) throws IOException{
        return new Hessian2Input(input).readObject();
    }

    public static byte[] hessianSerialize(Object obj) throws IOException{
        if(obj==null) throw new NullPointerException();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        try {
            ho.writeObject(obj);
            ho.flushBuffer();
            return os.toByteArray();
        }finally {
            ho.close();
        }

    }
    public static void hessianSerialize(Object obj, OutputStream out) throws IOException{
        Hessian2Output ho = new Hessian2Output(out);
        ho.writeObject(obj);
        ho.flushBuffer();
    }

    public static byte[] javaSerialize(Object obj) throws Exception {
        if(obj==null) throw new NullPointerException();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(obj);
        return os.toByteArray();
    }

    public static Object javaDeserialize(byte[] by) throws Exception {
        if(by==null) throw new NullPointerException();

        ByteArrayInputStream is = new ByteArrayInputStream(by);
        ObjectInputStream in = new ObjectInputStream(is);
        return in.readObject();
    }
}