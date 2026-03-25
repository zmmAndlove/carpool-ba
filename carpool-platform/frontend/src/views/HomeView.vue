<template>
  <Layout>
    <!-- 主内容区 -->
      <!-- 主视觉区 -->
      <div class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">安全 · 便捷 · 同乡拼车</h1>
          <p class="hero-subtitle">节假日回家不再难，找到靠谱同乡，安全舒适回家</p>
          <div class="hero-actions">
            <el-button
              type="primary"
              size="large"
              @click="$router.push('/publish')"
              class="hero-button"
            >
              发布行程
            </el-button>
            <el-button
              size="large"
              @click="$router.push('/search')"
              class="hero-button secondary"
            >
              寻找行程
            </el-button>
          </div>
        </div>
      </div>

      <!-- 快速搜索区 -->
      <el-card class="search-card" shadow="hover">
        <template #header>
          <div class="search-header">
            <el-icon><Search /></el-icon>
            <span>快速搜索行程</span>
          </div>
        </template>
        
        <el-form :model="searchForm" class="search-form">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6">
              <el-form-item>
                <el-input
                  v-model="searchForm.from"
                  placeholder="出发地"
                  :prefix-icon="Location"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-form-item>
                <el-input
                  v-model="searchForm.to"
                  placeholder="目的地"
                  :prefix-icon="Location"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="4">
              <el-form-item>
                <el-date-picker
                  v-model="searchForm.date"
                  type="date"
                  placeholder="出发日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="4">
              <el-form-item>
                <el-select
                  v-model="searchForm.passengers"
                  placeholder="人数"
                  style="width: 100%"
                >
                  <el-option label="1人" :value="1" />
                  <el-option label="2人" :value="2" />
                  <el-option label="3人" :value="3" />
                  <el-option label="4人" :value="4" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="4">
              <el-button
                type="primary"
                :icon="Search"
                @click="handleSearch"
                :disabled="!searchForm.from || !searchForm.to"
                style="width: 100%"
              >
                搜索行程
              </el-button>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 数据统计 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#007AFF">
                <Van />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.todayTrips }}</div>
                <div class="stat-label">今日行程</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#34C759">
                <User />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.matchRate }}%</div>
                <div class="stat-label">成功匹配</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#FF9500">
                <Star />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.averageRating.toFixed(1) }}</div>
                <div class="stat-label">用户评分</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#FF3B30">
                <Clock />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.averageWaitTime }}分钟</div>
                <div class="stat-label">平均等待</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 热门路线 -->
      <el-card class="popular-routes-card" shadow="hover">
        <template #header>
          <div class="routes-header">
            <span>热门路线</span>
            <el-link type="primary" @click="$router.push('/search')">查看全部</el-link>
          </div>
        </template>
        
        <el-row :gutter="20">
          <el-col
            v-for="route in popularRoutes"
            :key="route.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="4"
          >
            <el-card
              class="route-card"
              shadow="hover"
              @click="handleRouteClick(route)"
            >
              <div class="route-content">
                <h3 class="route-title">{{ route.from }} → {{ route.to }}</h3>
                <el-tag type="primary" class="route-tag">
                  今日 {{ route.count }}条
                </el-tag>
                <p class="route-price">约 {{ route.price }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>

      <!-- 安全保障 -->
      <el-card class="safety-card" shadow="hover">
        <template #header>
          <span>安全保障</span>
        </template>
        
        <el-row :gutter="40">
          <el-col
            v-for="feature in safetyFeatures"
            :key="feature.id"
            :xs="24"
            :sm="12"
            :md="6"
          >
            <div class="safety-feature">
              <el-icon :size="40" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-desc">{{ feature.description }}</p>
            </div>
          </el-col>
        </el-row>
      </el-card>

  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import {
  Search,
  Location,
  User,
  Star,
  Clock,
  Check,
  UserFilled,
  StarFilled,
  Lock,
  Van
} from '@element-plus/icons-vue'
import Layout from '@/components/Layout.vue'

const router = useRouter()

const searchForm = ref({
  from: '',
  to: '',
  date: null,
  passengers: 1
})

const popularRoutes = ref([
  { id: 1, from: '北京', to: '石家庄', count: 45, price: '100-150元' },
  { id: 2, from: '上海', to: '南京', count: 32, price: '80-120元' },
  { id: 3, from: '广州', to: '深圳', count: 28, price: '60-100元' },
  { id: 4, from: '成都', to: '重庆', count: 24, price: '90-130元' },
  { id: 5, from: '武汉', to: '长沙', count: 18, price: '70-110元' }
])

const safetyFeatures = ref([
    {
    id: 1,
    icon: 'UserFilled',
    title: '实名认证',
    description: '所有用户均需实名认证，确保身份真实',
    color: '#34C759'
  },
  {
    id: 2,
    icon: 'User',
    title: '同乡匹配',
    description: '优先匹配同乡，增加信任感',
    color: '#007AFF'
  },
  {
    id: 3,
    icon: 'Van',
    title: '行程分享',
    description: '实时位置分享给家人，出行更安心',
    color: '#FF9500'
  },
  {
    id: 4,
    icon: 'StarFilled',
    title: '评价系统',
    description: '双向评价，建立诚信体系',
    color: '#FF3B30'
  }
])

const stats = ref({
  todayTrips: 0,
  matchRate: 0,
  averageRating: 0,
  averageWaitTime: 0
})

const fetchStats = async () => {
  try {
    const response = await axios.get('/api/admin/dashboard');
    const data = response.data;
    
    // 计算今日行程数量（假设从所有行程中过滤出今天的）
    stats.value.todayTrips = data.tripCount;
    
    // 计算成功匹配率
    if (data.tripCount > 0) {
      stats.value.matchRate = Math.round((data.matchedTripCount / data.tripCount) * 100);
    }
    
    // 用户平均评分
    stats.value.averageRating = data.averageRating;
    
    // 平均等待时间
    stats.value.averageWaitTime = data.averageWaitTime || 15;
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
}

const handleMenuSelect = (index: string) => {
  router.push(index)
}

const handleSearch = () => {
  if (!searchForm.value.from || !searchForm.value.to) {
    ElMessage.warning('请填写出发地和目的地')
    return
  }
  
  const query: any = {
    from: searchForm.value.from,
    to: searchForm.value.to
  }
  
  if (searchForm.value.date) {
    query.date = (searchForm.value.date as Date).toISOString().split('T')[0]
  }
  
  if (searchForm.value.passengers > 1) {
    query.passengers = searchForm.value.passengers
  }
  
  router.push({
    path: '/search',
    query
  })
}

const handleRouteClick = (route: any) => {
  router.push({
    path: '/search',
    query: {
      from: route.from,
      to: route.to
    }
  })
}

onMounted(() => {
  fetchStats();
});
</script>

<style scoped>
.home-main {
  width: 100%;
}

/* 主内容区样式 */
.hero-section {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 80px 20px;
  text-align: center;
  color: white;
  position: relative;
  overflow: hidden;
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: 
    radial-gradient(circle at 20% 80%, rgba(0, 122, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(52, 199, 89, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(255, 149, 0, 0.1) 0%, transparent 50%);
  animation: float 15s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(30px, -30px) rotate(120deg); }
  66% { transform: translate(-20px, 20px) rotate(240deg); }
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 56px;
  font-weight: 800;
  margin-bottom: 20px;
  text-shadow: 0 4px 20px rgba(0,0,0,0.3);
  letter-spacing: -1px;
  background: linear-gradient(135deg, #ffffff 0%, #e0e7ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 0.5px;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.hero-button {
  padding: 18px 48px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 12px;
  box-shadow: 
    0 8px 32px rgba(0, 122, 255, 0.4),
    0 4px 16px rgba(0, 0, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
}

.hero-button:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 
    0 12px 40px rgba(0, 122, 255, 0.5),
    0 6px 20px rgba(0, 0, 0, 0.3);
}

.hero-button.secondary {
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
}

.hero-button.secondary:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
}

/* 搜索卡片 */
.search-card {
  margin: -50px auto 40px;
  max-width: 1200px;
  position: relative;
  z-index: 2;
  border-radius: 20px;
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}

.search-form {
  margin-top: 20px;
}

/* 3D图标样式 */
:deep(.el-icon) {
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-icon:hover) {
  transform: translateY(-2px) scale(1.1);
  filter: drop-shadow(0 8px 16px rgba(0, 0, 0, 0.25));
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 40px;
}

.stat-card {
  border-radius: 20px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  backdrop-filter: blur(16px);
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.stat-card:hover {
  transform: translateY(-8px) rotateX(5deg);
  box-shadow: 
    0 12px 32px rgba(0, 0, 0, 0.15),
    0 6px 16px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.9);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
}

.stat-icon {
  font-size: 48px;
  filter: 
    drop-shadow(0 8px 16px rgba(0, 0, 0, 0.2)),
    drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover .stat-icon {
  transform: scale(1.15) translateY(-4px);
  filter: 
    drop-shadow(0 12px 24px rgba(0, 0, 0, 0.3)),
    drop-shadow(0 6px 12px rgba(0, 0, 0, 0.15));
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: #1a1a2e;
  margin-bottom: 5px;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 热门路线 */
.popular-routes-card {
  margin-bottom: 40px;
  border-radius: 20px;
  backdrop-filter: blur(16px);
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.routes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}

.route-card {
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.6);
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.06),
    0 1px 4px rgba(0, 0, 0, 0.03);
}

.route-card:hover {
  transform: translateY(-6px) rotateY(3deg);
  border-color: rgba(0, 122, 255, 0.3);
  box-shadow: 
    0 12px 32px rgba(0, 122, 255, 0.2),
    0 6px 16px rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.9);
}

.route-content {
  padding: 24px;
}

.route-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
  letter-spacing: -0.3px;
}

.route-tag {
  margin-bottom: 12px;
  backdrop-filter: blur(8px);
}

.route-price {
  font-size: 15px;
  color: #666;
  margin: 0;
  font-weight: 500;
}

/* 安全保障 */
.safety-card {
  margin-bottom: 40px;
  border-radius: 20px;
  backdrop-filter: blur(16px);
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.safety-feature {
  text-align: center;
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
}

.safety-feature:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.8);
}

