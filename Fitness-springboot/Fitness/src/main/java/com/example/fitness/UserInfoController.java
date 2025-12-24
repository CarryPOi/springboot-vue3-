package com.example.fitness; // 先保留在启动类同包，确保生效

import com.example.fitness.entity.UserInfo;
import com.example.fitness.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {

    // 注入Service层（操作数据库）
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/get")
    public Map<String, Object> getUserInfo(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用Service查询数据库中的用户信息
            UserInfo userInfo = userInfoService.getUserInfoByUsername(username);
            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", userInfo); // 返回真实用户数据
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/save")
    public Map<String, Object> saveUserInfo(@RequestBody UserInfo userInfo) {
        Map<String, Object> result = new HashMap<>();
        // 参数校验
        if (userInfo.getUsername() == null || userInfo.getUsername().isEmpty()) {
            result.put("code", 1);
            result.put("msg", "用户名不能为空");
            return result;
        }
        try {
            // 保存/更新用户信息到数据库
            boolean success = userInfoService.saveOrUpdateUserInfo(userInfo);
            if (success) {
                result.put("code", 0);
                result.put("msg", "保存成功");
                result.put("data", userInfo);
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

    @PostMapping("/upload-avatar")
    public Map<String, Object> uploadAvatar(@RequestParam("username") String username,
                                            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file == null || file.isEmpty() || username == null || username.isEmpty()) {
            result.put("code", 1);
            result.put("msg", "文件或用户名不能为空");
            return result;
        }
        try {
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
            Path baseDir = Paths.get(System.getProperty("user.dir"), "uploads", "avatars", username);
            Files.createDirectories(baseDir);
            Path target = baseDir.resolve(fileName);
            Files.write(target, file.getBytes());

            String url = "/uploads/avatars/" + username + "/" + fileName;

            try {
                UserInfo userInfo = userInfoService.getUserInfoByUsername(username);
                if (userInfo == null) {
                    userInfo = new UserInfo();
                    userInfo.setUsername(username);
                    userInfo.setAvatarUrl(url);
                    userInfo.setUpdateTime(LocalDateTime.now());
                    userInfoService.saveOrUpdateUserInfo(userInfo);
                } else {
                    userInfoService.updateAvatarByUsername(username, url);
                }
            } catch (Exception ignore) {
                // 数据库缺少avatar_url列时，这里忽略持久化错误，前端依旧可使用返回的url
            }

            result.put("code", 0);
            result.put("msg", "上传成功");
            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", url);
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "上传失败：" + e.getMessage());
        }
        return result;
    }
}
