-- 创建数据库
CREATE DATABASE IF NOT EXISTS carpool_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE carpool_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender VARCHAR(10) COMMENT '性别',
    age INT COMMENT '年龄',
    id_card VARCHAR(20) COMMENT '身份证号',
    hometown_province VARCHAR(50) COMMENT '家乡省份',
    hometown_city VARCHAR(50) COMMENT '家乡城市',
    current_province VARCHAR(50) COMMENT '当前省份',
    current_city VARCHAR(50) COMMENT '当前城市',
    university VARCHAR(100) COMMENT '毕业院校',
    interests TEXT COMMENT '兴趣爱好',
    rating DECIMAL(3,2) DEFAULT 5.00 COMMENT '评分',
    trip_count INT DEFAULT 0 COMMENT '行程次数',
    completed_trips INT DEFAULT 0 COMMENT '完成行程数',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_hometown (hometown_province, hometown_city),
    INDEX idx_current (current_province, current_city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 行程表
CREATE TABLE IF NOT EXISTS trips (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    trip_type VARCHAR(10) NOT NULL COMMENT '行程类型: car_owner-车找人, passenger-人找车',
    departure_province VARCHAR(50) NOT NULL COMMENT '出发省份',
    departure_city VARCHAR(50) NOT NULL COMMENT '出发城市',
    departure_district VARCHAR(50) COMMENT '出发区县',
    departure_detail VARCHAR(255) COMMENT '出发详细地址',
    destination_province VARCHAR(50) NOT NULL COMMENT '目的省份',
    destination_city VARCHAR(50) NOT NULL COMMENT '目的城市',
    destination_district VARCHAR(50) COMMENT '目的区县',
    destination_detail VARCHAR(255) COMMENT '目的详细地址',
    departure_time DATETIME NOT NULL COMMENT '出发时间',
    seats_available INT DEFAULT 1 COMMENT '可用座位数',
    seats_total INT DEFAULT 4 COMMENT '总座位数',
    price_per_person DECIMAL(10,2) COMMENT '人均价格',
    description TEXT COMMENT '行程描述',
    car_model VARCHAR(50) COMMENT '车型',
    car_plate VARCHAR(20) COMMENT '车牌号',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待匹配, matched-已匹配, completed-已完成, cancelled-已取消',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    match_count INT DEFAULT 0 COMMENT '匹配次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_departure (departure_province, departure_city, departure_district),
    INDEX idx_destination (destination_province, destination_city, destination_district),
    INDEX idx_departure_time (departure_time),
    INDEX idx_status (status),
    INDEX idx_trip_type (trip_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程表';

-- 匹配表
CREATE TABLE IF NOT EXISTS matches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trip_id BIGINT NOT NULL COMMENT '行程ID',
    matched_trip_id BIGINT COMMENT '匹配的行程ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待确认, accepted-已接受, rejected-已拒绝, completed-已完成',
    initiator_id BIGINT NOT NULL COMMENT '发起匹配的用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (matched_trip_id) REFERENCES trips(id) ON DELETE SET NULL,
    FOREIGN KEY (initiator_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_trip_id (trip_id),
    INDEX idx_matched_trip_id (matched_trip_id),
    INDEX idx_status (status),
    INDEX idx_initiator (initiator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配表';

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    match_id BIGINT NOT NULL COMMENT '匹配ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_match_id (match_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 评价表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trip_id BIGINT NOT NULL COMMENT '行程ID',
    reviewer_id BIGINT NOT NULL COMMENT '评价者ID',
    reviewed_id BIGINT NOT NULL COMMENT '被评价者ID',
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5) COMMENT '评分1-5',
    content TEXT COMMENT '评价内容',
    tags VARCHAR(255) COMMENT '标签',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_trip_id (trip_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewed_id (reviewed_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- 插入测试数据
INSERT INTO users (phone, password, real_name, hometown_province, hometown_city, current_province, current_city, university, rating) VALUES
('13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', '张三', '北京市', '北京市', '北京市', '北京市', '清华大学', 4.8),
('13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', '李四', '河北省', '石家庄市', '北京市', '北京市', '北京大学', 4.9),
('13800138002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', '王五', '河北省', '保定市', '北京市', '北京市', '北京理工大学', 4.7);

INSERT INTO trips (user_id, trip_type, departure_province, departure_city, departure_district, departure_detail, destination_province, destination_city, destination_district, destination_detail, departure_time, seats_available, seats_total, price_per_person, description, car_model, car_plate) VALUES
(1, 'car_owner', '北京市', '北京市', '朝阳区', '望京SOHO T3座', '河北省', '石家庄市', '长安区', '火车站北广场', '2026-04-04 08:00:00', 3, 4, 100.00, '可放行李，走高速，不吸烟', '丰田卡罗拉', '京A·12345'),
(2, 'passenger', '北京市', '北京市', '海淀区', '清华大学东门', '河北省', '石家庄市', '桥西区', '万象城', '2026-04-04 09:00:00', 1, 1, 120.00, '学生返乡，行李不多', NULL, NULL),
(3, 'car_owner', '北京市', '北京市', '东城区', '王府井大街', '河北省', '保定市', '莲池区', '客运中心', '2026-04-05 10:00:00', 2, 4, 90.00, '可到地铁站接，有宠物', '本田雅阁', '京B·56789');

-- 密码都是: 123456