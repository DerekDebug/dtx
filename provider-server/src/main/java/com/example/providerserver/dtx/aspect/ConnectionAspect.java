package com.example.providerserver.dtx.aspect;

import com.example.providerserver.dtx.connection.InterceptedConnection;
import com.example.providerserver.dtx.txmanager_client.Tx;
import com.example.providerserver.dtx.txmanager_client.TxProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Aspect
@Component
public class ConnectionAspect {


//  @Around("execution(* java.sql.DriverManager.getConnection(..))")
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection getConnection(ProceedingJoinPoint point) throws Throwable {
        System.out.println("....................拦截connection");
        Connection spring_connection = (Connection) point.proceed();
        Tx tx = TxProcessor.getThreadnLocalTx();
        if (tx != null){
            return new InterceptedConnection(spring_connection, tx);
        }else{
            //放行
            return spring_connection;
        }
    }
}
