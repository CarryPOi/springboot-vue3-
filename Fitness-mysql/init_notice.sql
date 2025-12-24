CREATE TABLE IF NOT EXISTS `notice` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `author` VARCHAR(50) DEFAULT 'System' COMMENT '作者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `type` INT DEFAULT 0 COMMENT '类型 0:系统 1:活动 2:维护',
  `status` INT DEFAULT 1 COMMENT '状态 1:发布 0:草稿'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

INSERT INTO `notice` (`title`, `content`, `author`, `type`, `status`, `create_time`) VALUES
('系统升级通知', '系统将于今晚进行维护升级，预计耗时2小时，请各位用户提前做好准备。', 'Admin', 2, 1, NOW()),
('新功能上线：每日公告', '我们上线了全新的每日公告功能，包含健身小知识和饮食推荐，快来体验吧！', 'Admin', 0, 1, NOW()),
('冬季健身活动', '冬季健身打卡活动开始啦！坚持打卡30天，赢取精美礼品。', 'Operation', 1, 1, NOW());
