package com.example.fitness.controller;

import com.example.fitness.entity.User;
import com.example.fitness.entity.UserInfo;
import com.example.fitness.service.UserService;
import com.example.fitness.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户接口层：提供登录/注册HTTP接口
 * @CrossOrigin：解决前端跨域请求问题
 */
@RestController
@RequestMapping("/api/user")  // 接口前缀：http://localhost:8080/api/user
@CrossOrigin  // 允许跨域（前后端端口不同时必需）
public class UserController {

    // 注入UserService（业务逻辑层）
    @Resource
    private UserService userService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 注册接口
     * 请求方式：POST
     * 请求地址：http://localhost:8080/api/user/register
     * 请求体：JSON格式（username/password/email）
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userService.register(user);
            if (success) {
                result.put("code", 200);  // 成功状态码
                result.put("msg", "注册成功！");
            } else {
                result.put("code", 500);  // 失败状态码
                result.put("msg", "注册失败（用户名已存在或参数错误）");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "注册异常：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 登录接口
     * 请求方式：POST
     * 请求地址：http://localhost:8080/api/user/login
     * 请求参数：Form/Query格式（username/password）
     */
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.login(username, password);
            if (user != null) {
                result.put("code", 200);
                result.put("msg", "登录成功！");
                // 返回用户信息（脱敏，隐藏密码）
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("role", user.getRole()); // 返回角色信息
                
                // 新增：获取并返回用户详细信息（头像、真实姓名）
                try {
                    UserInfo info = userInfoService.getUserInfoByUsername(user.getUsername());
                    if (info != null) {
                        userInfo.put("avatarUrl", info.getAvatarUrl());
                        userInfo.put("realName", info.getRealName()); // 返回真实姓名
                    }
                } catch (Exception e) {
                    // 忽略获取详细信息失败
                }
                
                result.put("data", userInfo);
            } else {
                result.put("code", 500);
                result.put("msg", "用户名或密码错误！");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "登录异常：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}