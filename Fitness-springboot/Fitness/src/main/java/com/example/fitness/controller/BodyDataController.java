package com.example.fitness.controller;

import com.example.fitness.entity.BodyData;
import com.example.fitness.service.BodyDataService;
import com.example.fitness.service.IBodyAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身材数据控制器（严格对齐UserInfoController风格）
 */
@RestController
@RequestMapping("/api/body-data") // 接口前缀：/api/body-data
@CrossOrigin(origins = "http://localhost:8081") // 和UserInfoController一致的跨域配置
public class BodyDataController {

    @Autowired
    private BodyDataService bodyDataService;

    @Autowired
    @Qualifier("localAnalysisService")
    private IBodyAnalysisService localAnalysisService;

    @Autowired
    @Qualifier("aiAnalysisService")
    private IBodyAnalysisService aiAnalysisService;

    @Value("${ai.mode:local}")
    private String aiMode;

    /**
     * 获取身材分析建议
     * 完整路径：/api/body-data/analyze?username=test001
     */
    @GetMapping("/analyze")
    public Map<String, Object> analyzeBodyData(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            IBodyAnalysisService selectedService = "ai".equalsIgnoreCase(aiMode) ? aiAnalysisService : localAnalysisService;
            Map<String, Object> analysisResult = selectedService.analyzeBodyData(username);
            result.put("code", 0);
            result.put("msg", "分析成功");
            result.put("data", analysisResult);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "分析失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询用户历史身材记录（参考UserInfoController的getUserInfo写法）
     * 完整路径：/api/body-data/list?username=test001
     */
    @GetMapping("/list")
    public Map<String, Object> getBodyDataList(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用Service层查询
            List<BodyData> dataList = bodyDataService.getByUsername(username);
            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", dataList); // 无数据时返回空列表，前端兼容
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 新增/编辑身材数据（自动同步用户最新数据）
     * 参考UserInfoController的saveUserInfo写法
     * 完整路径：/api/body-data/save
     */
    @PostMapping("/save")
    public Map<String, Object> saveBodyData(@RequestBody BodyData bodyData) {
        Map<String, Object> result = new HashMap<>();
        // 参数校验（对齐你的逻辑）
        if (bodyData.getUsername() == null || bodyData.getUsername().isEmpty()) {
            result.put("code", 1);
            result.put("msg", "用户名不能为空");
            return result;
        }
        if (bodyData.getRecordDate() == null || bodyData.getWeight() == null) {
            result.put("code", 1);
            result.put("msg", "记录日期和体重不能为空");
            return result;
        }
        try {
            // 调用Service层保存/更新（自动同步user_info）
            boolean success = bodyDataService.saveOrUpdateBodyData(bodyData);
            if (success) {
                result.put("code", 0);
                result.put("msg", "保存成功");
                result.put("data", bodyData); // 返回保存的记录
            } else {
                result.put("code", 1);
                result.put("msg", "保存失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "保存失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除身材记录
     * 完整路径：/api/body-data/delete
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteBodyData(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用Service层删除
            boolean success = bodyDataService.deleteById(id);
            if (success) {
                result.put("code", 0);
                result.put("msg", "删除成功");
            } else {
                result.put("code", 1);
                result.put("msg", "删除失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "删除失败：" + e.getMessage());
        }
        return result;
    }
}