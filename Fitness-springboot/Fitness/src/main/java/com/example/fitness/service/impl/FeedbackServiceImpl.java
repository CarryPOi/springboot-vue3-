package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.Feedback;
import com.example.fitness.mapper.FeedbackMapper;
import com.example.fitness.service.IFeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {
}
