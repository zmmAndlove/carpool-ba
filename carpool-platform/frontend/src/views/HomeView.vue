<template>
  <Layout>
    <!-- 主视觉区 -->
    <div class="hero-section">
      <div class="hero-bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
      
      <div class="hero-content">
        <div class="hero-badge">安全 · 便捷 · 同乡拼车</div>
        <h1 class="hero-title">节假日回家<br/><span class="gradient-text">不再难</span></h1>
        <p class="hero-subtitle">找到靠谱同乡，安全舒适回家，让旅途更温暖</p>
        <div class="hero-actions">
          <el-button
            type="primary"
            size="large"
            @click="$router.push('/publish')"
            round
          >
            <el-icon class="btn-icon"><Van /></el-icon>
            发布行程
          </el-button>
          <el-button
            size="large"
            @click="$router.push('/search')"
            round
          >
            <el-icon class="btn-icon"><Search /></el-icon>
            寻找行程
          </el-button>
        </div>
        <div class="hero-stats">
          <div class="hero-stat">
            <span class="hero-stat-value">{{ stats.todayTrips || 128 }}</span>
            <span class="hero-stat-label">今日行程</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat">
            <span class="hero-stat-value">{{ stats.matchRate || 95 }}%</span>
            <span class="hero-stat-label">成功匹配</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat">
            <span class="hero-stat-value">{{ stats.averageRating.toFixed(1) || '4.9' }}</span>
            <span class="hero-stat-label">用户评分</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速搜索区 -->
    <div class="search-card">
      <div class="search-card-inner">
        <el-form :model="searchForm" class="search-form">
          <el-row :gutter="16" align="middle">
            <el-col :xs="24" :sm="8" :md="7">
              <el-form-item>
                <el-input
                  v-model="searchForm.from"
                  placeholder="出发地"
                  :prefix-icon="Location"
                  size="large"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8" :md="7">
              <el-form-item>
                <el-input
                  v-model="searchForm.to"
                  placeholder="目的地"
                  :prefix-icon="Location"
                  size="large"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :xs="12" :sm="4" :md="5">
              <el-form-item>
                <el-date-picker
                  v-model="searchForm.date"
                  type="date"
                  placeholder="出发日期"
                  style="width: 100%"
                  size="large"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="12" :sm="4" :md="5">
              <el-button
                type="primary"
                size="large"
                @click="handleSearch"
                :disabled="!searchForm.from || !searchForm.to"
                style="width: 100%"
                round
              >
                搜索行程
              </el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>

    <!-- 热门路线 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">热门路线</h2>
        <el-link type="primary" @click="$router.push('/search')">查看全部</el-link>
      </div>
      <el-row :gutter="16">
        <el-col
          v-for="route in popularRoutes"
          :key="route.id"
          :xs="12"
          :sm="8"
          :md="4"
        >
          <div class="route-card" @click="handleRouteClick(route)">
            <div class="route-card-header">
              <span class="route-from">{{ route.from }}</span>
              <div class="route-arrow-wrap">
                <el-icon><Right /></el-icon>
              </div>
              <span class="route-to">{{ route.to }}</span>
            </div>
            <div class="route-card-footer">
              <el-tag type="primary" size="small" effect="plain" round>{{ route.count }}条</el-tag>
              <span class="route-price">{{ route.price }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 平台特色 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">平台特色</h2>
      </div>
      <el-row :gutter="20">
        <el-col
          v-for="feature in platformFeatures"
          :key="feature.id"
          :xs="12"
          :sm="12"
          :md="6"
        >
          <div class="feature-card">
            <div class="feature-icon" :style="{ background: feature.bgColor }">
              <el-icon :size="28" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.description }}</p>
          </div>
        </el-col>
      </el-row>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import {
  Search,
  Location,
  User,
  Star,
  Clock,
  Van,
  Timer,
  Lock,
  ChatLineRound,
  Key,
  Right
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

const platformFeatures = ref([
  {
    id: 1,
    icon: 'Timer',
    title: '快速匹配',
    description: '基于指定标准高效连接用户，快速找到合适的拼车伙伴',
    color: '#F97316',
    bgColor: '#FFF7ED'
  },
  {
    id: 2,
    icon: 'Lock',
    title: '隐私保护',
    description: '不收集或存储任何用户个人信息，保护您的隐私安全',
    color: '#3B82F6',
    bgColor: '#EFF6FF'
  },
  {
    id: 3,
    icon: 'ChatLineRound',
    title: '直接沟通',
    description: '用户间可独立联系，建立直接的沟通渠道',
    color: '#10B981',
    bgColor: '#ECFDF5'
  },
  {
    id: 4,
    icon: 'Key',
    title: '自主决策',
    description: '用户拥有完全自由，自主决定互动方式和结果',
    color: '#8B5CF6',
    bgColor: '#F5F3FF'
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
    stats.value.todayTrips = data.tripCount;
    if (data.tripCount > 0) {
      stats.value.matchRate = Math.round((data.matchedTripCount / data.tripCount) * 100);
    }
    stats.value.averageRating = data.averageRating;
    stats.value.averageWaitTime = data.averageWaitTime || 15;
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
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
/* ===== 主视觉区 ===== */
.hero-section {
  background: linear-gradient(160deg, #4F6EF7 0%, #38BDF8 50%, #6EE7B7 100%);
  padding: 80px var(--space-5) 100px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-bg-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
  background: white;
}

.shape-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.shape-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -80px;
  animation: float 10s ease-in-out infinite reverse;
}

.shape-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 700px;
  margin: 0 auto;
}

.hero-badge {
  display: inline-block;
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-full);
  color: white;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  margin-bottom: var(--space-6);
  letter-spacing: 1px;
}

.hero-title {
  font-size: 48px;
  font-weight: var(--font-weight-bold);
  color: white;
  margin: 0 0 var(--space-4);
  line-height: var(--line-height-tight);
  letter-spacing: -1px;
}

.gradient-text {
  background: linear-gradient(135deg, #FDE68A 0%, #FCA5A5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: var(--font-size-lg);
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 var(--space-8);
  line-height: var(--line-height-relaxed);
}

.hero-actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: var(--space-10);
}

.hero-actions .el-button {
  padding: 12px 32px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
}

.hero-actions .el-button--primary {
  background: white !important;
  color: var(--brand) !important;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.15) !important;
}

.hero-actions .el-button--primary:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2) !important;
}

