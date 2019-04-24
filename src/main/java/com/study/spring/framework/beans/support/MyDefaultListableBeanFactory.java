package com.study.spring.framework.beans.support;

import com.study.spring.framework.beans.config.MyBeanDefinition;
import com.study.spring.framework.context.support.MyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC容器实例化的默认实现，可以扩展，但不能没有
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {
    //IOC容器，存储注册信息的BeanDefinition，伪IOC容器
    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>();
}
