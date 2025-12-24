-- 创建课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `title` VARCHAR(100) NOT NULL COMMENT '课程标题',
    `description` TEXT COMMENT '课程描述',
    `trainer` VARCHAR(50) COMMENT '教练名称',
    `duration` INT COMMENT '时长(分钟)',
    `difficulty` VARCHAR(20) COMMENT '难度 (初级/中级/高级)',
    `category` VARCHAR(50) COMMENT '分类 (减脂/增肌/瑜伽/力量)',
    `cover_image` TEXT COMMENT '封面图URL',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 创建课程订阅表
CREATE TABLE IF NOT EXISTS `course_subscription` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `subscribe_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订阅时间',
    UNIQUE KEY `uk_user_course` (`username`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程订阅表';

-- 插入初始化课程数据
INSERT INTO `course` (title, description, trainer, duration, difficulty, category, cover_image) 
VALUES ('全身燃脂HIIT', '高强度间歇训练，快速提升心肺耐力，高效燃脂。', '张教练', 30, '中级', '减脂', 'https://img.js.design/assets/img/646f0417937402660d36c641.png');

INSERT INTO `course` (title, description, trainer, duration, difficulty, category, cover_image) 
VALUES ('零基础增肌入门', '专为新手设计的力量训练方案，带你走进健身房。', '李教练', 45, '初级', '增肌', 'https://img.js.design/assets/img/646f043e937402660d36ca91.png');

INSERT INTO `course` (title, description, trainer, duration, difficulty, category, cover_image) 
VALUES ('晨间舒缓瑜伽', '开启元气满满的一天，拉伸肌肉，放松身心。', '王老师', 20, '初级', '瑜伽', 'https://img.js.design/assets/img/646f045e937402660d36ce53.png');

INSERT INTO `course` (title, description, trainer, duration, difficulty, category, cover_image) 
VALUES ('核心力量强化', '专注腹部与背部核心肌群，打造稳健身体基石。', '赵教练', 40, '高级', '力量', 'https://img.js.design/assets/img/646f0477937402660d36d10c.png');
