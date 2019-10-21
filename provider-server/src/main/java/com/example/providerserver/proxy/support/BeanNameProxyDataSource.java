package com.example.providerserver.proxy.support;

import com.example.providerserver.proxy.proxy.ProxyConfig;

import org.springframework.beans.factory.BeanNameAware;



/**
 * Extending {@link ProxyDataSource} to use
 * spring bean name(id) as dataSourceName when it is not set.
 *
 * @author complone
 */
public class BeanNameProxyDataSource extends ProxyDataSource implements BeanNameAware {

    public void setBeanName(String name) {
        final String dataSourceName = getProxyConfig().getDataSourceName();
        if (dataSourceName == null || "".equals(dataSourceName)) {
            ProxyConfig proxyConfig = ProxyConfig.Builder.from(getProxyConfig()).dataSourceName(name).build();
            setProxyConfig(proxyConfig);
        }
    }
}
