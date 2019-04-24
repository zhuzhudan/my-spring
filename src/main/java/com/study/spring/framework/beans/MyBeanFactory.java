package com.study.spring.framework.beans;

/**
 * 单例工厂的顶层设计
 */
public interface MyBeanFactory {
    /**
     * 根据beanName从IOC容器中获取一个实例bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;
}
