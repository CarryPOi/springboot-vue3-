package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.Knowledge;
import com.example.fitness.mapper.KnowledgeMapper;
import com.example.fitness.service.IKnowledgeService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {
}
