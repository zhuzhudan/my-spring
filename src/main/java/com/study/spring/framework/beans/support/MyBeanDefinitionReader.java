package com.study.spring.framework.beans.support;

import com.study.spring.framework.beans.config.MyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 定位配置文件的核心类（包装器模式）
 */
public class MyBeanDefinitionReader {
    private Properties config = new Properties();

    // 固定配置文件中的key，相当于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    // 需要注册的bean
    private List<String> registyBeanClasses = new ArrayList<>();

    public MyBeanDefinitionReader(String... locations){
        // 通过URL定位找到其所对应的文件，然后转换为文件流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    // 扫描解析
    private void doScanner(String scanPackage) {
        // 转换为文件路径，实际上就是把.替换为/ 就好了
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPathFile = new File(url.getFile());
        for (File file : classPathFile.listFiles()){
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            } else {
                if(!file.getName().endsWith(".class")){
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                this.registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return config;
    }

    // 返回值是List，刚刚扫描的所有配置文件
    // 把配置文件中扫描到的所有的配置信息转换为MyBeanDefinition对象，以便于之后IOC操作方便
    public List<MyBeanDefinition> loadBeanDefinitions(){
        List<MyBeanDefinition> result = new ArrayList<>();

        try {
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);

                // 如果是一个接口，不能实例化，用它的实现类实例化
                if(beanClass.isInterface()){
                    continue;
                }

                // beanName有三种情况：
                // 1、默认是类名首字母小写
                // 2、自定义名字
                // 3、接口注入
                MyBeanDefinition beanDefinition = doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName());
                if (null == beanDefinition) {
                    continue;
                }
                // 既可以根据类名来取，又可以根据自定义的名字来取
                result.add(beanDefinition);
                result.add(doCreateBeanDefinition(beanClass.getName(),beanClass.getName()));

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces){
                    // 如果多个实现类，只能覆盖
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 把每一个配置信息解析成BeanDefinition
    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String className){
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setBeanClassName(className);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;

    }

    private String toLowerFirstCase(String simpleName){
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


}