.safety-feature :deep(.el-icon) {
  font-size: 56px;
  filter: 
    drop-shadow(0 8px 20px rgba(0, 0, 0, 0.25)),
    drop-shadow(0 4px 10px rgba(0, 0, 0, 0.12));
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.safety-feature:hover :deep(.el-icon) {
  transform: scale(1.2) translateY(-6px);
  filter: 
    drop-shadow(0 12px 28px rgba(0, 0, 0, 0.35)),
    drop-shadow(0 6px 14px rgba(0, 0, 0, 0.18));
}

.feature-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 20px 0 12px;
  letter-spacing: -0.3px;
}

.feature-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.7;
  margin: 0;
  font-weight: 400;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-section {
    min-height: 400px;
    padding: 60px 15px;
  }
  
  .hero-title {
    font-size: 32px;
    letter-spacing: -0.5px;
  }
  
  .hero-subtitle {
    font-size: 16px;
    margin-bottom: 32px;
  }
  
  .hero-actions {
    flex-direction: column;
    gap: 12px;
    width: 100%;
  }
  
  .hero-button {
    padding: 14px 32px;
    font-size: 16px;
    width: 100%;
    max-width: 280px;
  }
  
  .search-card {
    margin: -30px 10px 30px;
    border-radius: 16px;
  }
  
  .search-header {
    font-size: 16px;
  }
  
  .stats-row {
    margin-bottom: 30px;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .stat-icon {
    font-size: 40px;
  }
  
  .stat-content {
    padding: 8px;
  }
  
  .popular-routes-card {
    margin-bottom: 30px;
    border-radius: 16px;
  }
  
  .route-card {
    margin-bottom: 12px;
    border-radius: 12px;
  }
  
  .route-content {
    padding: 16px;
  }
  
  .route-title {
    font-size: 16px;
  }
  
  .safety-card {
    margin-bottom: 30px;
    border-radius: 16px;
  }
  
  .safety-feature {
    padding: 20px 12px;
  }
  
  .safety-feature :deep(.el-icon) {
    font-size: 48px;
  }
  
  .feature-title {
    font-size: 16px;
    margin: 16px 0 10px;
  }
  
  .feature-desc {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    min-height: 350px;
    padding: 50px 12px;
  }
  
  .hero-title {
    font-size: 26px;
    line-height: 1.3;
  }
  
  .hero-subtitle {
    font-size: 14px;
    margin-bottom: 28px;
  }
  
  .hero-button {
    padding: 12px 24px;
    font-size: 15px;
    width: 100%;
    max-width: 100%;
  }
  
  .search-card {
    margin: -25px 8px 25px;
    border-radius: 14px;
  }
  
  .search-header {
    font-size: 15px;
    gap: 8px;
  }
  
  .search-form :deep(.el-form-item) {
    margin-bottom: 12px;
  }
  
  .stats-row {
    margin-bottom: 25px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .stat-icon {
    font-size: 36px;
  }
  
  .stat-content {
    gap: 12px;
    padding: 6px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .popular-routes-card {
    margin-bottom: 25px;
    border-radius: 14px;
  }
  
  .route-card {
    margin-bottom: 10px;
    border-radius: 10px;
  }
  
  .route-content {
    padding: 14px;
  }
  
  .route-title {
    font-size: 15px;
    margin-bottom: 10px;
  }
  
  .route-price {
    font-size: 13px;
  }
  
  .safety-card {
    margin-bottom: 25px;
    border-radius: 14px;
  }
  
  .safety-feature {
    padding: 16px 8px;
  }
  
  .safety-feature :deep(.el-icon) {
    font-size: 40px;
  }
  
  .feature-title {
    font-size: 15px;
    margin: 14px 0 8px;
  }
  
  .feature-desc {
    font-size: 12px;
    line-height: 1.5;
  }
}
</style>