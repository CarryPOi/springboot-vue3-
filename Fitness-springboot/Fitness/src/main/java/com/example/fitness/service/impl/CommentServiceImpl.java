package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.Comment;
import com.example.fitness.entity.User;
import com.example.fitness.entity.UserInfo;
import com.example.fitness.mapper.CommentMapper;
import com.example.fitness.service.ICommentService;
import com.example.fitness.service.UserInfoService;
import com.example.fitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public List<Comment> list() {
        // 1. 获取所有评论
        List<Comment> allComments = super.list();
        if (allComments.isEmpty()) return allComments;

        // 2. 填充用户信息 (发布者和被回复者)
        fillUserInformation(allComments);

        // 3. 构建树形结构 (两级：根评论 -> 子回复列表)
        return buildCommentTree(allComments);
    }

    /**
     * 批量填充评论的用户信息
     */
    private void fillUserInformation(List<Comment> comments) {
        // 获取所有涉及的 userId
        Set<Long> userIds = comments.stream()
                .flatMap(c -> java.util.stream.Stream.of(c.getUserId(), c.getReplyUserId()))
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        if (userIds.isEmpty()) return;

        // 批量获取用户基本信息
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 批量获取用户详细资料（头像、实名）
        Set<String> usernames = userMap.values().stream().map(User::getUsername).collect(Collectors.toSet());
        Map<String, UserInfo> userInfoMap = userInfoService.list(new LambdaQueryWrapper<UserInfo>().in(UserInfo::getUsername, usernames))
                .stream().collect(Collectors.toMap(UserInfo::getUsername, ui -> ui));

        // 填充到评论对象中
        comments.forEach(comment -> {
            // 设置发布者信息
            User user = userMap.get(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
                UserInfo userInfo = userInfoMap.get(user.getUsername());
                if (userInfo != null) {
                    comment.setRealName(userInfo.getRealName());
                    comment.setAvatarUrl(userInfo.getAvatarUrl());
                }
            }
            // 设置被回复者信息
            if (comment.getReplyUserId() != null && comment.getReplyUserId() > 0) {
                User replyUser = userMap.get(comment.getReplyUserId());
                if (replyUser != null) {
                    comment.setReplyUsername(replyUser.getUsername());
                    UserInfo replyUserInfo = userInfoMap.get(replyUser.getUsername());
                    if (replyUserInfo != null) {
                        comment.setReplyRealName(replyUserInfo.getRealName());
                    }
                }
            }
        });
    }

    /**
     * 构建嵌套评论树
     */
    private List<Comment> buildCommentTree(List<Comment> allComments) {
        // 1. 分离根评论和子评论
        List<Comment> rootComments = allComments.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> childrenMap = allComments.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() > 0)
                .collect(Collectors.groupingBy(Comment::getParentId));

        // 2. 将子评论绑定到根评论
        rootComments.forEach(root -> {
            root.setChildren(childrenMap.getOrDefault(root.getId(), new java.util.ArrayList<>()));
        });

        // 3. 根评论按时间倒序排列
        return rootComments.stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteSafety(Long commentId, Long userId) {
        Comment comment = this.getById(commentId);
        if (comment == null) {
            return false;
        }

        // 获取当前操作用户信息
        User user = userService.getById(userId);
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

        // ⭐ 核心权限判断：只能删自己的评论，或者是管理员
        if (!comment.getUserId().equals(userId) && !isAdmin) {
            return false;
        }

        return this.removeById(commentId);
    }
}