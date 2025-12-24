package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.CourseSubscription;
import com.example.fitness.mapper.CourseSubscriptionMapper;
import com.example.fitness.service.ICourseSubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class CourseSubscriptionServiceImpl extends ServiceImpl<CourseSubscriptionMapper, CourseSubscription> implements ICourseSubscriptionService {

    @Override
    public boolean subscribe(String username, Long courseId) {
        CourseSubscription subscription = new CourseSubscription();
        subscription.setUsername(username);
        subscription.setCourseId(courseId);
        subscription.setSubscribeTime(java.time.LocalDateTime.now());
        return this.save(subscription);
    }

    @Override
    public boolean unsubscribe(String username, Long courseId) {
        return this.remove(new LambdaQueryWrapper<CourseSubscription>()
            .eq(CourseSubscription::getUsername, username)
            .eq(CourseSubscription::getCourseId, courseId));
    }
}
