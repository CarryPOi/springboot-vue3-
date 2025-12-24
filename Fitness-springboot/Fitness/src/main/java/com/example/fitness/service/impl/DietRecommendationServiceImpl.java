package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.DietRecommendation;
import com.example.fitness.mapper.DietRecommendationMapper;
import com.example.fitness.service.IDietRecommendationService;
import org.springframework.stereotype.Service;

@Service
public class DietRecommendationServiceImpl extends ServiceImpl<DietRecommendationMapper, DietRecommendation> implements IDietRecommendationService {
}
