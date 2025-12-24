package com.example.fitness.controller;

import com.example.fitness.common.Result;
import com.example.fitness.entity.DietRecommendation;
import com.example.fitness.service.IDietRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/diet-recommendations")
@CrossOrigin(origins = "*")
public class DietRecommendationController {

    @Autowired
    private IDietRecommendationService dietRecommendationService;

    @GetMapping("/list")
    public Result<List<DietRecommendation>> list() {
        return Result.success(dietRecommendationService.list());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody DietRecommendation recommendation) {
        if (recommendation.getCreateTime() == null) {
            recommendation.setCreateTime(LocalDateTime.now());
        }
        return dietRecommendationService.save(recommendation) ? Result.success() : Result.fail("发布失败");
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody DietRecommendation recommendation) {
        return dietRecommendationService.updateById(recommendation) ? Result.success() : Result.fail("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return dietRecommendationService.removeById(id) ? Result.success() : Result.fail("删除失败");
    }
}
