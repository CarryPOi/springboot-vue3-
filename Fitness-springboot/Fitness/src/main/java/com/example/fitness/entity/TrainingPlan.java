package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 训练计划实体类，对应数据库 training_plans 表
 */
@Data
@TableName("training_plans")
public class TrainingPlan {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 所属用户名（关联用户表） */
    private String username;

    /** 计划名称 */
    private String name;

    /** 训练类型（力量训练/有氧运动等） */
    private String type;

    /** 单次时长（分钟） */
    private Integer duration;

    /** 训练频率（每日/每周3次等） */
    private String frequency;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // 自动填充创建/更新时间（可选，也可在Service层手动设置）
    public void setCreateTime() {
        this.createTime = LocalDateTime.now();
    }

    public void setUpdateTime() {
        this.updateTime = LocalDateTime.now();
    }
}