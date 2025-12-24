package com.example.fitness.controller;

import com.example.fitness.entity.Course;
import com.example.fitness.entity.Notice;
import com.example.fitness.entity.User;
import com.example.fitness.service.ICourseService;
import com.example.fitness.service.INoticeService;
import com.example.fitness.service.UserInfoService;
import com.example.fitness.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端接口控制器
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Resource
    private UserService userService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private INoticeService noticeService;

    @Resource
    private ICourseService courseService;

    // ==================== 用户管理 ====================

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userService.list();
            List<com.example.fitness.entity.UserInfo> allUserInfo = userInfoService.list();
            
            // 将UserInfo转为Map方便查找，Key为username
            Map<String, com.example.fitness.entity.UserInfo> userInfoMap = new HashMap<>();
            for (com.example.fitness.entity.UserInfo info : allUserInfo) {
                userInfoMap.put(info.getUsername(), info);
            }

            // 合并数据
            List<Map<String, Object>> userVOList = new java.util.ArrayList<>();
            for (User user : users) {
                Map<String, Object> userVO = new HashMap<>();
                userVO.put("id", user.getId());
                userVO.put("username", user.getUsername());
                userVO.put("email", user.getEmail());
                userVO.put("role", user.getRole());
                userVO.put("createTime", user.getCreateTime());
                
                // 获取对应的真实姓名和头像
                com.example.fitness.entity.UserInfo info = userInfoMap.get(user.getUsername());
                if (info != null) {
                    userVO.put("realName", info.getRealName());
                    userVO.put("avatarUrl", info.getAvatarUrl());
                } else {
                    userVO.put("realName", "");
                    userVO.put("avatarUrl", "");
                }
                
                userVOList.add(userVO);
            }

            result.put("code", 200);
            result.put("data", userVOList);
            result.put("msg", "查询成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取用户列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新用户角色
     */
    @PostMapping("/user/updateRole")
    public Map<String, Object> updateUserRole(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long id = Long.valueOf(params.get("id").toString());
            String role = params.get("role").toString();
            
            User user = new User();
            user.setId(id);
            user.setRole(role);
            boolean success = userService.updateById(user);
            
            if (success) {
                result.put("code", 200);
                result.put("msg", "角色更新成功");
            } else {
                result.put("code", 500);
                result.put("msg", "角色更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "更新异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/user/delete/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userService.removeById(id);
            if (success) {
                result.put("code", 200);
                result.put("msg", "用户删除成功");
            } else {
                result.put("code", 500);
                result.put("msg", "用户删除失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "删除异常：" + e.getMessage());
        }
        return result;
    }

    // ==================== 公告管理 ====================

    @GetMapping("/notices")
    public Map<String, Object> getAllNotices() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Notice> notices = noticeService.list();
            result.put("code", 200);
            result.put("data", notices);
            result.put("msg", "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "获取公告失败：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/notice/save")
    public Map<String, Object> saveNotice(@RequestBody Notice notice) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (notice.getCreateTime() == null) {
                notice.setCreateTime(LocalDateTime.now());
            }
            boolean success = noticeService.saveOrUpdate(notice);
            result.put("code", success ? 200 : 500);
            result.put("msg", success ? "保存成功" : "保存失败");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "保存异常：" + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/notice/delete/{id}")
    public Map<String, Object> deleteNotice(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = noticeService.removeById(id);
            result.put("code", success ? 200 : 500);
            result.put("msg", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "删除异常：" + e.getMessage());
        }
        return result;
    }

    // ==================== 课程管理 ====================

    @GetMapping("/courses")
    public Map<String, Object> getAllCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> courses = courseService.getAdminCourseList();
            result.put("code", 200);
            result.put("data", courses);
            result.put("msg", "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "获取课程失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/course/subscribers/{id}")
    public Map<String, Object> getCourseSubscribers(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> subscribers = courseService.getCourseSubscribers(id);
            result.put("code", 200);
            result.put("data", subscribers);
            result.put("msg", "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "获取订阅者失败：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/course/save")
    public Map<String, Object> saveCourse(@RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (course.getCreateTime() == null) {
                course.setCreateTime(LocalDateTime.now());
            }
            boolean success = courseService.saveOrUpdate(course);
            result.put("code", success ? 200 : 500);
            result.put("msg", success ? "保存成功" : "保存失败");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "保存异常：" + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/course/delete/{id}")
    public Map<String, Object> deleteCourse(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = courseService.removeById(id);
            result.put("code", success ? 200 : 500);
            result.put("msg", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "删除异常：" + e.getMessage());
        }
        return result;
    }
}
