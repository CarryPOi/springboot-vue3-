package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notice")
public class Notice {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;       // 公告标题
    private String content;     // 公告内容
    private String author;      // 发布者
    
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime; // 发布时间
    
    private Integer type;       // 公告类型 (0: 系统公告, 1: 活动通知, 2: 维护通知)
    private Integer status;     // 状态 (0: 草稿, 1: 已发布)
}
