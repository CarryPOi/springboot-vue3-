package com.example.fitness.service.impl;

import com.example.fitness.entity.BodyData;
import com.example.fitness.entity.UserInfo;
import com.example.fitness.service.BodyDataService;
import com.example.fitness.service.IBodyAnalysisService;
import com.example.fitness.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地规则版身材分析实现类
 */
@Service("localAnalysisService")
public class LocalBodyAnalysisServiceImpl implements IBodyAnalysisService {

    @Autowired
    private BodyDataService bodyDataService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public Map<String, Object> analyzeBodyData(String username) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 获取用户信息（主要是身高）
        UserInfo userInfo = userInfoService.getUserInfoByUsername(username);
        // 2. 获取最新身材数据
        List<BodyData> history = bodyDataService.getByUsername(username);
        
        if (history == null || history.isEmpty()) {
            result.put("analysis", "暂无身材记录，无法进行分析。请先添加一条身材数据。");
            return result;
        }
        
        BodyData latest = history.get(0);
        BigDecimal weight = latest.getWeight();
        BigDecimal height = userInfo != null && userInfo.getHeight() != null ? userInfo.getHeight() : null;
        BigDecimal bodyFat = latest.getBodyFatRate();

        StringBuilder analysis = new StringBuilder();
        analysis.append("### 📊 身材现状深度分析\n\n");

        // BMI计算与评价
        if (height != null && weight != null) {
            // BMI = weight(kg) / (height(m)^2)
            BigDecimal heightM = height.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = weight.divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
            result.put("bmi", bmi);
            
            analysis.append("您的当前BMI为 **").append(bmi).append("**。");
            if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                analysis.append("属于偏瘦体型。建议适当增加优质碳水和蛋白质的摄入。\n");
            } else if (bmi.compareTo(new BigDecimal("24")) < 0) {
                analysis.append("属于正常标准体型。请继续保持良好的饮食和运动习惯。\n");
            } else if (bmi.compareTo(new BigDecimal("28")) < 0) {
                analysis.append("属于超重范围。建议控制热量摄入，增加中等强度的有氧运动。\n");
            } else {
                analysis.append("属于肥胖范围。建议进行系统性的减脂训练，并咨询专业教练。\n");
            }
        }

        // 体脂率分析
        if (bodyFat != null) {
            analysis.append("\n**体脂率分析**：当前体脂率为 **").append(bodyFat).append("%**。");
            if (bodyFat.compareTo(new BigDecimal("15")) < 0) {
                analysis.append("体脂较低，肌肉线条可能较为明显。建议维持当前的训练强度。\n");
            } else if (bodyFat.compareTo(new BigDecimal("25")) < 0) {
                analysis.append("体脂处于健康区间。如果目标是增肌，可以适当增加抗阻训练。\n");
            } else {
                analysis.append("体脂率略高。建议结合力量训练和有氧运动，提高基础代谢。\n");
            }
        }

        // 综合建议
        analysis.append("\n### 💡 健身建议\n");
        analysis.append("1. **饮食方案**：保持高蛋白、适量碳水、低脂肪的饮食结构。\n");
        analysis.append("2. **训练重点**：建议每周进行3-4次力量训练，配合2次有氧运动。\n");
        analysis.append("3. **生活习惯**：保证充足的睡眠（7-8小时），有助于肌肉修复和脂肪代谢。\n");

        result.put("analysis", analysis.toString());
        result.put("latestData", latest);
        
        return result;
    }
}
