package com.example.fitness.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fitness.entity.DietRecommendation;
import com.example.fitness.entity.Notice;
import com.example.fitness.service.IDietRecommendationService;
import com.example.fitness.service.INoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private IDietRecommendationService dietRecommendationService;

    /**
     * 获取每日健身小知识
     */
    @GetMapping("/daily-tip")
    public Map<String, Object> getDailyTip() {
        Map<String, Object> result = new HashMap<>();
        String[] tips = {
            "深蹲是练腿之王，能带动全身肌肉生长。",
            "运动后30分钟内补充蛋白质，更有利于肌肉修复。",
            "充足的睡眠（7-8小时）是肌肉增长的关键。",
            "不要忽视热身，它能有效降低受伤风险并提升运动表现。",
            "保持水分充足，脱水会显著降低你的力量和耐力。",
            "减脂没有局部减脂，只有全身瘦了局部才会瘦。",
            "力量训练能提高基础代谢，让你在休息时也消耗热量。",
            "坚持比强度更重要，养成规律的健身习惯是成功的基石。",
            "拉伸不仅能缓解肌肉酸痛，还能改善关节活动范围。",
            "动作质量远比重量重要，确保动作标准才能最大化效果。",
            "记录你的进步，无论是重量还是体围，这会给你持续的动力。",
            "健身房不是唯一的锻炼场所，保持活跃的生活方式同样重要。",
            "听从身体的声音，如果感到剧烈疼痛，请及时休息或咨询专业人士。",
            "控制组间休息时间，这对于训练效果（增肌或耐力）至关重要。",
            "核心力量是所有运动的基础，每周安排1-2次核心训练。",
            "保持耐心，身体的改变需要时间，坚持3个月你会看到明显的不同。"
        };
        int index = (int) (Math.random() * tips.length);
        result.put("code", 0);
        result.put("data", tips[index]);
        return result;
    }

    /**
     * 获取饮食推荐
     */
    @GetMapping("/diet-recommendations")
    public Map<String, Object> getDietRecommendations() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<DietRecommendation> list = dietRecommendationService.list();
            if (list.isEmpty()) {
                // 如果数据库没数据，返回硬编码数据供演示
                List<Map<String, String>> mockDiets = new ArrayList<>();
                
                Map<String, String> d1 = new HashMap<>();
                d1.put("title", "增肌早餐推荐");
                d1.put("content", "全麦面包2片 + 鸡蛋2个 + 牛奶250ml + 香蕉1根。");
                mockDiets.add(d1);

                Map<String, String> d2 = new HashMap<>();
                d2.put("title", "减脂午餐推荐");
                d2.put("content", "香煎鸡胸肉150g + 糙米饭100g + 西兰花/芦笋200g。");
                mockDiets.add(d2);

                Map<String, String> d3 = new HashMap<>();
                d3.put("title", "运动前能量补充");
                d3.put("content", "一片涂有花生酱的全麦面包或一个小红薯，提前1小时摄入。");
                mockDiets.add(d3);

                Map<String, String> d4 = new HashMap<>();
                d4.put("title", "运动后修复晚餐");
                d4.put("content", "清蒸三文鱼150g + 土豆泥150g + 混合生菜沙拉（橄榄油调味）。");
                mockDiets.add(d4);

                Map<String, String> d5 = new HashMap<>();
                d5.put("title", "健康加餐推荐");
                d5.put("content", "无糖希腊酸奶150g + 蓝莓/坚果一把。");
                mockDiets.add(d5);

                Map<String, String> d6 = new HashMap<>();
                d6.put("title", "高蛋白晚点");
                d6.put("content", "一杯低脂奶酪或缓释蛋白粉，适合睡前1-2小时，防止肌肉分解。");
                mockDiets.add(d6);

                result.put("code", 0);
                result.put("data", mockDiets);
            } else {
                result.put("code", 0);
                result.put("data", list);
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "获取饮食推荐失败");
        }
        return result;
    }

    /**
     * 获取公告列表
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 只查询已发布的公告，按创建时间降序
            LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Notice::getStatus, 1)
                   .orderByDesc(Notice::getCreateTime);
            
            List<Notice> list = noticeService.list(wrapper);
            
            // 如果数据库没数据，模拟几条默认数据供演示
            if (list.isEmpty()) {
                result.put("code", 0);
                result.put("msg", "查询成功(模拟数据)");
                result.put("data", getMockNotices());
            } else {
                result.put("code", 0);
                result.put("msg", "查询成功");
                result.put("data", list);
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "查询失败：" + e.getMessage());
            // 发生异常时也返回模拟数据，保证前端不白屏
            result.put("data", getMockNotices());
        }
        return result;
    }

    private List<Notice> getMockNotices() {
        java.util.List<Notice> mocks = new java.util.ArrayList<>();
        
        Notice n1 = new Notice();
        n1.setId(1L);
        n1.setTitle("系统上线通知");
        n1.setContent("欢迎使用健身管理系统！在这里你可以记录你的训练计划、查看身体数据分析，还可以和大家一起交流健身心得。");
        n1.setAuthor("管理员");
        n1.setCreateTime(java.time.LocalDateTime.now().minusDays(2));
        n1.setType(0);
        n1.setStatus(1);
        mocks.add(n1);
        
        Notice n2 = new Notice();
        n2.setId(2L);
        n2.setTitle("元旦放假通知");
        n2.setContent("元旦期间，健身房正常营业，欢迎各位小伙伴前来锻炼！");
        n2.setAuthor("健身房前台");
        n2.setCreateTime(java.time.LocalDateTime.now().minusDays(1));
        n2.setType(1);
        n2.setStatus(1);
        mocks.add(n2);

        Notice n3 = new Notice();
        n3.setId(3L);
        n3.setTitle("系统维护公告");
        n3.setContent("为了提供更好的服务，系统将于本周日凌晨2:00-4:00进行例行维护，届时可能无法登录，请大家提前规划。");
        n3.setAuthor("技术团队");
        n3.setCreateTime(java.time.LocalDateTime.now());
        n3.setType(2);
        n3.setStatus(1);
        mocks.add(n3);
        
        return mocks;
    }
}
