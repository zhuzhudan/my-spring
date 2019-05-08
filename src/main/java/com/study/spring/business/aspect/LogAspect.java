package com.study.spring.business.aspect;

import com.study.spring.framework.aop.aspect.MyJoinPoint;

import java.util.Arrays;

public class LogAspect {

    // 在调用一个方法之前，执行before方法
    public void before(MyJoinPoint joinPoint){
        // 向对象里记录调用的开始时间
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());

        System.out.println("Invoker Before Method, " +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    // 在调用一个方法之后，执行after方法
    public void after(MyJoinPoint joinPoint){
        // 向对象里记录调用的结束时间（系统当前时间-之前记录的开始时间=方法调用所消耗的时间）
        System.out.println("Invoker After Method, " +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long)joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time : " + (endTime - startTime));
    }

    // 当调用一个方法时出现异常
    public void afterThrowing(MyJoinPoint joinPoint, Throwable ex){
        // 异常监测，可以拿到异常信息
        System.err.println("出现异常" + "\n" +
                "TargetObject:" + joinPoint.getThis() + "\n" +
                "Args:" + Arrays.toString(joinPoint.getArguments()) + "\n" +
                "Throws:" + ex.getMessage());
    }

    public void around(MyJoinPoint joinPoint){

    }
}
