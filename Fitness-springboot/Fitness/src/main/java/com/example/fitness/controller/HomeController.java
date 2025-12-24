package com.example.fitness.controller;

import com.example.fitness.entity.BodyData;
import com.example.fitness.entity.UserInfo;
import com.example.fitness.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页数据聚合控制器
 */
@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")
public class HomeController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private IDailyDietService dailyDietService;

    @Autowired
    private ICourseSubscriptionService subscriptionService;

    @Autowired
    private BodyDataService bodyDataService;

    @Autowired
    private IWorkoutLogService workoutLogService;

    @GetMapping("/data")
    public Map<String, Object> getHomeData(@RequestParam String username, @RequestParam(required = false) String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDate recordDate = (date == null) ? LocalDate.now() : LocalDate.parse(date);

            // 1. 获取用户信息
            UserInfo userInfo = userInfoService.getUserInfoByUsername(username);

            // 2. 获取今日饮食统计
            Map<String, Object> dietStats = dailyDietService.getDailyStats(username, recordDate);

            // 3. 获取订阅课程数量
            long courseCount = subscriptionService.lambdaQuery()
                    .eq(com.example.fitness.entity.CourseSubscription::getUsername, username)
                    .count();

            // 4. 获取最新身材数据
            List<BodyData> bodyDataList = bodyDataService.getByUsername(username);
            BodyData latestBodyData = null;
            if (bodyDataList != null && !bodyDataList.isEmpty()) {
                latestBodyData = bodyDataList.get(0);
            }

            // 5. 获取今日训练打卡
            List<com.example.fitness.entity.WorkoutLog> todayWorkouts = workoutLogService.lambdaQuery()
                    .eq(com.example.fitness.entity.WorkoutLog::getUsername, username)
                    .eq(com.example.fitness.entity.WorkoutLog::getRecordDate, recordDate)
                    .list();

            Map<String, Object> data = new HashMap<>();
            data.put("userInfo", userInfo);
            data.put("dietStats", dietStats);
            data.put("courseCount", courseCount);
            data.put("latestBodyData", latestBodyData);
            data.put("todayWorkouts", todayWorkouts);

            result.put("code", 0);
            result.put("msg", "获取首页数据成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "获取首页数据失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
