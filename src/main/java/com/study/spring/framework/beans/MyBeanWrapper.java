package com.study.spring.framework.beans;

public class MyBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrapperClass;

    public MyBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }

    /**
     * 如果是单例，可通过方法获得
     */
    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    /**
     * 如果不是单例，可根据Class获得实例（new）
     */
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
