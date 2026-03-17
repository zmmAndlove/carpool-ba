#!/bin/bash

# 简化的部署脚本 - 仅配置和启动

set -e

echo "🚀 开始配置长途拼车平台"

# 创建必要的目录
mkdir -p backend/logs
mkdir -p frontend/dist

# 1. 配置后端使用H2数据库
echo "⚙️ 配置后端（使用H2内存数据库）..."

cat > backend/src/main/resources/application.yml << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:carpool;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    username: sa
    password: 
    driver-class-name: org.h2.Driver
    hikari:
      maximum-pool-size: 10

  h2:
    console:
      enabled: true
      path: /h2-console
      settings:
        web-allow-others: true

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true

jwt:
  secret: development-jwt-secret-key-123456
  expiration: 86400000

logging:
  level:
    com.carpool: INFO
  file:
    name: logs/carpool.log

cors:
  allowed-origins: http://localhost:3000,http://localhost:5173
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
EOF

# 2. 创建数据库初始化脚本
cat > backend/src/main/resources/schema.sql << 'EOF'
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
EOF

# 3. 创建测试数据
cat > backend/src/main/resources/data.sql << 'EOF'
-- 测试用户
INSERT INTO users (phone, password, real_name, gender, age, hometown_province, hometown_city, current_province, current_city, university, interests) VALUES
('13800138001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeG3vJ.7g6Hn7.7p6J7p6J7p6J7p6J7p6J', '张三', '男', 28, '北京市', '北京市', '北京市', '北京市', '清华大学', '篮球,音乐'),
('13800138002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeG3vJ.7g6Hn7.7p6J7p6J7p6J7p6J7p6J', '李四', '女', 25, '上海市', '上海市', '上海市', '上海市', '复旦大学', '旅游,摄影'),
('13800138003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeG3vJ.7g6Hn7.7p6J7p6J7p6J7p6J7p6J', '王五', '男', 32, '广东省', '广州市', '广东省', '深圳市', '中山大学', '足球,电影');

-- 测试行程
INSERT INTO trips (user_id, trip_type, departure_province, departure_city, departure_district, departure_detail, destination_province, destination_city, destination_district, destination_detail, departure_time, seats_available, seats_total, price_per_person, description, car_model, car_plate) VALUES
(1, 'car_owner', '北京市', '北京市', '朝阳区', '望京SOHO T3座', '河北省', '石家庄市', '长安区', '火车站北广场', DATE_ADD(NOW(), INTERVAL 2 DAY), 3, 4, 150.00, '春节回家，可放行李，走高速，不吸烟', '丰田卡罗拉', '京A·12345'),
(2, 'passenger', '上海市', '上海市', '浦东新区', '陆家嘴金融中心', '江苏省', '南京市', '鼓楼区', '新街口地铁站', DATE_ADD(NOW(), INTERVAL 3 DAY), 1, 1, 120.00, '寻找顺风车回家过年，可分摊油费', NULL, NULL),
(3, 'car_owner', '广东省', '深圳市', '南山区', '科技园腾讯大厦', '广东省', '广州市', '天河区', '体育中心', DATE_ADD(NOW(), INTERVAL 1 DAY), 2, 3, 80.00, '周末回家，可接送至地铁站', '本田雅阁', '粤B·88888');
EOF

# 4. 配置前端
echo "⚙️ 配置前端..."

cat > frontend/.env.local << 'EOF'
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=长途拼车平台
VITE_APP_ENV=development
EOF

# 5. 创建启动脚本
echo "🚀 创建启动脚本..."

cat > start-backend.js << 'EOF'
const { exec } = require('child_process');
const fs = require('fs');

console.log('🚀 启动长途拼车平台后端...');

// 检查是否已安装Java
exec('which java', (error) => {
  if (error) {
    console.log('❌ Java未安装，无法启动后端服务');
    console.log('请先安装Java 17+：');
    console.log('Ubuntu/Debian: sudo apt install openjdk-17-jdk');
    console.log('CentOS/RHEL: sudo yum install java-17-openjdk');
    process.exit(1);
  }
  
  console.log('✅ Java已安装，开始启动后端...');
  
  // 启动后端
  const backendProcess = exec('cd backend && mvn spring-boot:run', (error, stdout, stderr) => {
    if (error) {
      console.error(`后端启动失败: ${error}`);
      return;
    }
    console.log(stdout);
  });
  
  backendProcess.stdout.on('data', (data) => {
    console.log(`后端: ${data}`);
  });
  
  backendProcess.stderr.on('data', (data) => {
    console.error(`后端错误: ${data}`);
  });
});
EOF

cat > start-frontend.js << 'EOF'
const { exec } = require('child_process');

console.log('🚀 启动长途拼车平台前端...');

// 安装依赖
console.log('📦 安装前端依赖...');
exec('cd frontend && npm install', (error, stdout, stderr) => {
  if (error) {
    console.error(`依赖安装失败: ${error}`);
    return;
  }
  
  console.log('✅ 依赖安装完成');
  
  // 启动前端开发服务器
  console.log('🌐 启动前端开发服务器...');
  const frontendProcess = exec('cd frontend && npm run dev', (error, stdout, stderr) => {
    if (error) {
      console.error(`前端启动失败: ${error}`);
      return;
    }
    console.log(stdout);
  });
  
  frontendProcess.stdout.on('data', (data) => {
    console.log(`前端: ${data}`);
  });
  
  frontendProcess.stderr.on('data', (data) => {
    console.error(`前端错误: ${data}`);
  });
});
EOF

cat > README-START.md << 'EOF'
# 长途拼车平台 - 启动指南

## 环境要求
1. **Java 17+** - 后端运行需要
2. **Node.js 18+** - 前端运行需要
3. **Maven** - Java项目构建工具

## 启动步骤

### 1. 安装Java（如果未安装）
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk maven

# CentOS/RHEL
sudo yum install java-17-openjdk-devel maven
```

### 2. 启动后端
```bash
cd backend
mvn spring-boot:run
```
或者使用Node.js脚本：
```bash
node start-backend.js
```

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
或者使用Node.js脚本：
```bash
node start-frontend.js
```

## 访问地址
- **前端**: http://localhost:3000
- **后端API**: http://localhost:8080/api
- **健康检查**: http://localhost:8080/api/health
- **H2数据库控制台**: http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:carpool
  - Username: sa
  - Password: (留空)

## 测试账号
- 手机号: 13800138001
- 密码: 123456

## 功能说明
1. **用户注册/登录** - 使用JWT认证
2. **发布行程** - 车找人/人找车
3. **搜索行程** - 按地点、时间筛选
4. **加入行程** - 匹配同行
5. **个人中心** - 查看我的行程

## 技术栈
- **后端**: Spring Boot 3 + MyBatis + H2
- **前端**: Vue 3 + TypeScript + Element Plus
- **数据库**: H2内存数据库（开发环境）
- **认证**: JWT令牌

## 生产环境部署
如需部署到生产环境（pincheba.icu），请使用Docker部署方案。
EOF

echo ""
echo "🎉 配置完成！"
echo ""
echo "📋 请按照以下步骤启动项目："
echo "1. 安装Java 17+ 和 Maven"
echo "2. 启动后端：cd backend && mvn spring-boot:run"
echo "3. 启动前端：cd frontend && npm install && npm run dev"
echo ""
echo "📖 详细说明请查看 README-START.md"
echo ""
echo "🌐 访问地址："
echo "   前端：http://localhost:3000"
echo "   后端：http://localhost:8080/api"