package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.Comment;

public interface ICommentService extends IService<Comment> {
    boolean deleteSafety(Long commentId, Long currentUserId);
}