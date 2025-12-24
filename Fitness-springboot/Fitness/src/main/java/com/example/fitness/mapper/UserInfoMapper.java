package com.example.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fitness.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 原有UserInfoMapper改造（仅新增按用户名查询方法）
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    // 复用MyBatis-Plus默认方法，无需额外编写SQL

    // ========== 新增：按用户名查询用户信息（核心方法） ==========
    @Select("SELECT * FROM user_info WHERE username = #{username}")
    UserInfo selectByUsername(String username);
}