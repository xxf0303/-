package com.example.mvc2;

import com.example.mvc2.dao.DiscussPostMapper;
import com.example.mvc2.dao.LoginTicketMapper;
import com.example.mvc2.dao.UserMapper;
import com.example.mvc2.entity.DiscussPost;
import com.example.mvc2.entity.LoginTicket;
import com.example.mvc2.entity.User;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = Mvc2Application.class)
public class MapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void test1() {
      userMapper.updateStatus(1,2);
    }

    @Test
    public void test2() {
       loginTicketMapper.updateStatus("abc",0);
    }
}
