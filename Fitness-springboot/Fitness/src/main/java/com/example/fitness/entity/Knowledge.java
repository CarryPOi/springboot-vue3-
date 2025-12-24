package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("fitness_knowledge")
public class Knowledge {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String category;
    private String coverUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
