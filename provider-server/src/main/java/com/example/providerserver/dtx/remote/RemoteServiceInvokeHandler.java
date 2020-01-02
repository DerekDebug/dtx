package com.example.providerserver.dtx.remote;

import com.alibaba.fastjson.JSONArray;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteServiceInvokeHandler implements InvocationHandler {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private Class portType;
    private String serviceFileName;
    private static Map<String, CloseableHttpClient> httpClientMap = new HashMap<>();

    private  CloseableHttpClient getClient() {
        if (httpClientMap.get(serviceFileName) != null) {
            return httpClientMap.get(serviceFileName);
        }
        if (httpClientMap.get(serviceFileName) == null) {
            synchronized (RemoteServiceInvokeHandler.class) {
                if (httpClientMap.get(serviceFileName) == null) {
                    try {
                        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager();
                        pool.setMaxTotal(3000);
                        URL url = new URL(getRemoteUrl());
                        pool.setMaxPerRoute(new HttpRoute(new HttpHost(url.getHost())), 3000);
                        CloseableHttpClient httpclient = HttpClientBuilder.create().disableCookieManagement().disableAuthCaching().disableAutomaticRetries().setConnectionManager(pool).build();
                        httpClientMap.put(serviceFileName, httpclient);
                    } catch (Exception e) {
                        log.error("Init http client error : "+e.toString());
                    }
                }
            }
        }
        return httpClientMap.get(serviceFileName);
    }

    private String getRemoteUrl() {
        return "http://" + ConfigHelper.getProp(serviceFileName);
    }

    public RemoteServiceInvokeHandler(Class portType) {
        this.portType = portType;
        this.serviceFileName = "service";
        getClient();
    }

    public RemoteServiceInvokeHandler(Class portType, String serviceFileName) {
        this.portType = portType;
        this.serviceFileName = serviceFileName;
        getClient();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("toString".equals(method.getName())) {
            return proxy;
        }

        //调用远程接口
        HttpPost postMethod = new HttpPost(getRemoteUrl());
        ByteArrayEntity entry = new ByteArrayEntity(SerializeUtils.hessianSerialize(new ProxyRequest(portType.getName(), method.getName(), args, method.getParameterTypes())));
        postMethod.setEntity(entry);
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            //long start = System.currentTimeMillis();
            response = httpClientMap.get(serviceFileName).execute(postMethod);
            //log.debug("execute "+method.getName()+":" + (System.currentTimeMillis()-start));
            int num = response.getStatusLine().getStatusCode();
            if (num >= 200 && num < 300) {
                // 获取二进制的byte流
                entity = response.getEntity();
                ProxyResponse r = (ProxyResponse) SerializeUtils.hessianDeserialize(entity.getContent());
                if (r.isSuccess()) {
                    JSONArray logs = r.getLogs();
                    return r.getData();
                } else {
                    throw new Exception(StringUtils.isBlank(r.getErrorMsg()) ? "调用远程业务接口时，未知错误" : r.getErrorMsg());
                }
            } else if (num == 404) {
                throw new Exception("业务服务器地址不存在，当前地址" + getRemoteUrl());
            } else {
                throw new Exception("请求业务服务器失败，返回" + num);
            }
        } catch (Exception e) {
            log.error("Execute remote method error", e);
            throw e;
        } finally {
            postMethod.releaseConnection();
            if(entity!=null){
                EntityUtils.consume(entity);
            }
            if (response != null) {
                response.close();
            }
        }
    }
}