package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id; // 匹配 user 表的 BIGINT 类型

    private Long userId;     // 发布者ID
    private String content;  // 评论内容
    private Long parentId;   // 父评论ID (回复他人)
    private Long replyUserId; // 被回复者ID
    private String images;    // 图片列表 (逗号分隔)

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Integer likeCount;

    @TableLogic
    private Integer isDeleted; // 逻辑删除

    // 辅助字段：用于在前端直接显示用户名和头像
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private String avatarUrl;

    @TableField(exist = false)
    private String replyUsername;

    @TableField(exist = false)
    private String replyRealName;

    @TableField(exist = false)
    private java.util.List<Comment> children;
}