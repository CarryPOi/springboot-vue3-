package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.Course;
import com.example.fitness.entity.CourseSubscription;
import com.example.fitness.mapper.CourseMapper;
import com.example.fitness.mapper.CourseSubscriptionMapper;
import com.example.fitness.service.ICourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private CourseSubscriptionMapper subscriptionMapper;

    @Autowired
    private com.example.fitness.service.UserInfoService userInfoService;

    @Override
    public List<Map<String, Object>> getCourseListWithSubscription(String username) {
        // 获取所有课程
        List<Course> courses = this.list();
        
        // 获取用户已订阅的课程ID
        List<CourseSubscription> subscriptions = subscriptionMapper.selectList(
            new LambdaQueryWrapper<CourseSubscription>().eq(CourseSubscription::getUsername, username)
        );
        Set<Long> subscribedCourseIds = subscriptions.stream()
            .map(CourseSubscription::getCourseId)
            .collect(Collectors.toSet());

        // 组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course course : courses) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", course.getId());
            map.put("title", course.getTitle());
            map.put("description", course.getDescription());
            map.put("trainer", course.getTrainer());
            map.put("duration", course.getDuration());
            map.put("difficulty", course.getDifficulty());
            map.put("category", course.getCategory());
            map.put("coverImage", course.getCoverImage());
            map.put("isSubscribed", subscribedCourseIds.contains(course.getId()));
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAdminCourseList() {
        List<Course> courses = this.list();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Course course : courses) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", course.getId());
            map.put("title", course.getTitle());
            map.put("description", course.getDescription());
            map.put("trainer", course.getTrainer());
            map.put("duration", course.getDuration());
            map.put("difficulty", course.getDifficulty());
            map.put("category", course.getCategory());
            map.put("coverImage", course.getCoverImage());
            map.put("createTime", course.getCreateTime());
            
            // 获取订阅人数
            Long subCount = subscriptionMapper.selectCount(
                new LambdaQueryWrapper<CourseSubscription>().eq(CourseSubscription::getCourseId, course.getId())
            );
            map.put("subscriptionCount", subCount);
            
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCourseSubscribers(Long courseId) {
        List<CourseSubscription> subscriptions = subscriptionMapper.selectList(
            new LambdaQueryWrapper<CourseSubscription>().eq(CourseSubscription::getCourseId, courseId)
        );
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseSubscription sub : subscriptions) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("username", sub.getUsername());
            map.put("subscribeTime", sub.getSubscribeTime());
            
            // 获取用户详细信息
            com.example.fitness.entity.UserInfo userInfo = userInfoService.getOne(
                new LambdaQueryWrapper<com.example.fitness.entity.UserInfo>().eq(com.example.fitness.entity.UserInfo::getUsername, sub.getUsername())
            );
            if (userInfo != null) {
                map.put("realName", userInfo.getRealName());
                map.put("avatarUrl", userInfo.getAvatarUrl());
            }
            
            result.add(map);
        }
        return result;
    }
}
