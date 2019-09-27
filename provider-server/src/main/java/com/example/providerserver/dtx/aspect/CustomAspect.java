package com.example.providerserver.dtx.aspect;


import com.example.providerserver.dtx.custom_annotation.CustomTransactional;
import com.example.providerserver.dtx.txmanager_client.Tx;
import com.example.providerserver.dtx.txmanager_client.TxActionType;
import com.example.providerserver.dtx.txmanager_client.TxProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class CustomAspect implements Ordered {

    @Around("@annotation(com.example.providerserver.dtx.custom_annotation.CustomTransactional)")
    public void invoke(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CustomTransactional currentTx = method.getAnnotation(CustomTransactional.class);
        String xid = null;
        if (currentTx.isStart()) {
//            如果这里是consumer，那就要createXid
        }else{
            xid=TxProcessor.getXid();
        }
        Tx tx = TxProcessor.createTx(xid);
        try {
            proceedingJoinPoint.proceed();
            TxProcessor.addTx(tx, currentTx.isEnd(), TxActionType.COMMIT);
            tx.setLocalTxActionType(TxActionType.COMMIT);
        } catch (Throwable throwable) {
            tx.setLocalTxActionType(TxActionType.ROLLBACK);
            TxProcessor.addTx(tx, currentTx.isEnd(), TxActionType.ROLLBACK);
            throwable.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return 100000;
    }
}
