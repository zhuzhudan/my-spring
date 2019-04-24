package com.study.spring.framework.beans.config;

/**
 * 用来存储配置文件中的信息
 * 相当于保存在内存中的配置
 */
public class MyBeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;

    // 由Spring创建、管理bean，需要先提供类名
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getBeanClassName() {
        return this.beanClassName;
    }

    // 这个类是否延时加载
    // 如果延时加载，在getBean时进行初始化
    // 如果不需要延时加载，在IOC容器初始化后进行初始化
    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    // 类存在工厂中的名字
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }
}
