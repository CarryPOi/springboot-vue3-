package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.Course;
import java.util.List;
import java.util.Map;

public interface ICourseService extends IService<Course> {
    List<Map<String, Object>> getCourseListWithSubscription(String username);
    List<Map<String, Object>> getAdminCourseList();
    List<Map<String, Object>> getCourseSubscribers(Long courseId);
}
