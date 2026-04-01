<template>
  <div class="user-center">
    <!-- 用户资料卡片 -->
    <div class="profile-banner">
      <div class="profile-banner-bg"></div>
      <div class="profile-card">
        <el-avatar :size="88" class="profile-avatar">
          {{ user?.username?.charAt(0) || 'U' }}
        </el-avatar>
        <div class="profile-info">
          <h2 class="profile-name">{{ user?.username || '未登录' }}</h2>
          <p class="profile-phone">
            <el-icon><Iphone /></el-icon>
            {{ user?.phone || '暂无手机号' }}
          </p>
          <div class="profile-stats">
            <div class="profile-stat">
              <span class="stat-value">{{ trips.length }}</span>
              <span class="stat-label">行程</span>
            </div>
            <div class="stat-divider"></div>
            <div class="profile-stat">
              <span class="stat-value">0</span>
              <span class="stat-label">收藏</span>
            </div>
            <div class="stat-divider"></div>
            <div class="profile-stat">
              <span class="stat-value">4.8</span>
              <span class="stat-label">评分</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 标签页内容 -->
    <div class="content-card">
      <el-tabs v-model="activeTab" class="user-tabs">
        <el-tab-pane label="我的行程" name="trips">
          <div class="trip-list">
            <el-empty v-if="trips.length === 0" description="暂无行程记录">
              <el-button type="primary" @click="$router.push('/publish')">发布行程</el-button>
            </el-empty>
            <div v-for="trip in trips" :key="trip.id" class="trip-card">
              <div class="trip-card-header">
                <div class="trip-card-title">
                  <el-icon class="trip-icon" :color="trip.type === 'CAR' ? 'var(--success)' : 'var(--brand)'">
                    <Van v-if="trip.type === 'CAR'" />
                    <User v-else />
                  </el-icon>
                  <span>{{ trip.title }}</span>
                </div>
                <el-tag :type="trip.type === 'CAR' ? 'success' : 'primary'" round>
                  {{ trip.type === 'CAR' ? '车找人' : '人找车' }}
                </el-tag>
              </div>
              <div class="trip-card-body">
                <div class="trip-route">
                  <div class="trip-point">
                    <div class="point-dot departure"></div>
                    <span>{{ trip.startPlace }}</span>
                  </div>
                  <div class="trip-arrow">
                    <el-icon><Right /></el-icon>
                  </div>
                  <div class="trip-point">
                    <div class="point-dot destination"></div>
                    <span>{{ trip.endPlace }}</span>
                  </div>
                </div>
                <div class="trip-meta">
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>
                    {{ trip.departureTime }}
                  </span>
                  <span class="meta-item price">
                    <el-icon><Money /></el-icon>
                    ¥{{ trip.price }}
                  </span>
                </div>
              </div>
              <div class="trip-card-actions">
                <el-button type="primary" text size="small">查看详情</el-button>
                <el-button text size="small">编辑</el-button>
                <el-button type="danger" text size="small">删除</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我的收藏" name="favorites">
          <el-empty description="暂无收藏">
            <template #description>
              <p style="color: var(--text-tertiary);">收藏感兴趣的行程，方便以后查看</p>
            </template>
          </el-empty>
        </el-tab-pane>

        <el-tab-pane label="设置" name="settings">
          <div class="settings-section">
            <h3 class="settings-title">基本信息</h3>
            <el-form :model="userForm" label-position="top" class="settings-form">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" placeholder="请输入用户名" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone" disabled>
                  <template #append>
                    <el-button>更换</el-button>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="修改密码">
                <el-input type="password" placeholder="不修改请留空" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSettings" class="save-btn">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 退出登录 -->
    <div class="logout-section">
      <el-button type="danger" plain @click="logout" class="logout-btn">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Timer, Money, Van, User, Clock, Right, Iphone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const activeTab = ref('trips')

const user = ref({
  username: '测试用户',
  phone: '13800138001'
})

const userForm = ref({
  username: user.value.username,
  phone: user.value.phone
})