.hero-actions .el-button--default {
  background: rgba(255, 255, 255, 0.15) !important;
  color: white !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  backdrop-filter: blur(10px);
}

.hero-actions .el-button--default:hover {
  background: rgba(255, 255, 255, 0.25) !important;
  color: white !important;
  transform: translateY(-2px) !important;
}

.btn-icon {
  margin-right: 6px;
}

.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-6);
}

.hero-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.hero-stat-value {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: white;
  line-height: 1;
}

.hero-stat-label {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.7);
  margin-top: 4px;
}

.hero-stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
}

/* ===== 搜索卡片 ===== */
.search-card {
  margin: -40px auto var(--space-8);
  max-width: 1100px;
  position: relative;
  z-index: 2;
  padding: 0 var(--space-5);
}

.search-card-inner {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  padding: var(--space-6);
  border: 1px solid var(--border-color);
}

/* ===== 通用 Section ===== */
.section {
  max-width: 1100px;
  margin: 0 auto var(--space-10);
  padding: 0 var(--space-5);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.section-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0;
}

/* ===== 热门路线卡片 ===== */
.route-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  cursor: pointer;
  transition: all var(--transition-normal);
  margin-bottom: var(--space-4);
}

.route-card:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-3px);
  border-color: var(--brand);
}

.route-card-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-3);
}

.route-from,
.route-to {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  font-size: var(--font-size-base);
}

.route-arrow-wrap {
  color: var(--brand);
  flex-shrink: 0;
}

.route-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.route-price {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-weight-medium);
}

/* ===== 特色卡片 ===== */
.feature-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-6) var(--space-5);
  text-align: center;
  transition: all var(--transition-normal);
  margin-bottom: var(--space-4);
}

.feature-card:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-3px);
}

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-4);
}

.feature-title {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
}

.feature-desc {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  line-height: var(--line-height-relaxed);
  margin: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .hero-section {
    padding: 60px var(--space-4) 80px;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .hero-subtitle {
    font-size: var(--font-size-md);
    margin-bottom: var(--space-6);
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
    gap: var(--space-3);
  }
  
  .hero-actions .el-button {
    width: 100%;
    max-width: 280px;
  }
  
  .hero-stats {
    gap: var(--space-4);
  }
  
  .hero-stat-value {
    font-size: var(--font-size-2xl);
  }
  
  .search-card {
    margin: -30px auto var(--space-6);
    padding: 0 var(--space-3);
  }
  
  .search-card-inner {
    padding: var(--space-4);
  }
  
  .section {
    margin-bottom: var(--space-6);
    padding: 0 var(--space-3);
  }
  
  .feature-card {
    padding: var(--space-4);
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 50px var(--space-3) 70px;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .hero-badge {
    font-size: var(--font-size-xs);
  }
  
  .hero-stats {
    gap: var(--space-3);
  }
  
  .hero-stat-value {
    font-size: var(--font-size-xl);
  }
  
  .hero-stat-label {
    font-size: 11px;
  }
  
  .search-card {
    margin: -25px auto var(--space-5);
    padding: 0 var(--space-2);
  }
  
  .search-card-inner {
    padding: var(--space-3);
  }
  
  .section-title {
    font-size: var(--font-size-xl);
  }
}
</style>
