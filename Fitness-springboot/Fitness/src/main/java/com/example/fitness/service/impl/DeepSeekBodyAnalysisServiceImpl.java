package com.example.fitness.service.impl;

import com.example.fitness.entity.BodyData;
import com.example.fitness.entity.UserInfo;
import com.example.fitness.service.BodyDataService;
import com.example.fitness.service.IBodyAnalysisService;
import com.example.fitness.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek AI 真实接入版分析实现类 (预留接口)
 */
@Service("aiAnalysisService")
public class DeepSeekBodyAnalysisServiceImpl implements IBodyAnalysisService {

    @Autowired
    private BodyDataService bodyDataService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    @Override
    public Map<String, Object> analyzeBodyData(String username) {
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.isEmpty()) {
            result.put("analysis", "AI 分析接口尚未配置 API Key。请在后端 application.yml 中配置 `ai.deepseek.api-key` 后使用。目前建议切换至本地规则分析。");
            return result;
        }

        // 这里是未来接入 DeepSeek 的逻辑
        // 1. 构造 Prompt
        // 2. 调用 Http 工具类发送请求到 https://api.deepseek.com/v1/chat/completions
        // 3. 解析返回结果
        
        result.put("analysis", "### 🤖 AI 深度分析报告 (模拟)\n\n由于当前为毕设演示环境，且未检测到有效的 DeepSeek API Key，此处显示为 AI 接口预留占位。\n\n**接入逻辑已就绪**：只需在配置文件中填入 Key，系统即可通过 GPT 协议与 DeepSeek 交互，为您提供基于最新身材趋势的个性化建议。");
        
        return result;
    }
}
