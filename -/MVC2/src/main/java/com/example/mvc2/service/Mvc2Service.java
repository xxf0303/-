package com.example.mvc2.service;

import com.example.mvc2.dao.Mvc2Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class Mvc2Service {
    @Autowired
    private Mvc2Dao mvc2Dao;
    public Mvc2Service() {
        System.out.println("实例化");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁");
    }

    public String find() {
        return mvc2Dao.select();
    }
}
