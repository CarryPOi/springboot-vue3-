package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_info") // 对应数据库表名
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;       // 用户名（唯一）
    private String realName;       // 真实姓名
    private Integer age;           // 年龄
    private BigDecimal height;     // 身高(cm)
    private BigDecimal weight;     // 体重(kg)
    private BigDecimal bodyFatRate;// 新增：最新体脂率(%)
    private BigDecimal waistCircumference; // 新增：最新腰围(cm)
    private BigDecimal hipCircumference;   // 新增：最新臀围(cm)
    private String phone;          // 手机号
    private String trainingPlan;   // 训练计划（手动填写）
    private Integer trainingDuration; // 训练时长（分钟）
    private String trainingType;   // 训练类型
    private LocalDateTime lastRecordTime;  // 新增：最后记录时间
    private LocalDateTime updateTime;      // 更新时间
    private String avatarUrl;      // 头像地址（静态资源URL）
}
