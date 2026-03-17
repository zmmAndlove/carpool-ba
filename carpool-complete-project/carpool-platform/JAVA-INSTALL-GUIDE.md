# Java和Maven安装指南

## 📦 安装步骤（使用root权限）

### 1. 登录服务器
```bash
ssh root@你的服务器IP
```

### 2. 更新系统并安装Java 17和Maven
```bash
# 更新包列表
apt update

# 安装Java 17 JDK
apt install -y openjdk-17-jdk

# 安装Maven
apt install -y maven

# 验证安装
java -version
mvn -version
```

### 3. 安装其他可选组件（推荐）
```bash
# 安装Nginx（Web服务器）
apt install -y nginx

# 安装MySQL（数据库）
apt install -y mysql-server

# 安装Redis（缓存）
apt install -y redis-server

# 启动服务
systemctl start nginx mysql redis
systemctl enable nginx mysql redis
```

## 🔧 环境验证

安装完成后，请运行以下命令验证：

```bash
# 检查Java版本（应该显示17.x）
java -version

# 检查Maven版本
mvn -version

# 检查安装位置
which java
which mvn
```

预期输出示例：
```
java version "17.0.10" 2024-01-16 LTS
Java(TM) SE Runtime Environment (build 17.0.10+11-LTS-240)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.10+11-LTS-240, mixed mode, sharing)

Apache Maven 3.9.6
Maven home: /usr/share/maven
Java version: 17.0.10, vendor: Oracle Corporation, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.15.0-91-generic", arch: "amd64", family: "unix"
```

## 📁 SSL证书上传指南

### 证书文件位置
将你的SSL证书上传到以下位置：

```bash
# 证书文件
/etc/ssl/certs/pincheba.icu.crt

# 私钥文件
/etc/ssl/private/pincheba.icu.key
```

### 上传命令（从本地电脑）
```bash
# 上传证书
scp pincheba.icu.crt root@服务器IP:/etc/ssl/certs/

# 上传私钥
scp pincheba.icu.key root@服务器IP:/etc/ssl/private/

# 设置权限
ssh root@服务器IP
chmod 644 /etc/ssl/certs/pincheba.icu.crt
chmod 600 /etc/ssl/private/pincheba.icu.key
```

### 证书格式要求
- **证书文件** (.crt 或 .pem): 包含完整的证书链
- **私钥文件** (.key): 无密码的私钥文件

## 🚀 快速启动命令

安装完成后，我可以立即启动服务：

### 启动后端（需要Java）
```bash
cd /home/node/.openclaw/workspace/carpool-platform/backend
mvn spring-boot:run
```

### 启动前端（已就绪）
```bash
cd /home/node/.openclaw/workspace/carpool-platform/frontend
npm run dev
```

## 🌐 访问地址

- **前端开发服务器**: http://localhost:3000
- **后端API**: http://localhost:8080/api
- **生产环境**: https://pincheba.icu

## 🔍 故障排除

### Java安装问题
```bash
# 如果Java未正确安装，尝试：
apt remove openjdk-*
apt autoremove
apt install openjdk-17-jdk
```

### Maven问题
```bash
# 如果Maven找不到Java
update-alternatives --config java
# 选择Java 17
```

### 端口冲突
```bash
# 检查端口占用
netstat -tulpn | grep -E ':(80|443|3000|8080)'

# 停止占用端口的服务
systemctl stop apache2 nginx
```

## 📞 安装完成后

请告诉我：
1. ✅ Java和Maven安装完成
2. ✅ SSL证书已上传
3. ✅ 需要我启动哪个服务

我将立即启动完整的拼车平台！