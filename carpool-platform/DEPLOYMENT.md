# 长途拼车平台部署指南

## 部署到 pincheba.icu

### 1. 服务器要求
- Ubuntu 20.04+ / CentOS 8+
- Docker 20.10+ 和 Docker Compose 2.0+
- 至少 2GB RAM，20GB 磁盘空间
- 开放端口：80, 443, 3306, 6379, 8080

### 2. 域名配置
1. 在域名注册商处配置 DNS：
   - A 记录：pincheba.icu → 服务器IP
   - A 记录：www.pincheba.icu → 服务器IP
   - CNAME 记录：api.pincheba.icu → pincheba.icu

2. 获取 SSL 证书（推荐使用 Let's Encrypt）：
   ```bash
   # 安装 certbot
   sudo apt install certbot python3-certbot-nginx
   
   # 获取证书
   sudo certbot certonly --nginx -d pincheba.icu -d www.pincheba.icu
   
   # 复制证书到项目目录
   sudo cp /etc/letsencrypt/live/pincheba.icu/fullchain.pem ./ssl/pincheba.icu.crt
   sudo cp /etc/letsencrypt/live/pincheba.icu/privkey.pem ./ssl/pincheba.icu.key
   sudo chmod 644 ./ssl/*.pem
   ```

### 3. 部署步骤
1. 上传项目到服务器
2. 配置环境变量：
   ```bash
   cp .env.prod.example .env.prod
   nano .env.prod  # 修改密码和密钥
   ```

3. 启动服务：
   ```bash
   # 给部署脚本执行权限
   chmod +x deploy.sh
   
   # 运行部署脚本
   ./deploy.sh
   
   # 启动所有服务
   docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d
   ```

4. 验证部署：
   ```bash
   # 查看服务状态
   docker-compose -f docker-compose.prod.yml ps
   
   # 查看日志
   docker-compose -f docker-compose.prod.yml logs -f
   
   # 测试访问
   curl https://pincheba.icu
   curl https://pincheba.icu/api/health
   ```

### 4. 常用命令
```bash
# 启动服务
docker-compose -f docker-compose.prod.yml up -d

# 停止服务
docker-compose -f docker-compose.prod.yml down

# 查看日志
docker-compose -f docker-compose.prod.yml logs -f backend
docker-compose -f docker-compose.prod.yml logs -f frontend

# 重启服务
docker-compose -f docker-compose.prod.yml restart backend

# 备份数据库
docker exec carpool-mysql mysqldump -u root -p carpool_prod > backup.sql

# 更新代码后重新部署
docker-compose -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.prod.yml up -d --force-recreate
```

### 5. 监控和维护
1. 设置日志轮转
2. 配置监控告警
3. 定期备份数据库
4. 更新 SSL 证书（每90天）

### 6. 故障排除
1. 检查端口是否开放：`sudo netstat -tulpn | grep :80`
2. 检查 Docker 服务：`sudo systemctl status docker`
3. 查看容器日志：`docker logs carpool-backend`
4. 检查数据库连接：`docker exec -it carpool-mysql mysql -u root -p`

### 7. 安全建议
1. 修改所有默认密码
2. 配置防火墙规则
3. 定期更新 Docker 镜像
4. 启用数据库备份
5. 监控异常访问

### 8. 性能优化
1. 配置 Redis 缓存
2. 启用数据库索引
3. 优化 Nginx 配置
4. 启用 CDN 加速
5. 配置负载均衡（可选）

## 快速部署脚本
```bash
#!/bin/bash
# 一键部署脚本
set -e

echo "🚀 开始部署长途拼车平台到 pincheba.icu"

# 1. 安装必要工具
sudo apt update
sudo apt install -y docker.io docker-compose certbot python3-certbot-nginx

# 2. 获取 SSL 证书
sudo certbot certonly --standalone -d pincheba.icu -d www.pincheba.icu --agree-tos --email your-email@example.com

# 3. 复制证书
sudo mkdir -p ssl
sudo cp /etc/letsencrypt/live/pincheba.icu/fullchain.pem ssl/pincheba.icu.crt
sudo cp /etc/letsencrypt/live/pincheba.icu/privkey.pem ssl/pincheba.icu.key
sudo chmod 644 ssl/*.pem

# 4. 配置环境变量
cat > .env.prod << EOF
MYSQL_ROOT_PASSWORD=$(openssl rand -base64 32)
DB_PASSWORD=$(openssl rand -base64 32)
REDIS_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 64)
DOMAIN=pincheba.icu
EOF

# 5. 启动服务
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

echo "✅ 部署完成！访问 https://pincheba.icu"
```

## 更新部署
当代码更新后，执行以下命令：
```bash
# 拉取最新代码
git pull origin main

# 重新构建并启动
docker-compose -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.prod.yml up -d --force-recreate
```

## 联系方式
如有部署问题，请联系技术支持。