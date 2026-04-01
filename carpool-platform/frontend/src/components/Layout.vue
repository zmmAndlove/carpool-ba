<template>
  <div class="layout-container">
    <!-- 导航栏 -->
    <el-header class="home-header" :class="{ 'header-scrolled': isScrolled }">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <div class="logo-icon">
            <el-icon size="22" color="#FFFFFF"><Van /></el-icon>
          </div>
          <span class="logo-text">长途拼车</span>
        </div>
        
        <!-- 移动端汉堡菜单 -->
        <div class="mobile-menu-button" @click="drawerVisible = true">
          <el-icon size="22"><Menu /></el-icon>
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
                <el-avatar :src="authStore.user?.avatar" :size="34">
                  {{ authStore.user?.realName?.charAt(0) || '用户' }}
                </el-avatar>
                <span class="user-name">{{ authStore.user?.realName || authStore.user?.username }}</span>
                <el-icon size="14"><ArrowDown /></el-icon>
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
            <el-button @click="$router.push('/login')" class="btn-login">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    
    <!-- 移动端侧边导航 -->
    <el-drawer
      v-model="drawerVisible"
      direction="rtl"
      size="75%"
      class="mobile-drawer"
    >
      <div class="drawer-content">
        <div class="drawer-header">
          <div class="logo">
            <div class="logo-icon">
              <el-icon size="20" color="#FFFFFF"><Van /></el-icon>
            </div>
            <span class="logo-text">长途拼车</span>
          </div>
          <el-button
            type="text"
            @click="drawerVisible = false"
            class="close-button"
          >
            <el-icon size="22"><Close /></el-icon>
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
    <footer class="home-footer">
      <div class="footer-content">
        <div class="footer-brand">
          <div class="footer-logo">
            <div class="logo-icon small">
              <el-icon size="18" color="#FFFFFF"><Van /></el-icon>
            </div>
            <span class="footer-logo-text">长途拼车</span>
          </div>
          <p class="footer-desc">专注节假日返乡拼车，让回家更简单，让旅途更温暖</p>
        </div>
        
        <div class="footer-section">
          <h4>平台特色</h4>
          <ul>
            <li>快速匹配</li>
            <li>隐私保护</li>
            <li>直接沟通</li>
            <li>自主决策</li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4>联系我们</h4>
          <ul>
            <li>客服电话: 400-123-4567</li>
            <li>邮箱: support@carpool.com</li>
            <li>工作时间: 9:00-21:00</li>
          </ul>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2026 长途拼车平台 版权所有 | 京ICP备12345678号</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
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

const drawerVisible = ref(false)
const isScrolled = ref(false)

const activeMenu = computed(() => route.path)

const handleMenuSelect = (index: string) => {
  router.push(index)
}

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

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
  drawerVisible.value = false
}

// 滚动监听
const handleScroll = () => {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-body);
}

/* ===== 导航栏 ===== */
.home-header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid transparent;
  padding: 0 var(--space-6);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 64px;
  transition: all var(--transition-normal);
}

.home-header.header-scrolled {
  background: rgba(255, 255, 255, 0.95);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  cursor: pointer;
  flex-shrink: 0;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: var(--brand-gradient);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon.small {
  width: 28px;
  height: 28px;
}

.logo-text {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  letter-spacing: -0.5px;
}

/* 移动端菜单按钮 */
.mobile-menu-button {
  display: none;
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.mobile-menu-button:hover {
  background: var(--bg-section);
  color: var(--text-primary);
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  display: flex;
  justify-content: center;
  min-width: 360px;
}

::deep(.el-menu) {
  width: 100%;
  min-width: 320px;
  display: flex;
  justify-content: center;
  background: transparent;
}

::deep(.el-menu--horizontal) {
  border-bottom: none;
}

::deep(.el-menu--horizontal > .el-menu-item) {
  height: 40px;
  line-height: 40px;
  margin: 0 4px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  background: transparent;
  transition: all var(--transition-fast);
}

::deep(.el-menu--horizontal > .el-menu-item:hover) {
  color: var(--brand);
  background: rgba(79, 110, 247, 0.06);
}

::deep(.el-menu--horizontal > .el-menu-item.is-active) {
  color: white;
}

/* 用户操作区 */
.user-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  padding: var(--space-1) var(--space-3) var(--space-1) var(--space-1);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.user-info:hover {
  background: var(--bg-section);
}

.user-name {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
}

.btn-login {
  color: var(--brand) !important;
  border: none !important;
  background: transparent !important;
  font-weight: var(--font-weight-medium) !important;
}

.btn-login:hover {
  background: rgba(79, 110, 247, 0.06) !important;
  transform: none !important;
  box-shadow: none !important;
}

/* 移动端侧边栏 */
.mobile-drawer :deep(.el-drawer__header) {
  display: none;
}

.drawer-content {
  height: 100%;
  padding: var(--space-5);
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.close-button {
  color: var(--text-tertiary) !important;
}

.drawer-menu {
  width: 100%;
  border: none !important;
  background: transparent !important;
}

.drawer-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  font-size: var(--font-size-base);
  margin-bottom: var(--space-1);
  border-radius: var(--radius-sm);
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  transition: all var(--transition-fast);
}

.drawer-menu :deep(.el-menu-item:hover) {
  background: var(--bg-section) !important;
  transform: none !important;
}

.drawer-menu :deep(.el-menu-item.is-active) {
  background: var(--brand) !important;
  color: white;
}

/* 主内容区 */
.layout-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-5);
  width: 100%;
  background: transparent;
}

/* ===== 页脚 ===== */
.home-footer {
  background: var(--text-primary);
  color: rgba(255, 255, 255, 0.7);
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr;
  gap: var(--space-10);
  padding: var(--space-10) var(--space-6);
}

.footer-brand {
  max-width: 320px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.footer-logo-text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: white;
}

.footer-desc {
  font-size: var(--font-size-sm);
  line-height: var(--line-height-relaxed);
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.footer-section h4 {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: white;
  margin: 0 0 var(--space-4);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: var(--space-2);
  cursor: pointer;
  transition: color var(--transition-fast);
}

.footer-section ul li:hover {
  color: white;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: var(--space-4) var(--space-6);
  text-align: center;
}

.footer-bottom p {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.3);
  margin: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .home-header {
    padding: 0 var(--space-4);
    height: 56px;
  }
  
  .header-content {
    gap: var(--space-3);
  }
  
  .mobile-menu-button {
    display: flex;
  }
  
  .nav-menu {
    display: none;
  }
  
  .logo {
    gap: var(--space-2);
  }
  
  .logo-text {
    font-size: var(--font-size-lg);
  }
  
  .user-actions {
    gap: var(--space-2);
  }
  
  .user-name {
    display: none;
  }
  
  .layout-main {
    padding: var(--space-4);
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: var(--space-8);
    padding: var(--space-8) var(--space-4);
  }
  
  .footer-brand {
    max-width: none;
  }
}

@media (max-width: 480px) {
  .home-header {
    padding: 0 var(--space-3);
    height: 52px;
  }
  
  .logo-text {
    font-size: var(--font-size-md);
  }
  
  .layout-main {
    padding: var(--space-3);
  }
  
  .drawer-content {
    padding: var(--space-4);
  }
}
</style>
