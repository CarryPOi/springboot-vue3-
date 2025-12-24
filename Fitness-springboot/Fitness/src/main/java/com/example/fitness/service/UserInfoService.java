package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.UserInfo;

/**
 * 个人信息 Service 接口（保留原有方法 + 新增适配方法）
 */
public interface UserInfoService extends IService<UserInfo> {

    // 保留你原有方法
    UserInfo getUserInfoByUsername(String username);
    boolean saveOrUpdateUserInfo(UserInfo userInfo);

    // ========== 新增：适配BodyDataService的调用（默认方法，无需修改实现类） ==========
    default UserInfo getByUsername(String username) {
        // 直接复用原有方法，仅方法名对齐
        return this.getUserInfoByUsername(username);
    }

    // 新增：根据用户名更新头像地址
    boolean updateAvatarByUsername(String username, String avatarUrl);
}
