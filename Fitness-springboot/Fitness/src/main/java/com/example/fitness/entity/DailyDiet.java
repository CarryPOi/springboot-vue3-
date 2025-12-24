package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_diet")
public class DailyDiet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String foodName;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double weight;
    private String mealType;
    private LocalDate recordDate;
    private LocalDateTime createTime;
}
