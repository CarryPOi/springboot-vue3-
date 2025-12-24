package com.example.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fitness.entity.BodyData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 身材数据Mapper（操作body_data表）
 */
@Mapper
public interface BodyDataMapper extends BaseMapper<BodyData> {

    /**
     * 根据用户名查询历史身材记录（按记录日期倒序）
     * @param username 用户名
     * @return 该用户的所有身材记录
     */
    @Select("SELECT * FROM body_data WHERE username = #{username} ORDER BY record_date DESC")
    List<BodyData> selectByUsername(String username);
}