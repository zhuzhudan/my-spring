package com.study.spring.framework.aop;

import com.study.spring.framework.aop.support.MyAdvisedSupport;

public class MyCglibAopProxy implements MyAopProxy {
    public MyCglibAopProxy(MyAdvisedSupport config) {

    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
