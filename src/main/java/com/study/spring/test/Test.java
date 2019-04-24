package com.study.spring.test;

import com.study.spring.framework.context.MyApplicationContext;

public class Test {
    public static void main(String[] args) {
        MyApplicationContext context = new MyApplicationContext("classpath:application.properties");
        System.out.println(context);
    }
}
