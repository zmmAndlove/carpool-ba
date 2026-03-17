#!/bin/bash

# 长途拼车平台部署脚本
# 域名: pincheba.icu

set -e

echo "🚀 开始部署长途拼车平台到 pincheba.icu"

# 检查必要工具
check_tools() {
    echo "🔧 检查必要工具..."
    command -v docker >/dev/null 2>&1 || { echo "❌ 请先安装 Docker"; exit 1; }
    command -v docker-compose >/dev/null 2>&1 || { echo "❌ 请先安装 Docker Compose"; exit 1; }
    command -v java >/dev/null 2>&1 || { echo "❌ 请先安装 Java 17+"; exit 1; }
    command -v node >/dev/null 2>&1 || { echo "❌ 请先安装 Node.js 18+"; exit 1; }
    command -v mvn >/dev/null 2>&1 || { echo "❌ 请先安装 Maven"; exit 1; }
    echo "✅ 所有必要工具已安装"
}

# 配置生产环境
setup_production() {
    echo "⚙️ 配置生产环境..."
    
    # 创建生产环境配置文件
    cat > backend/src/main/resources/application-prod.yml << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://mysql:3306/carpool_prod?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: carpool_prod
    password: ${DB_PASSWORD:-ChangeMe123!}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  data:
    redis:
      host: redis
      port: 6379
      password: ${REDIS_PASSWORD:-ChangeMe123!}
      database: 0
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: 5000ms

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

jwt:
  secret: ${JWT_SECRET:-your-production-jwt-secret-key-change-this}
  expiration: 86400000 # 24小时

logging:
  level:
    com.carpool: DEBUG
  file:
    name: logs/carpool.log
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30

cors:
  allowed-origins: https://pincheba.icu,https://www.pincheba.icu
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
EOF

    # 创建生产环境 Docker Compose 配置
    cat > docker-compose.prod.yml << 'EOF'
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: carpool-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-ChangeMe123!}
      MYSQL_DATABASE: carpool_prod
      MYSQL_USER: carpool_prod
      MYSQL_PASSWORD: ${DB_PASSWORD:-ChangeMe123!}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docker/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"
    networks:
      - carpool-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  redis:
    image: redis:7-alpine
    container_name: carpool-redis
    restart: unless-stopped
    command: redis-server --requirepass ${REDIS_PASSWORD:-ChangeMe123!}
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    networks:
      - carpool-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: carpool-backend
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_PASSWORD: ${DB_PASSWORD:-ChangeMe123!}
      REDIS_PASSWORD: ${REDIS_PASSWORD:-ChangeMe123!}
      JWT_SECRET: ${JWT_SECRET:-your-production-jwt-secret-key-change-this}
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - carpool-network
    volumes:
      - ./logs:/app/logs
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: carpool-frontend
    restart: unless-stopped
    ports:
      - "3000:80"
    networks:
      - carpool-network
    depends_on:
      - backend
    environment:
      VITE_API_BASE_URL: https://pincheba.icu/api

  nginx:
    image: nginx:alpine
    container_name: carpool-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/conf.d:/etc/nginx/conf.d
      - ./ssl:/etc/nginx/ssl
    networks:
      - carpool-network
    depends_on:
      - frontend
      - backend

networks:
  carpool-network:
    driver: bridge

volumes:
  mysql_data:
  redis_data:
EOF

    # 创建 Nginx 配置目录
    mkdir -p nginx/conf.d
    mkdir -p ssl

    # 创建 Nginx 主配置
    cat > nginx/nginx.conf << 'EOF'
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    include /etc/nginx/conf.d/*.conf;
}
EOF

    # 创建站点配置
    cat > nginx/conf.d/pincheba.icu.conf << 'EOF'
# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name pincheba.icu www.pincheba.icu;
    return 301 https://pincheba.icu$request_uri;
}

# HTTPS 配置
server {
    listen 443 ssl http2;
    server_name pincheba.icu www.pincheba.icu;

    ssl_certificate /etc/nginx/ssl/pincheba.icu.crt;
    ssl_certificate_key /etc/nginx/ssl/pincheba.icu.key;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # 前端静态文件
    location / {
        proxy_pass http://frontend:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 后端 API
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 增加超时时间
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://frontend:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # 健康检查
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
EOF

    echo "✅ 生产环境配置完成"
}

# 创建 Dockerfile
create_dockerfiles() {
    echo "🐳 创建 Dockerfile..."
    
    # 后端 Dockerfile
    cat > backend/Dockerfile << 'EOF'
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

    # 前端 Dockerfile
    cat > frontend/Dockerfile << 'EOF'
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
EOF

    # 前端 Nginx 配置
    cat > frontend/nginx.conf << 'EOF'
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # 开启 gzip
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # 处理 Vue Router 的 history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

    echo "✅ Dockerfile 创建完成"
}

# 创建环境变量文件
create_env_file() {
    echo "🔐 创建环境变量文件..."
    
    cat > .env.prod << 'EOF'
# 数据库配置
MYSQL_ROOT_PASSWORD=ChangeMe123!
DB_PASSWORD=StrongPassword123!

# Redis配置
REDIS_PASSWORD=RedisPassword123!

# JWT配置
JWT_SECRET=your-production-jwt-secret-key-change-this-long-random-string

# 域名配置
DOMAIN=pincheba.icu
EOF

    cat > .env.prod.example << 'EOF'
# 生产环境配置示例
# 请复制为 .env.prod 并修改实际值

# 数据库配置
MYSQL_ROOT_PASSWORD=你的MySQL根密码
DB_PASSWORD=你的应用数据库密码

# Redis配置
REDIS_PASSWORD=你的Redis密码

# JWT配置
JWT_SECRET=你的JWT密钥（长随机字符串）

# 域名配置
DOMAIN=pincheba.icu
EOF

    echo "✅ 环境变量文件创建完成"
    echo "⚠️  请编辑 .env.prod 文件，修改为实际的生产环境密码"
}

# 创建健康检查接口
create_health_check() {
    echo "🏥 创建健康检查接口..."
    
    cat > backend/src/main/java/com/carpool/controller/HealthController.java << 'EOF'
package com.carpool.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "carpool-platform");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
EOF
    
    echo "✅ 健康检查接口创建完成"
}

# 主部署流程
main() {
    echo "📦 开始部署流程..."
    
    # 检查工具
    check_tools
    
    # 创建生产环境配置
    setup_production
    
    # 创建 Dockerfile
    create_dockerfiles
    
    # 创建环境变量文件
    create_env_file
    
    # 创建健康检查接口
    create_health_check
    
    echo ""
    echo "🎉 部署配置完成！"
    echo ""
    echo "📋 下一步操作："
    echo "1. 将 SSL 证书文件复制到 ssl/ 目录："
    echo "   cp /path/to/certificate.crt ssl/pincheba.icu.crt"
    echo "   cp /path/to/private.key ssl/pincheba.icu.key"
    echo ""
    echo "2. 编辑环境变量文件："
    echo "   nano .env.prod"
    echo ""
    echo "3. 启动所有服务："
    echo "   docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d"
    echo ""
    echo "4. 查看服务状态："
    echo "   docker-compose -f docker-compose.prod.yml ps"
    echo ""
    echo "5. 测试访问："
    echo "   curl https://pincheba.icu"
    echo "   curl https://pincheba.icu/api/health"
}

# 执行主函数
main