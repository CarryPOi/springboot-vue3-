package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.TrainingPlan;
import java.util.List;

/**
 * 训练计划服务层接口
 */
public interface TrainingPlanService extends IService<TrainingPlan> {
    /**
     * 新增训练计划
     * @param plan 训练计划对象
     * @return 新增是否成功
     */
    boolean addTrainingPlan(TrainingPlan plan);

    /**
     * 根据用户名查询训练计划列表
     * @param username 用户名
     * @return 训练计划列表
     */
    List<TrainingPlan> getPlanListByUsername(String username);

    /**
     * 编辑训练计划
     * @param plan 训练计划对象（含ID）
     * @return 编辑是否成功
     */
    boolean editTrainingPlan(TrainingPlan plan);

    /**
     * 删除训练计划
     * @param id 计划ID
     * @return 删除是否成功
     */
    boolean deleteTrainingPlan(Integer id);
}