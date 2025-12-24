package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.BodyData;

import java.util.List;

/**
 * 身材数据 Service 接口（对齐UserInfoService风格）
 * 继承 IService<BodyData> 复用 MyBatis-Plus 基础CRUD方法
 */
public interface BodyDataService extends IService<BodyData> {

    // 自定义业务方法：按用户名查询历史身材记录
    List<BodyData> getByUsername(String username);

    // 自定义业务方法：保存/更新身材数据（同步用户最新数据）
    boolean saveOrUpdateBodyData(BodyData bodyData);

    // 自定义业务方法：删除身材记录
    boolean deleteById(Long id);
}