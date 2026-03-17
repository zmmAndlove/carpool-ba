-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    avatar VARCHAR(500),
    gender VARCHAR(10),
    age INT,
    hometown_province VARCHAR(50),
    hometown_city VARCHAR(50),
    current_province VARCHAR(50),
    current_city VARCHAR(50),
    university VARCHAR(100),
    interests VARCHAR(500),
    rating DECIMAL(3,2) DEFAULT 5.0,
    trip_count INT DEFAULT 0,
    completed_trips INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 行程表
CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    trip_type VARCHAR(20) NOT NULL,
    departure_province VARCHAR(50) NOT NULL,
    departure_city VARCHAR(50) NOT NULL,
    departure_district VARCHAR(50),
    departure_detail VARCHAR(200),
    destination_province VARCHAR(50) NOT NULL,
    destination_city VARCHAR(50) NOT NULL,
    destination_district VARCHAR(50),
    destination_detail VARCHAR(200),
    departure_time TIMESTAMP NOT NULL,
    seats_available INT NOT NULL,
    seats_total INT NOT NULL,
    price_per_person DECIMAL(10,2),
    description TEXT,
    car_model VARCHAR(100),
    car_plate VARCHAR(50),
    status VARCHAR(20) DEFAULT 'pending',
    view_count INT DEFAULT 0,
    match_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 匹配表
CREATE TABLE IF NOT EXISTS matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    trip_id BIGINT,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);

-- 评价表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id BIGINT NOT NULL,
    reviewed_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    FOREIGN KEY (reviewed_id) REFERENCES users(id),
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);
