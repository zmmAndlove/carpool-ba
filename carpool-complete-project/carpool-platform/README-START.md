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
