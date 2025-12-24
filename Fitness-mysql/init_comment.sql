-- 创建评论表
CREATE TABLE IF NOT EXISTS `t_comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID',
    `reply_user_id` BIGINT DEFAULT NULL COMMENT '被回复者ID',
    `images` TEXT DEFAULT NULL COMMENT '图片列表(逗号分隔)',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted` INT DEFAULT 0 COMMENT '是否删除 0:未删 1:已删',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';
