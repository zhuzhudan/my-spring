package com.study.spring.business.service;

public interface IDemoService {
    String get(String name);

    String add(String name, String addr) throws Exception;

    String edit(Integer id, String name);

    String remove(Integer id);
}
