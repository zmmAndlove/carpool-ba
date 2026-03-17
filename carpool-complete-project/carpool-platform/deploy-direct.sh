#!/bin/bash

# 直接部署脚本 - 不使用Docker，直接在服务器上运行

set -e

echo "🚀 开始直接部署长途拼车平台到 pincheba.icu"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查工具
check_tools() {
    echo -e "${YELLOW}🔧 检查必要工具...${NC}"
    
    local missing_tools=()
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        missing_tools+=("Java 17+")
    else
        JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
        echo -e "${GREEN}✅ Java已安装: $JAVA_VERSION${NC}"
    fi
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        missing_tools+=("Maven")
    else
        echo -e "${GREEN}✅ Maven已安装${NC}"
    fi
    
    # 检查Node.js
    if ! command -v node &> /dev/null; then
        missing_tools+=("Node.js 18+")
    else
        NODE_VERSION=$(node --version)
        echo -e "${GREEN}✅ Node.js已安装: $NODE_VERSION${NC}"
    fi
    
    # 检查npm
    if ! command -v npm &> /dev/null; then
        missing_tools+=("npm")
    else
        NPM_VERSION=$(npm --version)
        echo -e "${GREEN}✅ npm已安装: $NPM_VERSION${NC}"
    fi
    
    # 检查MySQL
    if ! command -v mysql &> /dev/null; then
        echo -e "${YELLOW}⚠️  MySQL未安装，将使用H2内存数据库${NC}"
        USE_MYSQL=false
    else
        echo -e "${GREEN}✅ MySQL已安装${NC}"
        USE_MYSQL=true
    fi
    
    # 检查Redis
    if ! command -v redis-cli &> /dev/null; then
        echo -e "${YELLOW}⚠️  Redis未安装，将使用内存缓存${NC}"
        USE_REDIS=false
    else
        echo -e "${GREEN}✅ Redis已安装${NC}"
        USE_REDIS=true
    fi
    
    # 检查Nginx
    if ! command -v nginx &> /dev/null; then
        echo -e "${YELLOW}⚠️  Nginx未安装${NC}"
        USE_NGINX=false
    else
        NGINX_VERSION=$(nginx -v 2>&1 | cut -d'/' -f2)
        echo -e "${GREEN}✅ Nginx已安装: $NGINX_VERSION${NC}"
        USE_NGINX=true
    fi
    
    if [ ${#missing_tools[@]} -gt 0 ]; then
        echo -e "${RED}❌ 缺少必要工具: ${missing_tools[*]}${NC}"
        echo "请使用root权限安装:"
        echo "  sudo apt update"
        echo "  sudo apt install openjdk-17-jdk maven nodejs npm nginx mysql-server redis-server"
        exit 1
    fi
}

# 配置MySQL数据库
setup_mysql() {
    if [ "$USE_MYSQL" = true ]; then
        echo -e "${YELLOW}🗄️  配置MySQL数据库...${NC}"
        
        # 创建数据库和用户
        cat > setup-mysql.sql << 'EOF'
CREATE DATABASE IF NOT EXISTS carpool_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'carpool'@'localhost' IDENTIFIED BY 'carpool_prod_password_123';
GRANT ALL PRIVILEGES ON carpool_prod.* TO 'carpool'@'localhost';
FLUSH PRIVILEGES;
EOF
        
        echo "请使用root权限执行以下命令创建数据库："
        echo "  sudo mysql < setup-mysql.sql"
        echo ""
        echo "然后导入数据库结构："
        echo "  sudo mysql carpool_prod < docker/init.sql"
        echo ""
        echo "或者使用以下命令手动操作："
        echo "  sudo mysql -e \"CREATE DATABASE carpool_prod;\""
        echo "  sudo mysql -e \"CREATE USER 'carpool'@'localhost' IDENTIFIED BY '你的密码';\""
        echo "  sudo mysql -e \"GRANT ALL PRIVILEGES ON carpool_prod.* TO 'carpool'@'localhost';\""
        echo "  sudo mysql carpool_prod < docker/init.sql"
    fi
}

# 配置后端
setup_backend() {
    echo -e "${YELLOW}⚙️ 配置后端...${NC}"
    
    cd backend
    
    # 根据是否使用MySQL创建配置文件
    if [ "$USE_MYSQL" = true ]; then
        cat > src/main/resources/application-prod.yml << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/carpool_prod?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: carpool
    password: carpool_prod_password_123
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  data:
    redis:
      host: localhost
      port: 6379
      password: 
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

jwt:
  secret: production-jwt-secret-key-change-this-in-production
  expiration: 86400000

logging:
  level:
    com.carpool: INFO
  file:
    name: /var/log/carpool/backend.log

cors:
  allowed-origins: https://pincheba.icu,https://www.pincheba.icu
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
EOF
    else
        cat > src/main/resources/application-prod.yml << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:file:/var/lib/carpool/carpool_prod;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    username: sa
    password: 
    driver-class-name: org.h2.Driver
    hikari:
      maximum-pool-size: 10

  h2:
    console:
      enabled: false

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true

jwt:
  secret: production-jwt-secret-key-change-this-in-production
  expiration: 86400000

logging:
  level:
    com.carpool: INFO
  file:
    name: /var/log/carpool/backend.log

cors:
  allowed-origins: https://pincheba.icu,https://www.pincheba.icu
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
EOF
    fi
    
    # 创建健康检查接口
    mkdir -p src/main/java/com/carpool/controller
    
    cat > src/main/java/com/carpool/controller/HealthController.java << 'EOF'
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
    
    echo -e "${GREEN}✅ 后端配置完成${NC}"
    cd ..
}

# 配置前端
setup_frontend() {
    echo -e "${YELLOW}⚙️ 配置前端...${NC}"
    
    cd frontend
    
    # 创建生产环境配置
    cat > .env.production << 'EOF'
VITE_API_BASE_URL=https://pincheba.icu/api
VITE_APP_TITLE=长途拼车平台
VITE_APP_ENV=production
EOF
    
    # 安装依赖
    echo -e "${YELLOW}📦 安装前端依赖...${NC}"
    npm install
    
    # 构建前端
    echo -e "${YELLOW}🔨 构建前端...${NC}"
    npm run build
    
    # 创建部署目录
    sudo mkdir -p /var/www/pincheba.icu
    sudo cp -r dist/* /var/www/pincheba.icu/
    sudo chown -R www-data:www-data /var/www/pincheba.icu
    
    echo -e "${GREEN}✅ 前端配置完成${NC}"
    cd ..
}

# 配置Nginx
setup_nginx() {
    if [ "$USE_NGINX" = true ]; then
        echo -e "${YELLOW}🌐 配置Nginx...${NC}"
        
        # 创建Nginx配置
        sudo tee /etc/nginx/sites-available/pincheba.icu > /dev/null << 'EOF'
# HTTP重定向到HTTPS
server {
    listen 80;
    server_name pincheba.icu www.pincheba.icu;
    return 301 https://pincheba.icu$request_uri;
}

# HTTPS配置
server {
    listen 443 ssl http2;
    server_name pincheba.icu www.pincheba.icu;

    # SSL证书路径 - 需要你提供证书文件
    ssl_certificate /etc/ssl/certs/pincheba.icu.crt;
    ssl_certificate_key /etc/ssl/private/pincheba.icu.key;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # 前端静态文件
    root /var/www/pincheba.icu;
    index index.html;

    # 开启gzip
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # 处理Vue Router的history模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 健康检查
    location /health {
        proxy_pass http://localhost:8080/api/health;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        access_log off;
    }
}
EOF
        
        # 启用站点
        sudo ln -sf /etc/nginx/sites-available/pincheba.icu /etc/nginx/sites-enabled/
        sudo rm -f /etc/nginx/sites-enabled/default
        
        # 测试Nginx配置
        sudo nginx -t
        
        echo -e "${GREEN}✅ Nginx配置完成${NC}"
    fi
}

# 创建Systemd服务
create_systemd_services() {
    echo -e "${YELLOW}⚙️ 创建Systemd服务...${NC}"
    
    # 创建日志目录
    sudo mkdir -p /var/log/carpool
    sudo chown -R $USER:$USER /var/log/carpool
    
    # 后端服务
    sudo tee /etc/systemd/system/carpool-backend.service > /dev/null << EOF
[Unit]
Description=长途拼车平台后端服务
After=network.target
After=mysql.service
After=redis.service

[Service]
Type=simple
User=$USER
WorkingDirectory=$(pwd)/backend
ExecStart=/usr/bin/mvn spring-boot:run
Restart=always
RestartSec=10
StandardOutput=append:/var/log/carpool/backend.log
StandardError=append:/var/log/carpool/backend-error.log
Environment="SPRING_PROFILES_ACTIVE=prod"

[Install]
WantedBy=multi-user.target
EOF
    
    echo -e "${GREEN}✅ Systemd服务创建完成${NC}"
}

# 创建启动脚本
create_startup_scripts() {
    echo -e "${YELLOW}🚀 创建启动脚本...${NC}"
    
    # 启动所有服务脚本
    cat > start-production.sh << 'EOF'
#!/bin/bash

echo "🚀 启动长途拼车平台生产环境..."

# 启动后端服务
echo "启动后端服务..."
cd backend
mvn spring-boot:run -Dspring.profiles.active=prod &
BACKEND_PID=$!

echo "后端服务已启动 (PID: $BACKEND_PID)"
echo ""
echo "🌐 访问地址:"
echo "  前端: https://pincheba.icu"
echo "  后端API: https://pincheba.icu/api"
echo "  健康检查: https://pincheba.icu/api/health"
echo ""
echo "📋 按 Ctrl+C 停止服务"

# 等待用户中断
trap "kill $BACKEND_PID; echo '服务已停止'; exit" INT
wait
EOF
    
    chmod +x start-production.sh
    
    echo -e "${GREEN}✅ 启动脚本创建完成${NC}"
}

# 主部署流程
main() {
    echo -e "${GREEN}📦 开始直接部署流程...${NC}"
    
    # 检查工具
    check_tools
    
    # 配置MySQL
    setup_mysql
    
    # 配置后端
    setup_backend
    
    # 配置前端
    setup_frontend
    
    # 配置Nginx
    setup_nginx
    
    # 创建Systemd服务
    create_systemd_services
    
    # 创建启动脚本
    create_startup_scripts
    
    echo ""
    echo -e "${GREEN}🎉 直接部署配置完成！${NC}"
    echo ""
    echo -e "${YELLOW}📋 下一步操作：${NC}"
    echo ""
    echo "1. ${RED}重要：配置SSL证书${NC}"
    echo "   将你的SSL证书文件复制到以下位置："
    echo "   sudo cp pincheba.icu.crt /etc/ssl/certs/"
    echo "   sudo cp pincheba.icu.key /etc/ssl/private/"
    echo ""
    echo "2. ${YELLOW}配置数据库（如果使用MySQL）${NC}"
    echo "   按照上面的说明创建MySQL数据库和用户"
    echo ""
    echo "3. ${GREEN}启动服务${NC}"
    echo "   # 使用脚本启动"
    echo "   ./start-production.sh"
    echo ""
    echo "   # 或使用Systemd服务"
    echo "   sudo systemctl daemon-reload"
    echo "   sudo systemctl enable carpool-backend"
    echo "   sudo systemctl start carpool-backend"
    echo "   sudo systemctl restart nginx"
    echo ""
    echo "4. ${BLUE}验证部署${NC}"
    echo "   curl https://pincheba.icu"
    echo "   curl https://pincheba.icu/api/health"
    echo ""
    echo "5. ${PURPLE}监控日志${NC}"
    echo "   tail -f /var/log/carpool/backend.log"
    echo "   tail -f /var/log/nginx/access.log"
}

# 执行主函数
main