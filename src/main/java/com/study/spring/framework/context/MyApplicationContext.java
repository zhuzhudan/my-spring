package com.study.spring.framework.context;

import com.study.spring.framework.beans.MyBeanFactory;
import com.study.spring.framework.beans.MyBeanWrapper;
import com.study.spring.framework.beans.config.MyBeanDefinition;
import com.study.spring.framework.beans.support.MyBeanDefinitionReader;
import com.study.spring.framework.beans.support.MyDefaultListableBeanFactory;

import java.util.List;
import java.util.Map;

public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
    private String[] configLoacations;
    private MyBeanDefinitionReader reader;

    public MyApplicationContext(String... configLoacations){
        this.configLoacations = configLoacations;

        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 1、定位，定位配置文件（策略模式）
        reader = new MyBeanDefinitionReader(this.configLoacations);

        // 2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        // 3、注册，把配置信息放到容器里面（伪IOC容器）
        // 真正的IOC容器是BeanWrapper
        doRegisterBeanDefinition(beanDefinitions);

        // 4、把不是延时加载的类，要提前初始化
        // 注入
        doAutowrited();
    }

    // 只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, MyBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doRegisterBeanDefinition(List<MyBeanDefinition> beanDefinitions) {
        for (MyBeanDefinition beanDefinition : beanDefinitions){
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        // IOC容器初始化完毕
    }

    // 依赖注入，开始，通过读取BeanDefinition中的信息，然后，通过反射机制创建一个实例并返回
    // Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、需要对它进行扩展，增强（为以后AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {
        //分成初始化、注入，是防止循环依赖
        // 1、初始化
        instantiateBean(beanName, new MyBeanDefinition());

        // 2、注入，DI操作
        populateBean(beanName, new MyBeanDefinition(), new MyBeanWrapper());
        
        return null;
    }

    // DI操作，给真正的IOC容器赋值、缓存
    // 将BeanDefinition转化成BeanWrapper，保存到真正的IOC容器中
    private void populateBean(String beanName, MyBeanDefinition myBeanDefinition, MyBeanWrapper myBeanWrapper) {
    }

    private void instantiateBean(String beanName, MyBeanDefinition myBeanDefinition) {
    }
}
