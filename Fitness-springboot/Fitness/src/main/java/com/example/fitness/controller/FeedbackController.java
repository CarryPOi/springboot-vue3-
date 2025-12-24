package com.example.fitness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fitness.common.Result;
import com.example.fitness.entity.Feedback;
import com.example.fitness.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    /**
     * 用户提交反馈
     */
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody Feedback feedback) {
        feedback.setStatus("待处理");
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setUpdateTime(LocalDateTime.now());
        return feedbackService.save(feedback) ? Result.success() : Result.fail("提交失败");
    }

    /**
     * 管理员获取反馈列表
     */
    @GetMapping("/list")
    public Result<List<Feedback>> list() {
        LambdaQueryWrapper<Feedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Feedback::getCreateTime);
        return Result.success(feedbackService.list(queryWrapper));
    }

    /**
     * 管理员处理反馈
     */
    @PutMapping("/process/{id}")
    public Result<Void> process(@PathVariable Long id) {
        Feedback feedback = feedbackService.getById(id);
        if (feedback == null) {
            return Result.fail("反馈不存在");
        }
        feedback.setStatus("已处理");
        feedback.setUpdateTime(LocalDateTime.now());
        return feedbackService.updateById(feedback) ? Result.success() : Result.fail("处理失败");
    }
}
