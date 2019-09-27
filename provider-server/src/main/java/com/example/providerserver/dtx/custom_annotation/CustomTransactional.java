package com.example.providerserver.dtx.custom_annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomTransactional {

    boolean isStart() default false;

    //其实也可以不要
    boolean isEnd() default false;

// int timeoutMils() default 60000;
// String name() default "";
}
