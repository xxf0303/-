package com.example.mvc2;

import com.example.mvc2.dao.Mvc2Dao;
import com.example.mvc2.dao.Mvc2DaoImpl;
import com.example.mvc2.service.Mvc2Service;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = Mvc2Application.class)
class Mvc2ApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    @Test
    public void testApplicationContext() {
        System.out.println(applicationContext);

        Mvc2Dao mvc2dao = applicationContext.getBean(Mvc2Dao.class);
        System.out.println(mvc2dao.select());

        Mvc2Dao mvc2dao2 = applicationContext.getBean("mvc",Mvc2Dao.class);
        System.out.println(mvc2dao2.select());

    }
    @Test
    public void testBeanService() {
        Mvc2Service mvc2Service = applicationContext.getBean(Mvc2Service.class);
        System.out.println(mvc2Service);
    }
    @Test
    public void test() {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    @Autowired
    @Qualifier("mybatis")
    private Mvc2Dao mvc2Dao;
    @Test
    public void test2() {
        System.out.println(mvc2Dao);
    }
}
