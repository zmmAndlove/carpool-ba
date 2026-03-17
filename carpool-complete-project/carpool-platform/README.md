# 长途拼车平台

## 🚀 项目概述
长途拼车平台，专注节假日返乡拼车，解决火车票难抢问题。

## 🏗️ 技术栈
### 后端
- Java 17
- Spring Boot 3.x
- MyBatis
- MySQL 8
- Redis
- Spring Security + JWT

### 前端
- Vue 3 + TypeScript
- Element Plus
- Vite
- Pinia
- Vue Router

## 📁 项目结构
```
carpool-platform/
├── backend/          # Spring Boot后端
├── frontend/         # Vue 3前端
├── docker/           # Docker配置
└── README.md         # 项目说明
```

## 🚀 快速开始
```bash
# 使用Docker启动服务
cd docker
docker-compose up -d

# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
npm run dev
```

## 📊 核心功能
1. **用户系统** - 注册、登录、资料管理
2. **行程发布** - 发布车找人/人找车
3. **行程搜索** - 按地点时间搜索
4. **匹配系统** - 智能匹配同乡
5. **消息系统** - 站内聊天
6. **评价系统** - 双向评价

## 🔧 环境要求
- Java 17+
- Node.js 18+
- MySQL 8+
- Redis 6+
- Docker (可选)

## 📞 联系方式
如有问题，请联系项目负责人。