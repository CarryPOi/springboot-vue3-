package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 身材数据历史记录实体类（适配body_data表）
 */
@Data
@TableName("body_data") // 关联数据库表名
public class BodyData {
    @TableId(type = IdType.AUTO) // 主键自增
    private Long id; // 记录ID（对应数据库id）
    private String username; // 关联用户名（对应username）
    private BigDecimal weight; // 体重(kg)（对应weight）
    private BigDecimal bodyFatRate; // 体脂率(%)（对应body_fat_rate）
    private BigDecimal waistCircumference; // 腰围(cm)（对应waist_circumference）
    private BigDecimal hipCircumference; // 臀围(cm)（对应hip_circumference）
    private LocalDate recordDate; // 记录日期（对应record_date，LocalDate适配DATE类型）
    private String remark; // 备注（对应remark）
    private LocalDateTime createTime; // 创建时间（对应create_time）
    private LocalDateTime updateTime; // 更新时间（对应update_time）
}