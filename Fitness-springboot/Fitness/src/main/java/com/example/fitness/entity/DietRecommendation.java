package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("diet_recommendations")
public class DietRecommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String mealType;
    private Double calories;
    private String author;
    private LocalDateTime createTime;
}
