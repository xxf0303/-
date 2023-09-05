package com.example.mvc2.dao;

import org.springframework.stereotype.Repository;

@Repository("mvc")
public class Mvc2DaoImpl implements Mvc2Dao{
    @Override
    public String select() {
        return "Mvc2DaoImpl";
    }
}
