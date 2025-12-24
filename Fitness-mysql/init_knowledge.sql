CREATE TABLE IF NOT EXISTS `fitness_knowledge` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `category` VARCHAR(50) DEFAULT '其他',
  `cover_url` VARCHAR(255),
  `status` VARCHAR(20) DEFAULT '已发布',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `fitness_knowledge` (`title`, `content`, `category`, `cover_url`) VALUES 
('新手健身入门指南', '健身的第一步是了解自己的身体，并制定科学的计划...', '健身入门', 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438'),
('增肌期间的蛋白质摄入', '蛋白质是肌肉生长的基础，建议每公斤体重摄入1.6-2.2g...', '增肌', 'https://images.unsplash.com/photo-1532384748853-8f54a9f472e9');
