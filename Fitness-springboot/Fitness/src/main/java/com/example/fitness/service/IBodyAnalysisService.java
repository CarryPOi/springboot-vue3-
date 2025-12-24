package com.example.fitness.service;

import com.example.fitness.entity.BodyData;
import java.util.Map;

/**
 * 身材数据AI分析服务接口
 */
public interface IBodyAnalysisService {
    /**
     * 根据最新的身材数据进行深度分析
     * @param username 用户名
     * @return 分析结果（包含建议、风险提示、BMI等）
     */
    Map<String, Object> analyzeBodyData(String username);
}
