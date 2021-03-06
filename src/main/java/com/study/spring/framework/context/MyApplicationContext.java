package com.study.spring.framework.context;

import com.study.spring.framework.annotation.MyAutowired;
import com.study.spring.framework.annotation.MyController;
import com.study.spring.framework.annotation.MyService;
import com.study.spring.framework.aop.MyAopProxy;
import com.study.spring.framework.aop.MyCglibAopProxy;
import com.study.spring.framework.aop.MyJdkDynamicAopProxy;
import com.study.spring.framework.aop.config.MyAopConfig;
import com.study.spring.framework.aop.support.MyAdvisedSupport;
import com.study.spring.framework.beans.MyBeanFactory;
import com.study.spring.framework.beans.MyBeanWrapper;
import com.study.spring.framework.beans.config.MyBeanDefinition;
import com.study.spring.framework.beans.config.MyBeanPostProcessor;
import com.study.spring.framework.beans.support.MyBeanDefinitionReader;
import com.study.spring.framework.beans.support.MyDefaultListableBeanFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
    private String[] configLoacations;
    private MyBeanDefinitionReader reader;

    // 用来保证注册式单例的容器，单例IOC缓存
    private Map<String, Object> singletonBeanCacheMap = new ConcurrentHashMap<>();
    // 用来存储所有的被代理过的对象
    private Map<String, MyBeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

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
        //------------------------------------IOC初始化完毕-----------------------------//

        //--------------------------------------DI注入开始------------------------------//
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
    //------------------------------------IOC初始化完毕--------------------------------------

    //--------------------------------------DI注入开始---------------------------------------
    // 依赖注入，开始，通过读取BeanDefinition中的信息，然后，通过反射机制创建一个实例并返回
    // Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、需要对它进行扩展，增强（为以后AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {
        //分成初始化、注入，是防止循环依赖
        // 1、初始化
        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        MyBeanPostProcessor postProcessor = new MyBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance, beanName);

        instance = instantiateBean(beanName, beanDefinition);
        // instantiateBean中的3、将对象封装到BeanWrapper中
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);

        // 2、拿到BeanWrapper之后，把BeanWrapper保存到IOC容器中
        this.beanWrapperMap.put(beanName, beanWrapper);

        postProcessor.postProcessAfterInitialzation(instance, beanName);

        // 3、注入，DI操作
        populateBean(beanName, new MyBeanDefinition(), beanWrapper);
        
        return this.beanWrapperMap.get(beanName).getWrappedInstance();
    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    // DI操作，给真正的IOC容器赋值、缓存
    // 将BeanDefinition转化成BeanWrapper，保存到真正的IOC容器中
    private void populateBean(String beanName, MyBeanDefinition beanDefinition, MyBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();

        Class<?> clazz = beanWrapper.getWrappedClass();
        // 判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyService.class))){
            return;
        }

        // 获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            if(!field.isAnnotationPresent(MyAutowired.class)){
                continue;
            }
            MyAutowired autowired = field.getAnnotation(MyAutowired.class);
            String autowridBeanName = autowired.value();
            if("".equals(autowridBeanName)){
                autowridBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                if(this.beanWrapperMap.get(autowridBeanName) == null){
                    continue;
                }
                field.set(instance, this.beanWrapperMap.get(autowridBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


    }

    // 负责读取BeanDefinition的配置，将配置的信息转化成实体对象，存到真正的IOC容器
    // IOC容器的Key就是beanName，Value就是beanDefinition存储的Class的实例
    private Object instantiateBean(String beanName, MyBeanDefinition beanDefinition) {
        // 1、拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();

        // 2、反射实例化，得到对象
        Object instance = null;
        try {
            if(this.singletonBeanCacheMap.containsKey(className)){
                instance = this.singletonBeanCacheMap.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                // 解析配置文件
                MyAdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                // 查看当前类是否在切面规则之内
                // 符合PointCut的规则的话，将创建代理对象
                if(config.pointCutMatch()){
                    // 创建代理策略，看是用CGLib还是JDK
                    instance = createProxy(config).getProxy();
                }

                this.singletonBeanCacheMap.put(className, instance);
                this.singletonBeanCacheMap.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        // 3、将对象封装到BeanWrapper中
//        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);
//
//        // 4、把BeanWrapper存到IOC容器里面
        return instance;
    }

    //--------------------------------------MVC 开始---------------------------------------
    public String[] getBeanDefinitionNames(){
        //伪IOC容器，存储注册信息的BeanDefinition
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

    //--------------------------------------AOP 开始---------------------------------------

    private MyAdvisedSupport instantionAopConfig(MyBeanDefinition beanDefinition) {
        MyAopConfig config = new MyAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
//        config.setAspectAround(this.reader.getConfig().getProperty("aspectAround"));
        return new MyAdvisedSupport(config);
    }

    private MyAopProxy createProxy(MyAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length> 0){
            return new MyJdkDynamicAopProxy(config);
        }
        return new MyCglibAopProxy(config);
    }

}
