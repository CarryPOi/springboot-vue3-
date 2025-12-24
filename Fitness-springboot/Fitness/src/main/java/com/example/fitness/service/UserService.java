package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.User;

/**
 * 用户服务接口：继承MyBatis-Plus的IService，获得基础CRUD方法
 */
public interface UserService extends IService<User> {
    // 注册方法
    boolean register(User user);
    // 登录方法
    User login(String username, String password);
}