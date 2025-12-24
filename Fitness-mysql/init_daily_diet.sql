-- 创建每日饮食记录表
CREATE TABLE IF NOT EXISTS `daily_diet` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `food_name` VARCHAR(100) NOT NULL COMMENT '食物名称',
    `calories` DOUBLE DEFAULT 0 COMMENT '卡路里(kcal)',
    `protein` DOUBLE DEFAULT 0 COMMENT '蛋白质(g)',
    `fat` DOUBLE DEFAULT 0 COMMENT '脂肪(g)',
    `carbs` DOUBLE DEFAULT 0 COMMENT '碳水(g)',
    `weight` DOUBLE DEFAULT 0 COMMENT '重量(g)',
    `meal_type` VARCHAR(20) NOT NULL COMMENT '餐次 (早餐/午餐/晚餐/加餐)',
    `record_date` DATE NOT NULL COMMENT '记录日期',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日饮食记录表';

-- 插入一些示例数据
INSERT INTO `daily_diet` (username, food_name, calories, protein, fat, carbs, weight, meal_type, record_date) 
VALUES ('hehuicong', '燕麦粥', 150, 5, 3, 27, 200, '早餐', CURDATE());

INSERT INTO `daily_diet` (username, food_name, calories, protein, fat, carbs, weight, meal_type, record_date) 
VALUES ('hehuicong', '鸡胸肉', 165, 31, 3.6, 0, 100, '午餐', CURDATE());
