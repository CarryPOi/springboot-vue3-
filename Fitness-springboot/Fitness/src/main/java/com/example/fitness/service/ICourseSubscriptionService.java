package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.CourseSubscription;

public interface ICourseSubscriptionService extends IService<CourseSubscription> {
    boolean subscribe(String username, Long courseId);
    boolean unsubscribe(String username, Long courseId);
}
