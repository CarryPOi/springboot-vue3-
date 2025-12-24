package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_subscription")
public class CourseSubscription {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private Long courseId;
    private LocalDateTime subscribeTime;
}
