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
          <h3>平台特色</h3>
          <ul>
            <li>⚡ 快速匹配</li>
            <li>🔒 隐私保护</li>
            <li>💬 直接沟通</li>
            <li>🔑 自主决策</li>
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

<style scoped>
/* 像素风格布局 */
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg0);
  background-image: 
    linear-gradient(45deg, var(--bg1) 25%, transparent 25%),
    linear-gradient(-45deg, var(--bg1) 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, var(--bg1) 75%),
    linear-gradient(-45deg, transparent 75%, var(--bg1) 75%);
  background-size: 20px 20px;
  background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
}

.home-header {
  background: var(--pixel-bg);
  border-bottom: 3px solid var(--border-dark);
  box-shadow: var(--pixel-shadow);
  padding: 12px 20px;
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
  border: 2px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--pixel-bg);
  box-shadow: var(--pixel-shadow);
  transition: all 0.1s ease;
}

.mobile-menu-button:hover {
  background: var(--pixel-hover);
  transform: translate(1px, 1px);
  box-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
}

/* 移动端侧边栏 */
.mobile-drawer :deep(.el-drawer__header) {
  display: none;
}

.drawer-content {
  height: 100%;
  padding: 20px;
  background: var(--bg0);
  background-image: 
    linear-gradient(45deg, var(--bg1) 25%, transparent 25%),
    linear-gradient(-45deg, var(--bg1) 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, var(--bg1) 75%),
    linear-gradient(-45deg, transparent 75%, var(--bg1) 75%);
  background-size: 20px 20px;
  background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--border);
}

.close-button {
  padding: 8px;
  border: 2px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--pixel-bg);
  box-shadow: var(--pixel-shadow);
  transition: all 0.1s ease;
}

.close-button:hover {
  background: var(--pixel-hover);
  transform: translate(1px, 1px);
  box-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
}

.drawer-menu {
  width: 100%;
}

.drawer-menu :deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  font-size: 14px;
  margin-bottom: 8px;
  border: 2px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--pixel-bg);
  box-shadow: var(--pixel-shadow);
  transition: all 0.1s ease;
}

.drawer-menu :deep(.el-menu-item:hover) {
  background: var(--pixel-hover);
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
}

.drawer-menu :deep(.el-menu-item.is-active) {
  background: var(--brand);
  color: white;
  border-color: var(--border-dark);
  box-shadow: var(--pixel-shadow-hover);
}

.drawer-menu :deep(.el-icon) {
  margin-right: 12px;
  font-size: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.1s ease;
}

.logo:hover {
  transform: scale(1.02);
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: var(--brand);
  text-shadow: 2px 2px 0px rgba(0, 0, 0, 0.2);
}

.logo-subtext {
  color: var(--text-2);
  font-size: 12px;
  font-weight: normal;
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
  height: 48px;
  line-height: 48px;
  margin: 0 8px;
  border: 2px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--pixel-bg);
  box-shadow: var(--pixel-shadow);
  transition: all 0.1s ease;
  font-weight: normal;
  color: var(--text-1);
}

:deep(.el-menu--horizontal > .el-menu-item:hover) {
  background: var(--pixel-hover);
  transform: translate(1px, 1px);
  box-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  background: var(--brand);
  color: white;
  border-color: var(--border-dark);
  box-shadow: var(--pixel-shadow-hover);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border: 2px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--pixel-bg);
  box-shadow: var(--pixel-shadow);
  transition: all 0.1s ease;
}

.user-info:hover {
  background: var(--pixel-hover);
  transform: translate(1px, 1px);
  box-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
}

.user-name {
  font-size: 12px;
  color: var(--text-1);
  font-weight: normal;
}

.layout-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
  background: transparent;
}

/* 像素风格页脚 */
.home-footer {
  background: var(--bg2);
  border-top: 3px solid var(--border-dark);
  padding: 30px 20px 20px;
  margin-top: auto;
  color: var(--text-1);
  min-height: 300px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.2);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.footer-section h3 {
  font-size: 16px;
  margin-bottom: 16px;
  color: var(--brand);
  font-weight: bold;
  text-shadow: 1px 1px 0px rgba(0, 0, 0, 0.2);
  border-bottom: 2px solid var(--border);
  padding-bottom: 8px;
}

.footer-section p {
  font-size: 12px;
  color: var(--text-2);
  margin: 8px 0;
  line-height: 1.4;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  font-size: 12px;
  color: var(--text-2);
  margin: 8px 0;
  transition: all 0.1s ease;
}

.footer-section ul li:hover {
  color: var(--pixel-hover);
  transform: translateX(4px);
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 2px solid var(--border);
  color: var(--text-3);
}

.footer-bottom p {
  font-size: 11px;
  margin: 4px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-header {
    padding: 10px 15px;
  }
  
  .header-content {
    flex-direction: row;
    gap: 10px;
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
    font-size: 16px;
  }
  
  .logo-subtext {
    font-size: 10px;
    display: none;
  }
  
  .user-actions {
    gap: 8px;
  }
  
  .user-info {
    padding: 4px 8px;
    gap: 6px;
  }
  
  .user-name {
    font-size: 11px;
    display: none;
  }
  
  .layout-main {
    padding: 15px;
  }
  
  .home-footer {
    padding: 25px 15px 15px;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 25px;
    margin-bottom: 25px;
  }
  
  .footer-section h3 {
    font-size: 14px;
  }
  
  .footer-section p,
  .footer-section ul li {
    font-size: 11px;
  }
  
  .footer-bottom p {
    font-size: 10px;
  }
}

@media (max-width: 480px) {
  .home-header {
    padding: 8px 12px;
  }
  
  .header-content {
    gap: 8px;
  }
  
  .logo {
    gap: 6px;
  }
  
  .logo-text {
    font-size: 14px;
  }
  
  .mobile-menu-button {
    padding: 6px;
  }
  
  .user-actions {
    gap: 6px;
  }
  
  .user-info {
    padding: 3px 6px;
  }
  
  .layout-main {
    padding: 12px;
  }
  
  .home-footer {
    padding: 20px 12px 12px;
  }
  
  .footer-content {
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .footer-section h3 {
    font-size: 13px;
    margin-bottom: 12px;
  }
  
  .footer-section p,
  .footer-section ul li {
    font-size: 10px;
    margin: 6px 0;
  }
  
  .footer-bottom {
    padding-top: 15px;
  }
  
  .footer-bottom p {
    font-size: 9px;
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
    height: 48px;
    line-height: 48px;
    font-size: 13px;
  }
  
  .drawer-menu :deep(.el-icon) {
    margin-right: 10px;
    font-size: 18px;
  }
}
</style>