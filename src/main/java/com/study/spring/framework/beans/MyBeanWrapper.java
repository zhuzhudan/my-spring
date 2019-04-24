package com.study.spring.framework.beans;

public class MyBeanWrapper {
    /**
     * 如果是单例，可通过方法获得
     */
    public Object getWrappedInstance(){

    }

    /**
     * 如果不是单例，可根据Class获得实例（new）
     */
    public Class<?> getWrappedClass(){

    }
}
