package com.study.spring.framework.aop.intercept;

public interface MyMethodInterceptor {
    Object invoke(MyMethodInvocation invocation) throws Throwable;
}
