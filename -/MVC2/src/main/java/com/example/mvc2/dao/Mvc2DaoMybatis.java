package com.example.mvc2.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("mybatis")
@Primary
public class Mvc2DaoMybatis implements Mvc2Dao{
    @Override
    public String select() {
        return "Mybatis";
    }
}
