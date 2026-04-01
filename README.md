# 长途拼车平台 (Carpool Platform)

## 项目简介

长途拼车平台是一个基于前后端分离架构的 Web 应用，旨在为用户提供长途拼车服务，包括发布拼车信息、搜索拼车信息、用户认证、评论系统和管理后台等功能。

## 技术栈

### 前端技术栈
- **框架**：Vue 3 + TypeScript
- **构建工具**：Vite 5
- **UI组件库**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios
- **日期处理**：dayjs
- **代码规范**：ESLint + Prettier

### 后端技术栈
- **框架**：Spring Boot 2.7.15
- **语言**：Java 8
- **ORM**：MyBatis 2.2.2
- **数据库**：MySQL 8.0.33
- **连接池**：Druid 1.2.20
- **安全**：Spring Security + JWT (jjwt 0.12.3)
- **缓存**：Spring Data Redis
- **对象映射**：MapStruct 1.5.5
- **工具库**：Lombok、Guava、Apache Commons Lang3
- **依赖管理**：Maven

## 项目结构

```
carpool-ba/
├── carpool-platform/
│   ├── frontend/              # 前端项目 (Vue 3 + TS + Element Plus)
│   │   └── src/
│   │       ├── views/         # 页面组件 (13个)
│   │       │   ├── HomeView.vue          # 首页
│   │       │   ├── LoginView.vue         # 登录
│   │       │   ├── RegisterView.vue      # 注册
│   │       │   ├── PublishView.vue       # 发布行程
│   │       │   ├── SearchView.vue        # 搜索行程
│   │       │   ├── TripDetailView.vue    # 行程详情
│   │       │   ├── UserCenterView.vue    # 个人中心
│   │       │   ├── CommentsView.vue      # 用户评论
│   │       │   ├── NotFoundView.vue      # 404页面
│   │       │   └── admin/                # 管理后台
│   │       │       ├── AdminDashboard.vue # 管理面板
│   │       │       ├── AdminUsers.vue    # 用户管理
│   │       │       ├── AdminTrips.vue    # 行程管理
│   │       │       └── AdminComments.vue # 评论管理
│   │       ├── components/    # 公共组件
│   │       ├── router/        # 路由配置
│   │       ├── stores/        # Pinia 状态管理
│   │       ├── styles/        # 样式文件（设计令牌、玻璃拟态主题）
│   │       └── utils/         # 工具函数
│   │
│   ├── backend/               # 后端项目 (Spring Boot + MyBatis)
│   │   └── src/main/java/com/carpool/
│   │       ├── config/        # 配置层（Security、JWT、CORS）
│   │       ├── controller/    # 控制层（Auth、Trip、Comment、Admin）
│   │       ├── service/       # 服务层
│   │       ├── mapper/        # MyBatis Mapper
│   │       ├── entity/        # 实体类（User、Trip、Comment）
│   │       └── dto/           # 数据传输对象
│   │
│   ├── deploy.sh              # 部署脚本
│   ├── deploy-local.sh        # 本地部署
│   ├── deploy-direct.sh       # 直接部署
│   ├── simple-deploy.sh       # 简单部署
│   ├── start-frontend.js      # 前端启动脚本
│   └── start-backend.js       # 后端启动脚本
├── package.json
├── README.md
└── test-login.ps1
```

## 功能特性

### 前端功能
- ✅ 用户注册/登录
- ✅ 发布拼车信息
- ✅ 搜索拼车信息
- ✅ 查看行程详情
- ✅ 用户中心

### 后端功能
- ✅ 用户认证与授权（基于JWT）
- ✅ 行程管理
- ✅ 健康检查
- ✅ 数据库操作

## 安装与运行

### 前置条件
- **前端**：Node.js 16+，npm 7+
- **后端**：Java 8+，Maven 3.6+
- **数据库**：MySQL 8.0+

### 前端安装与运行

1. 进入前端目录
   ```bash
   cd carpool-complete-project/carpool-platform/frontend
   ```

2. 安装依赖
   ```bash
   npm install
   ```

3. 启动开发服务器
   ```bash
   npm run dev
   ```

4. 访问前端应用
   ```
   http://localhost:3000
   ```

### 后端安装与运行

1. 进入后端目录
   ```bash
   cd carpool-platform/backend
   ```

2. 安装依赖
   ```bash
   mvn install
   ```

3. 启动后端服务
   ```bash
   mvn spring-boot:run
   ```

4. 访问后端API
   ```
   http://localhost:8080/api
   ```

## 数据库配置

后端默认连接到远程MySQL数据库：

```yaml
spring:
  datasource:
    url: jdbc:mysql://101.43.210.158:3306/carpool_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: abc123
    driver-class-name: com.mysql.cj.jdbc.Driver
```

如需使用本地数据库，请修改 `carpool-platform/backend/src/main/resources/application.yml` 文件中的数据库配置。

## API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息

### 行程接口
- `POST /api/trips` - 发布拼车信息
- `GET /api/trips` - 获取拼车列表
- `GET /api/trips/{id}` - 获取行程详情
- `PUT /api/trips/{id}` - 更新行程信息
- `DELETE /api/trips/{id}` - 删除行程

### 健康检查
- `GET /api/health` - 健康检查

## 部署

项目提供了多种部署脚本：

- `deploy.sh` - 完整部署脚本
- `deploy-local.sh` - 本地部署脚本
- `deploy-direct.sh` - 直接部署脚本
- `simple-deploy.sh` - 简单部署脚本

## 开发指南

### 前端开发
1. 代码风格：使用ESLint和Prettier保持代码规范
2. 组件命名：使用PascalCase命名组件
3. 文件命名：使用kebab-case命名文件

### 后端开发
1. 代码风格：遵循Spring Boot编码规范
2. 包结构：按照功能模块划分包
3. 异常处理：统一处理异常，返回标准格式

## 测试

### 前端测试
```bash
npm run test
```

### 后端测试
```bash
mvn test
```

## 贡献

欢迎贡献代码！请遵循以下流程：

1. Fork 仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

- 项目地址：[https://github.com/zmmAndlove/carpool-ba](https://github.com/zmmAndlove/carpool-ba)
- 问题反馈：[GitHub Issues](https://github.com/zmmAndlove/carpool-ba/issues)

---

**长途拼车平台** - 让出行更便捷、更环保！