package com.example.consumerserver.dtx.custom_annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这里完全是鄙人对阿里SEATA拙劣的模仿：
 * 用注解的方式，可以实现低耦合让service实现分布式事务
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomTransactional {

    /**
     * isstart == true -->创建xid
     */
    boolean isStart() default false;


    /**
     * 当TxManager收到所有的注解信息的时候，你可以通过是否 isEnd == true && txCount来进行最终判断，虽然
     * 这里的判断条件isEnd==true可能是多余的，关于txCount
     * 在createXid的时候与TxManager约定txCount才是严谨的
     */
    boolean isEnd() default false;

    /**
     * 想自己定规则，随你写
     */


// int timeoutMils() default 60000;
// String name() default "";
}
