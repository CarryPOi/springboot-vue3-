package com.example.fitness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fitness.common.Result;
import com.example.fitness.entity.Knowledge;
import com.example.fitness.service.IKnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private IKnowledgeService knowledgeService;

    @GetMapping("/list")
    public Result<List<Knowledge>> list(@RequestParam(required = false) String category) {
        LambdaQueryWrapper<Knowledge> queryWrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(Knowledge::getCategory, category);
        }
        queryWrapper.eq(Knowledge::getStatus, "已发布");
        queryWrapper.orderByDesc(Knowledge::getCreateTime);
        return Result.success(knowledgeService.list(queryWrapper));
    }

    @GetMapping("/admin/list")
    public Result<List<Knowledge>> adminList() {
        LambdaQueryWrapper<Knowledge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Knowledge::getCreateTime);
        return Result.success(knowledgeService.list(queryWrapper));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Knowledge knowledge) {
        if (knowledge.getId() == null) {
            knowledge.setCreateTime(LocalDateTime.now());
        }
        knowledge.setUpdateTime(LocalDateTime.now());
        return knowledgeService.saveOrUpdate(knowledge) ? Result.success() : Result.fail("保存失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return knowledgeService.removeById(id) ? Result.success() : Result.fail("删除失败");
    }

    @GetMapping("/{id}")
    public Result<Knowledge> getById(@PathVariable Long id) {
        return Result.success(knowledgeService.getById(id));
    }
}
