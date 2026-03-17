#!/bin/bash

# 长途拼车平台本地部署脚本（无Docker）
# 域名: pincheba.icu

set -e

echo "🚀 开始本地部署长途拼车平台到 pincheba.icu"

# 检查必要工具
check_tools() {
    echo "🔧 检查必要工具..."
    command -v node >/dev/null 2>&1 || { echo "❌ 请先安装 Node.js 18+"; exit 1; }
    command -v npm >/dev/null 2>&1 || { echo "❌ 请先安装 npm"; exit 1; }
    command -v java >/dev/null 2>&1 || { echo "❌ 请先安装 Java 17+"; exit 1; }
    command -v mvn >/dev/null 2>&1 || { echo "❌ 请先安装 Maven"; exit 1; }
    echo "✅ 所有必要工具已安装"
}

# 安装MySQL和Redis（如果未安装）
install_database() {
    echo "🗄️  检查数据库..."
    
    # 检查MySQL
    if ! command -v mysql &> /dev/null; then
        echo "⚠️  MySQL未安装，将使用内存数据库（H2）"
        USE_H2=true
    else
        echo "✅ MySQL已安装"
        USE_H2=false
    fi
    
    # 检查Redis
    if ! command -v redis-cli &> /dev/null; then
        echo "⚠️  Redis未安装，将使用内存缓存"
        USE_REDIS=false
    else
        echo "✅ Redis已安装"
        USE_REDIS=true
    fi
}

# 配置后端
setup_backend() {
    echo "⚙️ 配置后端..."
    
    cd backend
    
    # 创建本地配置文件
    cat > src/main/resources/application-local.yml << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:carpool;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
    driver-class-name: org.h2.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  data:
    redis:
      host: localhost
      port: 6379
      database: 0

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true

jwt:
  secret: local-jwt-secret-key-for-development-only
  expiration: 86400000 # 24小时

logging:
  level:
    com.carpool: DEBUG
  file:
    name: logs/carpool-local.log

cors:
  allowed-origins: http://localhost:3000,http://localhost:5173
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
EOF

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
        response.put("mode", "local");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
EOF
    
    echo "✅ 后端配置完成"
    cd ..
}

# 配置前端
setup_frontend() {
    echo "⚙️ 配置前端..."
    
    cd frontend
    
    # 创建本地环境文件
    cat > .env.local << 'EOF'
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=长途拼车平台（本地开发）
VITE_APP_ENV=local
EOF
    
    # 安装依赖
    echo "📦 安装前端依赖..."
    npm install
    
    echo "✅ 前端配置完成"
    cd ..
}

# 创建启动脚本
create_startup_scripts() {
    echo "🚀 创建启动脚本..."
    
    # 后端启动脚本
    cat > start-backend.sh << 'EOF'
#!/bin/bash
echo "启动后端服务..."
cd backend
mvn spring-boot:run
EOF
    
    # 前端启动脚本
    cat > start-frontend.sh << 'EOF'
#!/bin/bash
echo "启动前端服务..."
cd frontend
npm run dev
EOF
    
    # 一键启动脚本
    cat > start-all.sh << 'EOF'
#!/bin/bash
echo "🚀 启动长途拼车平台..."

# 启动后端
cd backend
mvn spring-boot:run &
BACKEND_PID=$!

# 等待后端启动
sleep 10

# 启动前端
cd ../frontend
npm run dev &
FRONTEND_PID=$!

echo "✅ 服务已启动"
echo "后端：http://localhost:8080"
echo "前端：http://localhost:3000"
echo "API文档：http://localhost:8080/api"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
trap "kill $BACKEND_PID $FRONTEND_PID; echo '服务已停止'; exit" INT
wait
EOF
    
    chmod +x start-backend.sh start-frontend.sh start-all.sh
    
    echo "✅ 启动脚本创建完成"
}

# 创建测试数据
create_test_data() {
    echo "📊 创建测试数据..."
    
    cat > test-data.sql << 'EOF'
-- 测试用户数据
INSERT INTO users (phone, password, real_name, gender, age, hometown_province, hometown_city, current_province, current_city, created_at) VALUES
('13800138001', '$2a$10$YourHashedPassword', '张三', '男', 28, '北京市', '北京市', '北京市', '北京市', NOW()),
('13800138002', '$2a$10$YourHashedPassword', '李四', '女', 25, '上海市', '上海市', '上海市', '上海市', NOW()),
('13800138003', '$2a$10$YourHashedPassword', '王五', '男', 32, '广东省', '广州市', '广东省', '深圳市', NOW());

-- 测试行程数据
INSERT INTO trips (user_id, trip_type, departure_province, departure_city, departure_district, departure_detail, destination_province, destination_city, destination_district, destination_detail, departure_time, seats_available, seats_total, price_per_person, description, car_model, car_plate, status, created_at) VALUES
(1, 'car_owner', '北京市', '北京市', '朝阳区', '望京SOHO', '河北省', '石家庄市', '长安区', '火车站', DATE_ADD(NOW(), INTERVAL 2 DAY), 3, 4, 150, '春节回家，可放行李，走高速', '丰田卡罗拉', '京A·12345', 'pending', NOW()),
(2, 'passenger', '上海市', '上海市', '浦东新区', '陆家嘴', '江苏省', '南京市', '鼓楼区', '新街口', DATE_ADD(NOW(), INTERVAL 3 DAY), 1, 1, 120, '寻找顺风车回家过年', NULL, NULL, 'pending', NOW()),
(3, 'car_owner', '广东省', '深圳市', '南山区', '科技园', '广东省', '广州市', '天河区', '体育中心', DATE_ADD(NOW(), INTERVAL 1 DAY), 2, 3, 80, '周末回家，可接送', '本田雅阁', '粤B·88888', 'pending', NOW());
EOF
    
    echo "✅ 测试数据创建完成"
}

# 主部署流程
main() {
    echo "📦 开始本地部署流程..."
    
    # 检查工具
    check_tools
    
    # 安装数据库
    install_database
    
    # 配置后端
    setup_backend
    
    # 配置前端
    setup_frontend
    
    # 创建启动脚本
    create_startup_scripts
    
    # 创建测试数据
    create_test_data
    
    echo ""
    echo "🎉 本地部署配置完成！"
    echo ""
    echo "📋 启动方式："
    echo "1. 一键启动所有服务："
    echo "   ./start-all.sh"
    echo ""
    echo "2. 分别启动服务："
    echo "   后端：./start-backend.sh"
    echo "   前端：./start-frontend.sh"
    echo ""
    echo "3. 访问地址："
    echo "   前端：http://localhost:3000"
    echo "   后端API：http://localhost:8080/api"
    echo "   健康检查：http://localhost:8080/api/health"
    echo "   H2控制台：http://localhost:8080/h2-console"
    echo ""
    echo "4. 测试账号："
    echo "   手机号：13800138001"
    echo "   密码：123456"
    echo ""
    echo "⚠️  注意：这是本地开发环境，使用H2内存数据库"
    echo "    生产环境请使用MySQL和Redis"
}

# 执行主函数
main