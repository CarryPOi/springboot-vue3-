package com.example.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fitness.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 必须继承BaseMapper<User>，且@Mapper注解不能少
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 无需写任何方法，BaseMapper已封装CRUD
}