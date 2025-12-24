package com.example.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fitness.entity.TrainingPlan;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 训练计划Mapper，基于MyBatis-Plus
 */
@Mapper
public interface TrainingPlanMapper extends BaseMapper<TrainingPlan> {
    /**
     * 根据用户名查询训练计划列表
     * @param username 用户名
     * @return 该用户的所有训练计划
     */
    List<TrainingPlan> selectByUsername(String username);
}