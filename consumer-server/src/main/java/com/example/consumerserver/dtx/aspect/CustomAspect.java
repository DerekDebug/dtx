package com.example.consumerserver.dtx.aspect;

import com.example.consumerserver.dtx.custom_annotation.CustomTransactional;
import com.example.consumerserver.dtx.txmanager_client.TxActionType;
import com.example.consumerserver.dtx.txmanager_client.TxProcessor;
import com.example.consumerserver.dtx.txmanager_client.Tx;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 必须实现Ordered；使@CustomTransactional嵌套在spring的@Transactional外部
 */
@Aspect
@Component
public class CustomAspect implements Ordered {

    @Around("@annotation(com.example.consumerserver.dtx.custom_annotation.CustomTransactional)")
    public void invoke(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CustomTransactional currentTx = method.getAnnotation(CustomTransactional.class);
        String xid = null;
        //如果你是一个分布式事务的"发起者"，你就要创建xid
        if (currentTx.isStart()) {
            xid = TxProcessor.createTxGroup();
        }else{
            //而如果你是一个分布式事务的"非发起者"，你就要接收xid
        }
        Tx tx = TxProcessor.createTx(xid);
        try {
            proceedingJoinPoint.proceed();
            //当我们知道本地tx的spring的commit/rollback状态以后，发送给TxManager
            TxProcessor.addTx(tx, currentTx.isEnd(), TxActionType.COMMIT);
            tx.setLocalTxActionType(TxActionType.COMMIT);
        } catch (Throwable throwable) {
            TxProcessor.addTx(tx, currentTx.isEnd(), TxActionType.ROLLBACK);
            tx.setLocalTxActionType(TxActionType.ROLLBACK);
            throwable.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return 10000000;
    }
}
