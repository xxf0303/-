package com.example.mvc2.util;

import com.example.mvc2.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();
    public void setUser(User user) {
        users.set(user);
    }
    public User getUser() {
        return users.get();
    }
    public void clear() {
        users.remove();
    }
 }
