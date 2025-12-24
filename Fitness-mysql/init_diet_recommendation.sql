CREATE TABLE IF NOT EXISTS `diet_recommendations` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT,
  `meal_type` VARCHAR(50),
  `calories` DOUBLE,
  `author` VARCHAR(50),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
