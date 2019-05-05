package com.study.spring.framework.beans.config;

public class MyBeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }

    public Object postProcessAfterInitialzation(Object bean, String beanName) throws Exception{
        return bean;
    }
}
