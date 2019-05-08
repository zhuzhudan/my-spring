package com.study.spring.framework.aop;

public interface MyAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
