package com.example.fitness.controller;

import com.example.fitness.entity.WorkoutLog;
import com.example.fitness.service.IWorkoutLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workout-log")
@CrossOrigin(origins = "*")
public class WorkoutLogController {

    @Autowired
    private IWorkoutLogService workoutLogService;

    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<WorkoutLog> list = workoutLogService.getByUsername(username);
            result.put("code", 0);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody WorkoutLog workoutLog) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (workoutLog.getCreateTime() == null) {
                workoutLog.setCreateTime(LocalDateTime.now());
            }
            boolean success = workoutLogService.saveOrUpdate(workoutLog);
            result.put("code", success ? 0 : 1);
            result.put("msg", success ? "保存成功" : "保存失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> stats = workoutLogService.getWorkoutStats(username);
            result.put("code", 0);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = workoutLogService.removeById(id);
            result.put("code", success ? 0 : 1);
            result.put("msg", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
