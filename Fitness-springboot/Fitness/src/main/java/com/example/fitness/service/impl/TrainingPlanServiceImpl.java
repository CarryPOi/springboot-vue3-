package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.TrainingPlan;
import com.example.fitness.mapper.TrainingPlanMapper;
import com.example.fitness.service.TrainingPlanService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 训练计划服务层实现类
 */
@Service
public class TrainingPlanServiceImpl extends ServiceImpl<TrainingPlanMapper, TrainingPlan>
        implements TrainingPlanService {

    /**
     * 新增训练计划
     */
    @Override
    public boolean addTrainingPlan(TrainingPlan plan) {
        // 设置创建/更新时间
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        // 调用MyBatis-Plus的保存方法
        return save(plan);
    }

    /**
     * 根据用户名查询计划列表
     */
    @Override
    public List<TrainingPlan> getPlanListByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    /**
     * 编辑训练计划
     */
    @Override
    public boolean editTrainingPlan(TrainingPlan plan) {
        // 更新修改时间
        plan.setUpdateTime(LocalDateTime.now());
        // 根据ID更新
        return updateById(plan);
    }

    /**
     * 删除训练计划
     */
    @Override
    public boolean deleteTrainingPlan(Integer id) {
        // 根据ID删除
        return removeById(id);
    }
}