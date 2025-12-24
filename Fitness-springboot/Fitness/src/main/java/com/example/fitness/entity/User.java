package com.example.fitness.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO) // 必须加！
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role; // 新增：角色 (ADMIN/USER)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}