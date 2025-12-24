package com.example.fitness.controller;

import com.example.fitness.service.ICourseService;
import com.example.fitness.service.ICourseSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICourseSubscriptionService subscriptionService;

    // 获取课程列表（带订阅状态）
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("code", 0);
            result.put("data", courseService.getCourseListWithSubscription(username));
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "获取课程列表失败");
        }
        return result;
    }

    // 订阅课程
    @PostMapping("/subscribe")
    public Map<String, Object> subscribe(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = (String) params.get("username");
            Long courseId = Long.valueOf(params.get("courseId").toString());
            if (subscriptionService.subscribe(username, courseId)) {
                result.put("code", 0);
                result.put("msg", "订阅成功");
            } else {
                result.put("code", 1);
                result.put("msg", "订阅失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "订阅失败: " + e.getMessage());
        }
        return result;
    }

    // 取消订阅
    @PostMapping("/unsubscribe")
    public Map<String, Object> unsubscribe(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = (String) params.get("username");
            Long courseId = Long.valueOf(params.get("courseId").toString());
            if (subscriptionService.unsubscribe(username, courseId)) {
                result.put("code", 0);
                result.put("msg", "取消订阅成功");
            } else {
                result.put("code", 1);
                result.put("msg", "取消订阅失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "取消订阅失败");
        }
        return result;
    }
}
