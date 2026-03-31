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
              class="hero-button pixel-button"
            >
              发布行程
            </el-button>
            <el-button
              size="large"
              @click="$router.push('/search')"
              class="hero-button secondary pixel-button"
            >
              寻找行程
            </el-button>
          </div>
        </div>
      </div>

      <!-- 快速搜索区 -->
      <div class="search-card pixel pixel-strong">
        <div class="search-header">
          <el-icon><Search /></el-icon>
          <span>快速搜索行程</span>
        </div>
        
        <el-form :model="searchForm" class="search-form">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6">
              <el-form-item>
                <el-input
                  v-model="searchForm.from"
                  placeholder="出发地"
                  :prefix-icon="Location"
                  class="pixel-input"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-form-item>
                <el-input
                  v-model="searchForm.to"
                  placeholder="目的地"
                  :prefix-icon="Location"
                  class="pixel-input"
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
      </div>

      <!-- 数据统计 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :xs="12" :sm="6">
          <div class="stat-card pixel pixel-strong">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#4CAF50">
                <Van />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.todayTrips }}</div>
                <div class="stat-label">今日行程</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="stat-card pixel pixel-strong">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#8BC34A">
                <User />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.matchRate }}%</div>
                <div class="stat-label">成功匹配</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="stat-card pixel pixel-strong">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#FF9800">
                <Star />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.averageRating.toFixed(1) }}</div>
                <div class="stat-label">用户评分</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="stat-card pixel pixel-strong">
            <div class="stat-content">
              <el-icon class="stat-icon" color="#F44336">
                <Clock />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.averageWaitTime }}分钟</div>
                <div class="stat-label">平均等待</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 热门路线 -->
      <div class="popular-routes-card pixel pixel-strong">
        <div class="routes-header">
          <span>热门路线</span>
          <el-link type="primary" @click="$router.push('/search')">查看全部</el-link>
        </div>
        
        <el-row :gutter="20">
          <el-col
            v-for="route in popularRoutes"
            :key="route.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="4"
          >
            <div
              class="route-card pixel"
              @click="handleRouteClick(route)"
            >
              <div class="route-content">
                <h3 class="route-title">{{ route.from }} → {{ route.to }}</h3>
                <el-tag type="primary" class="route-tag">
                  今日 {{ route.count }}条
                </el-tag>
                <p class="route-price">约 {{ route.price }}</p>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 安全保障 -->
      <div class="safety-card pixel pixel-strong">
        <div class="safety-header">
          <span>安全保障</span>
        </div>
        
        <el-row :gutter="40">
          <el-col
            v-for="feature in safetyFeatures"
            :key="feature.id"
            :xs="24"
            :sm="12"
            :md="6"
          >
            <div class="safety-feature pixel">
              <el-icon :size="40" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-desc">{{ feature.description }}</p>
            </div>
          </el-col>
        </el-row>
      </div>

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
    const response = await axios.get('/api/public/stats');
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
/* 主内容区样式 */
.hero-section {
  background: var(--bg0);
  background-image: 
    linear-gradient(45deg, var(--bg1) 25%, transparent 25%),
    linear-gradient(-45deg, var(--bg1) 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, var(--bg1) 75%),
    linear-gradient(-45deg, transparent 75%, var(--bg1) 75%);
  background-size: 20px 20px;
  background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
  padding: 80px 20px;
  text-align: center;
  color: var(--text-1);
  position: relative;
  min-height: 450px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 3px solid var(--border-dark);
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 0px rgba(0,0,0,0.2);
  color: var(--brand);
  font-family: 'Press Start 2P', cursive;
}

.hero-subtitle {
  font-size: 16px;
  margin-bottom: 40px;
  color: var(--text-2);
  font-family: 'Press Start 2P', cursive;
  line-height: 1.4;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.hero-button {
  padding: 14px 32px;
  font-size: 14px;
  font-weight: bold;
  font-family: 'Press Start 2P', cursive;
  transition: all 0.1s ease;
}

.hero-button.secondary {
  background: var(--pixel-bg) !important;
  color: var(--text-1) !important;
  border-color: var(--border-dark) !important;
}

/* 搜索卡片 */
.search-card {
  margin: -50px auto 40px;
  max-width: 1200px;
  position: relative;
  z-index: 2;
  padding: 20px;
  transition: all 0.1s ease;
}

.search-card:hover {
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
}

.search-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: bold;
  color: var(--text-1);
  margin-bottom: 20px;
  font-family: 'Press Start 2P', cursive;
}

.search-form {
  margin-top: 10px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 40px;
}

.stat-card {
  padding: 20px;
  transition: all 0.1s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px;
}

.stat-icon {
  font-size: 40px;
  transition: all 0.1s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-1);
  margin-bottom: 5px;
  font-family: 'Press Start 2P', cursive;
}

.stat-label {
  font-size: 12px;
  color: var(--text-2);
  font-family: 'Press Start 2P', cursive;
}

/* 热门路线 */
.popular-routes-card {
  margin-bottom: 40px;
  padding: 20px;
  transition: all 0.1s ease;
}

.popular-routes-card:hover {
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
}

.routes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  color: var(--text-1);
  margin-bottom: 20px;
  font-family: 'Press Start 2P', cursive;
}

.route-card {
  cursor: pointer;
  transition: all 0.1s ease;
  margin-bottom: 16px;
  padding: 16px;
}

.route-card:hover {
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
  background: var(--pixel-hover);
}

