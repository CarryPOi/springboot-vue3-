package com.example.fitness.controller;

import com.example.fitness.entity.TrainingPlan;
import com.example.fitness.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 训练计划控制器，对接前端请求
 */
@RestController
@RequestMapping("/api/plan")
@CrossOrigin(origins = "http://localhost:8081") // 允许前端跨域访问
public class TrainingPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    /**
     * 新增训练计划
     */
    @PostMapping("/add")
    public Map<String, Object> addPlan(@RequestBody TrainingPlan plan) {
        Map<String, Object> result = new HashMap<>();
        // 参数校验
        if (plan.getUsername() == null || plan.getName() == null ||
                plan.getType() == null || plan.getDuration() == null || plan.getFrequency() == null) {
            result.put("code", 1);
            result.put("msg", "参数不全，请填写完整信息");
            return result;
        }
        // 调用服务层新增
        boolean success = trainingPlanService.addTrainingPlan(plan);
        if (success) {
            result.put("code", 0);
            result.put("msg", "训练计划新增成功");
            result.put("data", plan); // 返回新增的计划（含ID）
        } else {
            result.put("code", 1);
            result.put("msg", "训练计划新增失败");
        }
        return result;
    }

    /**
     * 根据用户名查询计划列表
     */
    @GetMapping("/list")
    public Map<String, Object> getPlanList(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        List<TrainingPlan> planList = trainingPlanService.getPlanListByUsername(username);
        result.put("code", 0);
        result.put("msg", "查询成功");
        result.put("data", planList);
        return result;
    }

    /**
     * 编辑训练计划
     */
    @PostMapping("/edit")
    public Map<String, Object> editPlan(@RequestBody TrainingPlan plan) {
        Map<String, Object> result = new HashMap<>();
        // 参数校验（ID为必传）
        if (plan.getId() == null || plan.getName() == null ||
                plan.getType() == null || plan.getDuration() == null || plan.getFrequency() == null) {
            result.put("code", 1);
            result.put("msg", "参数不全，请填写完整信息");
            return result;
        }
        // 调用服务层编辑
        boolean success = trainingPlanService.editTrainingPlan(plan);
        if (success) {
            result.put("code", 0);
            result.put("msg", "训练计划编辑成功");
        } else {
            result.put("code", 1);
            result.put("msg", "训练计划编辑失败");
        }
        return result;
    }

    /**
     * 删除训练计划
     */
    @PostMapping("/delete")
    public Map<String, Object> deletePlan(@RequestBody Map<String, Integer> params) {
        Map<String, Object> result = new HashMap<>();
        Integer id = params.get("id");
        if (id == null) {
            result.put("code", 1);
            result.put("msg", "缺少计划ID，删除失败");
            return result;
        }
        // 调用服务层删除
        boolean success = trainingPlanService.deleteTrainingPlan(id);
        if (success) {
            result.put("code", 0);
            result.put("msg", "训练计划删除成功");
        } else {
            result.put("code", 1);
            result.put("msg", "训练计划删除失败");
        }
        return result;
    }

    /**
     * 获取用户训练统计数据（首页仪表盘使用）
     */
    @GetMapping("/stats")
    public Map<String, Object> getTrainingStats(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取用户所有训练计划
            List<TrainingPlan> planList = trainingPlanService.getPlanListByUsername(username);
            
            // 统计数据
            int planCount = planList.size();
            int totalDuration = 0;
            Map<String, Integer> typeCount = new HashMap<>();
            
            for (TrainingPlan plan : planList) {
                // 累计总时长
                if (plan.getDuration() != null) {
                    totalDuration += plan.getDuration();
                }
                // 统计各类型数量
                String type = plan.getType();
                if (type != null) {
                    typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
                }
            }
            
            // 构建类型占比数据（供饼图使用）
            List<Map<String, Object>> typeDistribution = new java.util.ArrayList<>();
            for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getKey());
                item.put("value", entry.getValue());
                typeDistribution.add(item);
            }
            
            // 返回统计结果
            Map<String, Object> stats = new HashMap<>();
            stats.put("planCount", planCount);                    // 训练计划数量
            stats.put("totalDuration", totalDuration);            // 总训练时长（分钟）
            stats.put("typeDistribution", typeDistribution);      // 类型分布
            stats.put("planList", planList);                      // 计划列表（用于详情展示）
            
            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", stats);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }
}