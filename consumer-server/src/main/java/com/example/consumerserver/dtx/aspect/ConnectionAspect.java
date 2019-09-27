package com.example.consumerserver.dtx.aspect;

import com.example.consumerserver.dtx.connection.InterceptedConnection;
import com.example.consumerserver.dtx.txmanager_client.TxProcessor;
import com.example.consumerserver.dtx.txmanager_client.Tx;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;


/**
 *Q:我们想要拿到spring对事务commit/rollback的控制权，怎么拿？
 *A:思路有哪些呢：
 *  1.拿到DateSource
 *  2.拿到实际spring commit/rollback的方法，重写
 *
 *最后怎么搞？我们看看SEATA框架源码
 * 我们从阿里的SEATA发现，在其rm-datasource-module下面，有各种datasource、connection、PrepareStatement、statement的代理类
 *而且，其写了一系列的"UndoXXXXExecutor"，关于tcc事务补偿，我们下次再说
 *打断点发现:spring的connection是HikariProxyConnection；尝试改造HikariProxyConnection，卒
 *水平有限，我们还是implements connection，试一试呗
 *
 */
@Aspect
@Component
public class ConnectionAspect {

    /**
     * 测试发现，我们成功可以拦截connection！！！
     * 由于本人基础差，对javax.sql包以及jdbc知之甚少，水平过低，开始使用druid的时候用的druid的getConnection以及DriverManager的getConnection
     *     @Around("execution(* com.alibaba.pool.druid.getConnection(..)")
     *     @Around("execution(* java.sql.DriverManager.getConnection(..))")
     * 后来干脆不用线程池，直接切javax基础包的getConnection，成功
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection getConnection(ProceedingJoinPoint point) throws Throwable {
        System.out.println("....................拦截connection");
        Connection spring_connection = (Connection) point.proceed();
        Tx tx = TxProcessor.getThreadLocalTx();
        if (tx != null){
            return new InterceptedConnection(spring_connection, tx);
        }else{
            //如果不是一个分布式事务，直接放行spring的connection
            return spring_connection;
        }
    }
}
