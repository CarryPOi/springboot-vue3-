package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.UserInfo;
import com.example.fitness.mapper.UserInfoMapper;
import com.example.fitness.service.UserInfoService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public UserInfo getUserInfoByUsername(String username) {
        // 使用MyBatis-Plus查询用户名对应的用户
        return this.lambdaQuery().eq(UserInfo::getUsername, username).one();
    }

    @Override
    public boolean saveOrUpdateUserInfo(UserInfo userInfo) {
        // 保存/更新用户信息（MyBatis-Plus内置方法）
        return this.saveOrUpdate(userInfo);
    }

    @Override
    public boolean updateAvatarByUsername(String username, String avatarUrl) {
        return this.lambdaUpdate()
                .eq(UserInfo::getUsername, username)
                .set(UserInfo::getAvatarUrl, avatarUrl)
                .set(UserInfo::getUpdateTime, LocalDateTime.now())
                .update();
    }
}
