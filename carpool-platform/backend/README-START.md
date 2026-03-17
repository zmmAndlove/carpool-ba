# 后端启动指南

## 🚀 快速启动（安装Java后）

### 1. 进入后端目录
```bash
cd /home/node/.openclaw/workspace/carpool-platform/backend
```

### 2. 启动Spring Boot应用
```bash
# 使用Maven启动
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/*.jar
```

### 3. 验证启动
```
后端启动后，访问以下地址：
- API地址: http://localhost:8080/api
- 健康检查: http://localhost:8080/api/health
- H2数据库控制台: http://localhost:8080/h2-console
```

## ⚙️ 配置说明

### 数据库配置
当前使用H2内存数据库（开发环境）：
- **JDBC URL**: `jdbc:h2:mem:carpool;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL`
- **用户名**: `sa`
- **密码**: (空)

### 测试数据
启动时自动创建测试数据：
- 用户: 13800138001 / 123456
- 行程: 3条测试行程

## 🔧 生产环境配置

### 切换到MySQL（推荐）
1. 安装MySQL：
   ```bash
   sudo apt install mysql-server
   sudo systemctl start mysql
   sudo systemctl enable mysql
   ```

2. 创建数据库：
   ```bash
   sudo mysql -e "CREATE DATABASE carpool_prod;"
   sudo mysql -e "CREATE USER 'carpool'@'localhost' IDENTIFIED BY '你的密码';"
   sudo mysql -e "GRANT ALL PRIVILEGES ON carpool_prod.* TO 'carpool'@'localhost';"
   sudo mysql carpool_prod < ../docker/init.sql
   ```

3. 修改配置文件 `src/main/resources/application-prod.yml`

## 📊 接口文档

### 用户认证
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息

### 行程管理
- `POST /api/trips` - 发布行程
- `GET /api/trips/search` - 搜索行程
- `GET /api/trips/{id}` - 获取行程详情
- `PUT /api/trips/{id}` - 更新行程
- `DELETE /api/trips/{id}` - 删除行程
- `POST /api/trips/{id}/join` - 加入行程
- `POST /api/trips/{id}/complete` - 完成行程

## 🐛 常见问题

### 1. 端口被占用
```bash
# 检查端口占用
netstat -tulpn | grep :8080

# 停止占用进程
kill -9 <PID>
```

### 2. Maven依赖下载慢
```bash
# 使用阿里云镜像
# 在 ~/.m2/settings.xml 中添加：
<mirror>
  <id>aliyunmaven</id>
  <mirrorOf>*</mirrorOf>
  <name>阿里云公共仓库</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

### 3. Java版本问题
```bash
# 检查Java版本
java -version

# 如果版本不对，设置JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

## 📈 监控和日志

### 日志文件
- 应用日志: `logs/carpool.log`
- 控制台输出: 启动时显示

### 健康检查
```bash
# 检查服务状态
curl http://localhost:8080/api/health

# 预期响应
{
  "status": "UP",
  "service": "carpool-platform",
  "timestamp": 1678888888888
}
```

## 🎯 下一步

1. **启动后端**: `mvn spring-boot:run`
2. **启动前端**: 在另一个终端运行 `npm run dev`
3. **访问应用**: http://localhost:3000
4. **测试功能**: 使用测试账号登录

## 📞 需要帮助？

如果启动遇到问题，请检查：
1. Java和Maven是否正确安装
2. 端口8080是否被占用
3. 依赖是否下载完整

启动成功后，前端将自动连接到后端API！