.route-content {
  padding: 8px;
}

.route-title {
  font-size: 14px;
  font-weight: bold;
  color: var(--text-1);
  margin-bottom: 12px;
  font-family: 'Press Start 2P', cursive;
}

.route-tag {
  margin-bottom: 12px;
  font-size: 10px;
  font-family: 'Press Start 2P', cursive;
}

.route-price {
  font-size: 12px;
  color: var(--text-2);
  margin: 0;
  font-family: 'Press Start 2P', cursive;
}

/* 安全保障 */
.safety-card {
  margin-bottom: 40px;
  padding: 20px;
  transition: all 0.1s ease;
}

.safety-card:hover {
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
}

.safety-header {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-1);
  margin-bottom: 20px;
  font-family: 'Press Start 2P', cursive;
}

.safety-feature {
  text-align: center;
  padding: 20px;
  transition: all 0.1s ease;
  margin-bottom: 16px;
}

.safety-feature:hover {
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
  background: var(--pixel-hover);
}

.safety-feature :deep(.el-icon) {
  font-size: 48px;
  transition: all 0.1s ease;
}

.safety-feature:hover :deep(.el-icon) {
  transform: scale(1.1);
}

.feature-title {
  font-size: 14px;
  font-weight: bold;
  color: var(--text-1);
  margin: 16px 0 8px;
  font-family: 'Press Start 2P', cursive;
}

.feature-desc {
  font-size: 11px;
  color: var(--text-2);
  line-height: 1.4;
  margin: 0;
  font-family: 'Press Start 2P', cursive;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-section {
    min-height: 400px;
    padding: 60px 15px;
  }
  
  .hero-title {
    font-size: 24px;
    line-height: 1.3;
  }
  
  .hero-subtitle {
    font-size: 12px;
    margin-bottom: 32px;
  }
  
  .hero-actions {
    flex-direction: column;
    gap: 12px;
    width: 100%;
  }
  
  .hero-button {
    padding: 12px 24px;
    font-size: 12px;
    width: 100%;
    max-width: 280px;
  }
  
  .search-card {
    margin: -30px 10px 30px;
    padding: 16px;
  }
  
  .search-header {
    font-size: 14px;
  }
  
  .search-form :deep(.el-form-item) {
    margin-bottom: 12px;
  }
  
  .search-form :deep(.el-input),
  .search-form :deep(.el-date-picker),
  .search-form :deep(.el-select) {
    width: 100%;
  }
  
  .stats-row {
    margin-bottom: 30px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .stat-icon {
    font-size: 36px;
  }
  
  .stat-content {
    padding: 12px 8px;
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
  
  .stat-info {
    text-align: center;
  }
  
  .popular-routes-card {
    margin-bottom: 30px;
    padding: 16px;
  }
  
  .route-card {
    margin-bottom: 12px;
    padding: 12px;
  }
  
  .route-content {
    padding: 6px;
  }
  
  .route-title {
    font-size: 12px;
  }
  
  .safety-card {
    margin-bottom: 30px;
    padding: 16px;
  }
  
  .safety-feature {
    padding: 16px 8px;
  }
  
  .safety-feature :deep(.el-icon) {
    font-size: 40px;
  }
  
  .feature-title {
    font-size: 12px;
    margin: 12px 0 6px;
  }
  
  .feature-desc {
    font-size: 10px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    min-height: 350px;
    padding: 50px 12px;
  }
  
  .hero-title {
    font-size: 20px;
    line-height: 1.4;
  }
  
  .hero-subtitle {
    font-size: 10px;
    margin-bottom: 28px;
  }
  
  .hero-button {
    padding: 10px 20px;
    font-size: 11px;
    width: 100%;
    max-width: 100%;
  }
  
  .search-card {
    margin: -25px 8px 25px;
    padding: 14px;
  }
  
  .search-header {
    font-size: 12px;
    gap: 8px;
  }
  
  .search-form :deep(.el-form-item) {
    margin-bottom: 10px;
  }
  
  .stats-row {
    margin-bottom: 25px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .stat-icon {
    font-size: 32px;
  }
  
  .stat-content {
    gap: 6px;
    padding: 8px 6px;
  }
  
  .stat-label {
    font-size: 10px;
  }
  
  .popular-routes-card {
    margin-bottom: 25px;
    padding: 14px;
  }
  
  .routes-header {
    font-size: 12px;
  }
  
  .route-card {
    margin-bottom: 10px;
    padding: 10px;
  }
  
  .route-title {
    font-size: 11px;
    margin-bottom: 8px;
  }
  
  .route-price {
    font-size: 10px;
  }
  
  .safety-card {
    margin-bottom: 25px;
    padding: 14px;
  }
  
  .safety-header {
    font-size: 12px;
  }
  
  .safety-feature {
    padding: 12px 6px;
  }
  
  .safety-feature :deep(.el-icon) {
    font-size: 36px;
  }
  
  .feature-title {
    font-size: 11px;
    margin: 10px 0 5px;
  }
  
  .feature-desc {
    font-size: 9px;
    line-height: 1.3;
  }
  
  /* 优化触摸交互 */
  .hero-button,
  .route-card,
  .safety-feature,
  .stat-card {
    cursor: pointer;
    -webkit-tap-highlight-color: transparent;
  }
  
  .hero-button:active,
  .route-card:active,
  .safety-feature:active,
  .stat-card:active {
    transform: translate(1px, 1px);
    box-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
    transition: transform 0.1s ease;
  }
}
</style>