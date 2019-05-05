package com.study.spring.business.service.impl;

import com.study.spring.business.service.IDemoService;
import com.study.spring.framework.annotation.MyService;

@MyService
public class DemoService implements IDemoService {
    @Override
    public String get(String name) {
        return "Hello, " + name;
    }

    @Override
    public String add(String name, String addr) throws Exception {
        throw new Exception("这是故意抛的异常");
    }

    @Override
    public String edit(Integer id, String name) {
        return "demoService edit,id=" + id + ",name=" + name;
    }

    @Override
    public String remove(Integer id) {
        return "demoService id=" + id;
    }
}
