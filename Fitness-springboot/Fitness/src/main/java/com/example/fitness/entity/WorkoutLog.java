package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 训练打卡记录实体类
 */
@Data
@TableName("workout_logs")
public class WorkoutLog {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 用户名 */
    private String username;

    /** 关联计划ID (可选) */
    private Integer planId;

    /** 训练名称 */
    private String workoutName;

    /** 训练类型 */
    private String type;

    /** 时长 (分钟) */
    private Integer duration;

    /** 消耗热量 (kcal) */
    private Integer calories;

    /** 训练强度 (轻松/适中/高强度) */
    private String intensity;

    /** 训练详情 (JSON格式) */
    private String content;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 创建时间 */
    private LocalDateTime createTime;
}
