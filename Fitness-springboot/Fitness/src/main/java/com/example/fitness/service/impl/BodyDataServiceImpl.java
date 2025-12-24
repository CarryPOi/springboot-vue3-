package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.BodyData;
import com.example.fitness.entity.UserInfo;
import com.example.fitness.mapper.BodyDataMapper;
import com.example.fitness.service.UserInfoService; // 改为注入Service而非Mapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 身材数据Service实现类（适配你的UserInfoService）
 */
@Service
public class BodyDataServiceImpl extends ServiceImpl<BodyDataMapper, BodyData> implements com.example.fitness.service.BodyDataService {

    // ========== 改为注入UserInfoService（而非Mapper，符合分层规范） ==========
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public List<BodyData> getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public boolean saveOrUpdateBodyData(BodyData bodyData) {
        // 1. 保存/更新身材历史记录
        boolean success = this.saveOrUpdate(bodyData);

        if (success) {
            // 2. 查询用户原有信息（复用你的getUserInfoByUsername方法）
            UserInfo userInfo = userInfoService.getUserInfoByUsername(bodyData.getUsername());
            if (userInfo != null) {
                // 3. 更新用户最新数据
                userInfo.setWeight(bodyData.getWeight());
                userInfo.setBodyFatRate(bodyData.getBodyFatRate());
                userInfo.setWaistCircumference(bodyData.getWaistCircumference());
                userInfo.setHipCircumference(bodyData.getHipCircumference());
                userInfo.setLastRecordTime(LocalDateTime.now());
                // 4. 调用你原有saveOrUpdateUserInfo方法保存
                userInfoService.saveOrUpdateUserInfo(userInfo);
            }
        }

        return success;
    }

    @Override
    public boolean deleteById(Long id) {
        return this.removeById(id);
    }
}

