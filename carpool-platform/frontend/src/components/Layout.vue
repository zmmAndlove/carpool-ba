<template>
  <div class="layout-container">
    <!-- 导航栏 -->
    <el-header class="home-header">
      <div class="header-content">
        <div class="logo">
          <el-icon size="28" color="#007AFF">
            <Van />
          </el-icon>
          <span class="logo-text">长途拼车</span>
          <span class="logo-subtext">· 节假日回家不再难</span>
        </div>
        
        <!-- 移动端汉堡菜单 -->
        <div class="mobile-menu-button" @click="drawerVisible = true">
          <el-icon size="24"><Menu /></el-icon>
        </div>
        
        <!-- 桌面端导航菜单 -->
        <div class="nav-menu">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/search">找行程</el-menu-item>
            <el-menu-item index="/publish">发布行程</el-menu-item>
            <el-menu-item index="/comments">建议</el-menu-item>
          </el-menu>
        </div>
        
        <div class="user-actions">
          <template v-if="authStore.isAuthenticated">
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :src="authStore.user?.avatar" :size="36">
                  {{ authStore.user?.realName?.charAt(0) || '用户' }}
                </el-avatar>
                <span class="user-name">{{ authStore.user?.realName || authStore.user?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="myTrips">我的行程</el-dropdown-item>
                  <el-dropdown-item command="messages">消息中心</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button @click="$router.push('/login')" class="glass-button">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')" class="glass-button primary">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    
    <!-- 移动端侧边导航 -->
    <el-drawer
      v-model="drawerVisible"
      direction="rtl"
      size="80%"
      class="mobile-drawer"
    >
      <div class="drawer-content">
        <div class="drawer-header">
          <div class="logo">
            <el-icon size="24" color="#007AFF">
              <Van />
            </el-icon>
            <span class="logo-text">长途拼车</span>
          </div>
          <el-button
            type="text"
            @click="drawerVisible = false"
            class="close-button"
          >
            <el-icon size="24"><Close /></el-icon>
          </el-button>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          @select="handleDrawerMenuSelect"
          class="drawer-menu"
        >
          <el-menu-item index="/">
            <el-icon><Van /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/search">
            <el-icon><Search /></el-icon>
            <span>找行程</span>
          </el-menu-item>
          <el-menu-item index="/publish">
            <el-icon><Edit /></el-icon>
            <span>发布行程</span>
          </el-menu-item>
          <el-menu-item index="/comments">
            <el-icon><ChatRound /></el-icon>
            <span>建议</span>
          </el-menu-item>
          
          <el-divider />
          
          <template v-if="authStore.isAuthenticated">
            <el-menu-item index="/user">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
            <el-menu-item index="/user?tab=trips">
              <el-icon><Van /></el-icon>
              <span>我的行程</span>
            </el-menu-item>
            <el-menu-item index="/user?tab=messages">
              <el-icon><Bell /></el-icon>
              <span>消息中心</span>
            </el-menu-item>
            <el-menu-item @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-menu-item>
          </template>
          <template v-else>
            <el-menu-item @click="$router.push('/login')">
              <el-icon><User /></el-icon>
              <span>登录</span>
            </el-menu-item>
            <el-menu-item @click="$router.push('/register')">
              <el-icon><Plus /></el-icon>
              <span>注册</span>
            </el-menu-item>
          </template>
        </el-menu>
      </div>
    </el-drawer>

    <!-- 主内容区 -->
    <el-main class="layout-main">
      <slot></slot>
    </el-main>

    <!-- 页脚 -->
    <div class="home-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于我们</h3>
          <p>长途拼车平台，专注节假日返乡拼车</p>
          <p>让回家更简单，让旅途更温暖</p>
        </div>
        
        <div class="footer-section">
          <h3>安全保障</h3>
          <ul>
            <li>✅ 实名认证</li>
            <li>✅ 行程分享</li>
            <li>✅ 紧急联系</li>
            <li>✅ 用户评价</li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3>联系我们</h3>
          <p>客服电话: 400-123-4567</p>
          <p>邮箱: support@carpool.com</p>
          <p>工作时间: 9:00-21:00</p>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>© 2026 长途拼车平台 版权所有 | 京ICP备12345678号</p>
        <p>安全出行，快乐回家</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Van,
  ArrowDown,
  Menu,
  Close,
  Search,
  Edit,
  ChatRound,
  User,
  Bell,
  SwitchButton,
  Plus
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 移动端侧边栏状态
const drawerVisible = ref(false)

const activeMenu = computed(() => route.path)

const handleMenuSelect = (index: string) => {
  router.push(index)
}

// 处理侧边栏菜单选择
const handleDrawerMenuSelect = (index: string) => {
  router.push(index)
  drawerVisible.value = false
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/user')
      break
    case 'myTrips':
      router.push('/user?tab=trips')
      break
    case 'messages':
      router.push('/user?tab=messages')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理退出登录
const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
  drawerVisible.value = false
}
</script>

<style>
/* 全局样式覆盖，确保页脚显示正确 */
.home-footer,
.home-footer * {
  background-color: #1a1a2e !important;
  background: #1a1a2e !important;
  color: #ffffff !important;
}

.footer-section h3 {
  color: #ffffff !important;
}

.footer-section p,
.footer-section ul li {
  color: rgba(255, 255, 255, 0.7) !important;
}

.footer-section ul li:hover {
  color: rgba(255, 255, 255, 1) !important;
}

.footer-bottom p {
  color: rgba(255, 255, 255, 0.5) !important;
}
</style>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.home-header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.6);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

/* 移动端菜单按钮 */
.mobile-menu-button {
  display: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.mobile-menu-button:hover {
  background: rgba(0, 122, 255, 0.1);
}

/* 移动端侧边栏 */
.mobile-drawer :deep(.el-drawer__header) {
  display: none;
}

.drawer-content {
  height: 100%;
  padding: 20px;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.close-button {
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.close-button:hover {
  background: rgba(0, 0, 0, 0.05);
}

.drawer-menu {
  width: 100%;
}

.drawer-menu :deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  font-size: 16px;
  margin-bottom: 8px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.drawer-menu :deep(.el-menu-item:hover) {
  background: rgba(0, 122, 255, 0.1);
}

.drawer-menu :deep(.el-menu-item.is-active) {
  background: rgba(0, 122, 255, 0.15);
  color: #007AFF;
  font-weight: 600;
}

.drawer-menu :deep(.el-icon) {
  margin-right: 12px;
  font-size: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo:hover {
  transform: scale(1.02);
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #007AFF;
  letter-spacing: -0.3px;
  background: linear-gradient(135deg, #007AFF 0%, #0056b3 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-subtext {
  color: #666;
  font-size: 14px;
  font-weight: 400;
  opacity: 0.8;
}

.nav-menu {
  flex: 1;
  display: flex;
  justify-content: center;
  min-width: 400px;
}

:deep(.el-menu) {
  width: 100%;
  min-width: 360px;
  display: flex;
  justify-content: center;
  background: transparent;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu--horizontal > .el-menu-item) {
  height: 64px;
  line-height: 64px;
  margin: 0 12px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  color: #333;
}

:deep(.el-menu--horizontal > .el-menu-item:hover) {
  background: rgba(0, 122, 255, 0.08);
  color: #007AFF;
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  background: rgba(0, 122, 255, 0.12);
  color: #007AFF;
  font-weight: 600;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.glass-button {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(0, 122, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
}

.glass-button:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(0, 122, 255, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.15);
}

.glass-button.primary {
  background: rgba(0, 122, 255, 0.9);
  border-color: rgba(0, 122, 255, 0.5);
}

.glass-button.primary:hover {
  background: rgba(0, 122, 255, 1);
  box-shadow: 0 6px 16px rgba(0, 122, 255, 0.3);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.layout-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
  background: inherit;
}

.layout-container .home-footer {
  background-color: #1a1a2e !important;
  background: #1a1a2e !important;
  padding: 40px 20px 20px;
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #ffffff !important;
  min-height: 300px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
  background-color: #1a1a2e !important;
  background: #1a1a2e !important;
  color: #ffffff !important;
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 16px;
  color: #ffffff !important;
  font-weight: 600;
  letter-spacing: -0.3px;
}

.footer-section p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7) !important;
  margin: 8px 0;
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
  background-color: #1a1a2e !important;
  background: #1a1a2e !important;
}

.footer-section ul li {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7) !important;
  margin: 8px 0;
  transition: all 0.3s;
  background-color: #1a1a2e !important;
  background: #1a1a2e !important;
}

.footer-section ul li:hover {
  color: rgba(255, 255, 255, 1) !important;
  transform: translateX(4px);
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background-color: #1a1a2e !important;
  background: #1a1a2e !important;
  color: #ffffff !important;
}

.footer-bottom p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5) !important;
  margin: 4px 0;
  background-color: #1a1a2e !important;
  background: #1a1a2e !important;
}

@media (max-width: 768px) {
  .home-header {
    padding: 0 15px;
  }
  
  .header-content {
    flex-direction: row;
    gap: 12px;
    padding: 12px 0;
  }
  
  /* 显示移动端菜单按钮 */
  .mobile-menu-button {
    display: block;
  }
  
  /* 隐藏桌面端导航菜单 */
  .nav-menu {
    display: none;
  }
  
  .logo {
    gap: 8px;
    flex: 1;
  }
  
  .logo-text {
    font-size: 18px;
  }
  
  .logo-subtext {
    font-size: 12px;
    display: none;
  }
  
  .user-actions {
    gap: 8px;
  }
  
  .glass-button {
    padding: 8px 16px;
    font-size: 14px;
  }
  
  .user-info {
    padding: 4px 8px;
    gap: 6px;
  }
  
  .user-name {
    font-size: 13px;
    display: none;
  }
  
  .layout-main {
    padding: 15px;
  }
  
  .home-footer {
    padding: 30px 15px 15px;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
    margin-bottom: 30px;
  }
  
  .footer-section h3 {
    font-size: 16px;
  }
  
  .footer-section p,
  .footer-section ul li {
    font-size: 13px;
  }
  
  .footer-bottom p {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .home-header {
    padding: 0 12px;
  }
  
  .header-content {
    gap: 8px;
    padding: 10px 0;
  }
  
  .logo {
    gap: 6px;
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .mobile-menu-button {
    padding: 6px;
  }
  
  .user-actions {
    gap: 6px;
  }
  
  .glass-button {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  .user-info {
    padding: 3px 6px;
  }
  
  .layout-main {
    padding: 12px;
  }
  
  .home-footer {
    padding: 25px 12px 12px;
  }
  
  .footer-content {
    gap: 25px;
    margin-bottom: 25px;
  }
  
  .footer-section h3 {
    font-size: 15px;
    margin-bottom: 12px;
  }
  
  .footer-section p,
  .footer-section ul li {
    font-size: 12px;
    margin: 6px 0;
  }
  
  .footer-bottom {
    padding-top: 15px;
  }
  
  .footer-bottom p {
    font-size: 11px;
    margin: 3px 0;
  }
  
  /* 移动端侧边栏 */
  .drawer-content {
    padding: 15px;
  }
  
  .drawer-header {
    margin-bottom: 20px;
  }
  
  .drawer-menu :deep(.el-menu-item) {
    height: 52px;
    line-height: 52px;
    font-size: 15px;
  }
  
  .drawer-menu :deep(.el-icon) {
    margin-right: 10px;
    font-size: 18px;
  }
}
</style>