const trips = ref([
  {
    id: 1,
    title: '北京到上海',
    type: 'CAR',
    startPlace: '北京市朝阳区',
    endPlace: '上海市浦东新区',
    departureTime: '2024-01-20 08:00',
    price: 300
  },
  {
    id: 2,
    title: '上海到杭州',
    type: 'PERSON',
    startPlace: '上海市浦东新区',
    endPlace: '杭州市西湖区',
    departureTime: '2024-01-21 10:00',
    price: 100
  }
])

const saveSettings = () => {
  ElMessage.success('设置保存成功')
}

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.user-center {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-6);
}

/* 资料横幅 */
.profile-banner {
  position: relative;
  margin-bottom: var(--space-5);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.profile-banner-bg {
  height: 120px;
  background: var(--brand-gradient);
  position: relative;
}

.profile-banner-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.08'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.profile-card {
  display: flex;
  align-items: flex-end;
  gap: var(--space-5);
  padding: 0 var(--space-5) var(--space-5);
  margin-top: -44px;
  position: relative;
  z-index: 1;
}

.profile-avatar {
  border: 4px solid var(--bg-card);
  box-shadow: var(--shadow-md);
  background: var(--brand-gradient);
  color: white;
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  flex-shrink: 0;
}

.profile-info {
  flex: 1;
  padding-bottom: var(--space-1);
}

.profile-name {
  margin: 0 0 var(--space-1);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.profile-phone {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  margin: 0 0 var(--space-4);
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

.profile-stats {
  display: flex;
  align-items: center;
  gap: var(--space-5);
}

.profile-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-stat .stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.profile-stat .stat-label {
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}

.stat-divider {
  width: 1px;
  height: 28px;
  background: var(--border-color);
}

/* 内容区域 */
.content-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  padding: var(--space-5);
  border: 1px solid var(--border-color);
}

.user-tabs :deep(.el-tabs__header) {
  margin-bottom: var(--space-5);
}

.user-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background: var(--border-color);
}

.user-tabs :deep(.el-tabs__item) {
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  font-size: var(--font-size-md);
}

.user-tabs :deep(.el-tabs__item.is-active) {
  color: var(--brand);
  font-weight: var(--font-weight-semibold);
}

.user-tabs :deep(.el-tabs__active-bar) {
  background: var(--brand-gradient);
}

/* 行程卡片 */
.trip-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.trip-card {
  padding: var(--space-4) var(--space-5);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  transition: var(--transition-fast);
}

.trip-card:hover {
  box-shadow: var(--shadow-card-hover);
  border-color: transparent;
}

.trip-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.trip-card-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.trip-icon {
  font-size: 18px;
}

.trip-card-body {
  padding: var(--space-3) 0;
}

.trip-route {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
}

.trip-point {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
}

.point-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.point-dot.departure {
  background: var(--brand);
}

.point-dot.destination {
  background: var(--success);
}

.trip-arrow {
  color: var(--text-tertiary);
  font-size: 12px;
}

.trip-meta {
  display: flex;
  gap: var(--space-4);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}

.meta-item.price {
  color: var(--danger);
  font-weight: var(--font-weight-semibold);
}

.trip-card-actions {
  display: flex;
  gap: var(--space-2);
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-light);
}

/* 设置 */
.settings-section {
  max-width: 480px;
}

.settings-title {
  margin: 0 0 var(--space-5);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  padding-left: 14px;
  position: relative;
}

.settings-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: var(--brand-gradient);
  border-radius: 2px;
}

.settings-form :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
}

.save-btn {
  min-width: 140px;
  padding: 10px 24px;
  border-radius: var(--radius-md);
}

/* 退出按钮 */
.logout-section {
  margin-top: var(--space-8);
  text-align: center;
}

.logout-btn {
  border-radius: var(--radius-md);
  padding: 10px 32px;
}

@media (max-width: 768px) {
  .user-center {
    padding: var(--space-4);
  }

  .profile-banner-bg {
    height: 100px;
  }

  .profile-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 0 var(--space-4) var(--space-4);
  }

  .profile-phone {
    justify-content: center;
  }

  .profile-stats {
    justify-content: center;
  }

  .content-card {
    padding: var(--space-4);
  }

  .trip-meta {
    flex-direction: column;
    gap: var(--space-2);
  }
}
</style>
