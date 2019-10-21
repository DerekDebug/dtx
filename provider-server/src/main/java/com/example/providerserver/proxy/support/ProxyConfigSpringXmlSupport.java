package com.example.providerserver.proxy.support;

import com.example.providerserver.proxy.proxy.ProxyConfig;

/**
 * {@link ProxyConfig} bean creation support for XML based spring configuration.
 *
 * In xml based spring configuration file, defining a {@link ProxyConfig} bean with its builder class requires
 * extra effort since all builder methods are not java bean setters.
 * To simplify it, this class provides setters to create a {@link ProxyConfig} bean.
 *
 * <p/>Example spring xml config:
 * <pre>
 * {@code
 * <bean id="proxyConfig"
 *       factory-bean="proxyConfigSupport"
 *       factory-method="create"/>
 *
 * <bean id="proxyConfigSupport" class="net.ttddyy.dsproxy.support.ProxyConfigSpringXmlSupport">
 *   <property name="dataSourceName" value="my-ds"/>
 *   <property name="queryListener" ref="myQueryListener"/>
 *   <property name="methodListener" ref="myMethodListener"/>
 * </bean>
 *
 * <bean id="myQueryListener" class="net.ttddyy.dsproxy.listener.ChainListener">
 *   <property name="listeners">
 *     <list>
 *       <bean class="net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener"/>
 *     </list>
 *   </property>
 * </bean>
 *
 * <bean id="myMethodListener" class="net.ttddyy.dsproxy.listener.CompositeMethodListener">
 *   <property name="listeners">
 *       <list>
 *       </list>
 *   </property>
 * </bean>
 * }
 * </pre>
 *
 * @author complone
 * @since 1.4.4
 */
public class ProxyConfigSpringXmlSupport {

    private String dataSourceName;

    public ProxyConfig create() {
        ProxyConfig.Builder builder = ProxyConfig.Builder.create();
        if (this.dataSourceName != null) {
            builder.dataSourceName(this.dataSourceName);
        }
        return builder.build();
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

}
