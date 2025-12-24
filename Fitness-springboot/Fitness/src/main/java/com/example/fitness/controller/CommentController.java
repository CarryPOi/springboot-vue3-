package com.example.fitness.controller;

import com.example.fitness.common.Result;
import com.example.fitness.entity.Comment;
import com.example.fitness.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 论坛评论控制层
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    /**
     * 发布评论
     * @param comment 评论实体
     * @return 统一返回结果
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Comment comment) {
        // 强制设置时间，确保即使自动填充失效也能存入时间
        if (comment.getCreateTime() == null) {
            comment.setCreateTime(java.time.LocalDateTime.now());
        }
        System.out.println("正在保存评论: " + comment);
        boolean saved = commentService.save(comment);
        System.out.println("保存结果: " + saved + ", 评论ID: " + comment.getId() + ", 时间: " + comment.getCreateTime());
        return saved ? Result.success() : Result.fail("发布失败");
    }

    /**
     * 获取所有评论列表
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result<List<Comment>> list() {
        return Result.success(commentService.list());
    }

    /**
     * 删除评论（带权限校验：只能删除自己的评论）
     * @param id 评论ID
     * @param userId 当前操作用户ID
     * @return 统一返回结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestParam Long userId) {

        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.fail("该评论不存在或已被删除");
        }

        boolean ok = commentService.deleteSafety(id, userId);
        return ok ? Result.success() : Result.fail("无权限删除该评论");
    }

    /**
     * 点赞评论
     * @param id 评论ID
     * @return 统一返回结果
     */
    @PostMapping("/{id}/like")
    public Result<Integer> like(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.fail("评论不存在");
        }
        
        int newLikeCount = (comment.getLikeCount() == null ? 0 : comment.getLikeCount()) + 1;
        comment.setLikeCount(newLikeCount);
        commentService.updateById(comment);
        
        return Result.success(newLikeCount);
    }

}