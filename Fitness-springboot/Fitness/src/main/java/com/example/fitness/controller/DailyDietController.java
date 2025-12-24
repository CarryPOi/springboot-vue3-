package com.example.fitness.controller;

import com.example.fitness.entity.DailyDiet;
import com.example.fitness.service.IDailyDietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diet")
@CrossOrigin(origins = "*")
public class DailyDietController {

    @Autowired
    private IDailyDietService dailyDietService;

    // 获取某日的饮食记录列表
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam String username, @RequestParam(required = false) String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
            List<DailyDiet> list = dailyDietService.getListByUsernameAndDate(username, localDate);
            result.put("code", 0);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "获取饮食记录失败");
        }
        return result;
    }

    // 获取某日的营养统计
    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestParam String username, @RequestParam(required = false) String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
            Map<String, Object> stats = dailyDietService.getDailyStats(username, localDate);
            result.put("code", 0);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "获取统计数据失败");
        }
        return result;
    }

    // 获取历史统计 (最近7天)
    @GetMapping("/history")
    public Map<String, Object> history(@RequestParam String username, @RequestParam(defaultValue = "7") int days) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> history = dailyDietService.getHistoryStats(username, days);
            result.put("code", 0);
            result.put("data", history);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "获取历史记录失败");
        }
        return result;
    }

    // 添加饮食记录
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody DailyDiet dailyDiet) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (dailyDiet.getRecordDate() == null) {
                dailyDiet.setRecordDate(LocalDate.now());
            }
            if (dailyDietService.save(dailyDiet)) {
                result.put("code", 0);
                result.put("msg", "添加成功");
            } else {
                result.put("code", 1);
                result.put("msg", "添加失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "系统错误: " + e.getMessage());
        }
        return result;
    }

    // 删除记录
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (dailyDietService.removeById(id)) {
                result.put("code", 0);
                result.put("msg", "删除成功");
            } else {
                result.put("code", 1);
                result.put("msg", "删除失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "系统错误");
        }
        return result;
    }
}
