<template>
  <div class="admin-dashboard">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><DataAnalysis /></el-icon>
        数据总览
      </h1>
      <p class="page-subtitle">平台运营实时数据概览</p>
    </div>

    <div class="dashboard-cards">
      <div class="stat-card" v-for="(card, index) in statCards" :key="index">
        <div class="stat-icon" :style="{ background: card.iconBg }">
          <el-icon :size="28" :style="{ color: card.iconColor }">
            <component :is="card.icon" />
          </el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-label">{{ card.label }}</span>
          <span class="stat-value" :style="{ color: card.valueColor }">
            {{ card.value }}
          </span>
        </div>
        <div class="stat-trend" :class="card.trendDir">
          <el-icon v-if="card.trendDir === 'up'"><Top /></el-icon>
          <el-icon v-else><Bottom /></el-icon>
          <span>{{ card.trend }}</span>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <el-icon><TrendCharts /></el-icon>
              平台概况
            </span>
          </div>
        </template>
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">平均用户评分</span>
            <el-rate
              :model-value="dashboardData.averageRating"
              disabled
              show-score
              score-template="{value} 分"
            />
          </div>
          <div class="info-item">
            <span class="info-label">匹配成功率</span>
            <el-progress
              :percentage="matchRate"
              :stroke-width="8"
              :color="'var(--brand)'"
            />
          </div>
          <div class="info-item">
            <span class="info-label">用户活跃度</span>
            <el-progress
              :percentage="activityRate"
              :stroke-width="8"
              :color="'var(--success)'"
              status="success"
            />
          </div>
        </div>
      </el-card>

      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <el-icon><Bell /></el-icon>
              快捷操作
            </span>
          </div>
        </template>
        <div class="quick-actions">
          <router-link to="/admin/users" class="action-item">
            <el-icon :size="20"><User /></el-icon>
            <span>用户管理</span>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </router-link>
          <router-link to="/admin/trips" class="action-item">
            <el-icon :size="20"><Van /></el-icon>
            <span>行程管理</span>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </router-link>
          <router-link to="/admin/comments" class="action-item">
            <el-icon :size="20"><ChatDotRound /></el-icon>
            <span>评论管理</span>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import {
  DataAnalysis, User, Van, ChatDotRound, TrendCharts,
  Bell, ArrowRight, Top, Bottom, UserFilled,
  Tickets, ChatLineSquare, CircleCheck
} from '@element-plus/icons-vue';

const dashboardData = ref({
  userCount: 0,
  tripCount: 0,
  commentCount: 0,
  matchedTripCount: 0,
  averageRating: 0
});

const previousDashboardData = ref({
  userCount: 0,
  tripCount: 0,
  commentCount: 0,
  matchedTripCount: 0,
  averageRating: 0
});

const matchRate = computed(() => {
  if (dashboardData.value.tripCount === 0) return 0;
  return Math.round((dashboardData.value.matchedTripCount / dashboardData.value.tripCount) * 100);
});

const activityRate = computed(() => {
  if (dashboardData.value.userCount === 0) return 0;
  return Math.min(Math.round((dashboardData.value.tripCount / dashboardData.value.userCount) * 50), 100);
});

const calculateTrend = (current: number, previous: number): { trend: string, trendDir: 'up' | 'down' | 'flat' } => {
  if (previous === 0) {
    return { trend: current > 0 ? '+100%' : '0%', trendDir: current > 0 ? 'up' : 'flat' };
  }
  
  const percentage = ((current - previous) / previous) * 100;
  if (Math.abs(percentage) < 0.1) {
    return { trend: '0%', trendDir: 'flat' };
  }
  
  const trend = percentage > 0 ? `+${percentage.toFixed(0)}%` : `${percentage.toFixed(0)}%`;
  return { trend, trendDir: percentage > 0 ? 'up' : 'down' };
};

const calculateRatingTrend = (current: number, previous: number): { trend: string, trendDir: 'up' | 'down' | 'flat' } => {
  const difference = current - previous;
  if (Math.abs(difference) < 0.1) {
    return { trend: '0.0', trendDir: 'flat' };
  }
  
  const trend = difference > 0 ? `+${difference.toFixed(1)}` : difference.toFixed(1);
  return { trend, trendDir: difference > 0 ? 'up' : 'down' };
};

const statCards = computed(() => {
  const userTrend = calculateTrend(dashboardData.value.userCount, previousDashboardData.value.userCount);
  const tripTrend = calculateTrend(dashboardData.value.tripCount, previousDashboardData.value.tripCount);
  const commentTrend = calculateTrend(dashboardData.value.commentCount, previousDashboardData.value.commentCount);
  const matchedTrend = calculateTrend(dashboardData.value.matchedTripCount, previousDashboardData.value.matchedTripCount);
  const ratingTrend = calculateRatingTrend(dashboardData.value.averageRating, previousDashboardData.value.averageRating);
  
  return [
    {
      label: '注册用户',
      value: dashboardData.value.userCount.toLocaleString(),
      icon: UserFilled,
      iconBg: 'linear-gradient(135deg, rgba(79, 110, 247, 0.12) 0%, rgba(79, 110, 247, 0.04) 100%)',
      iconColor: 'var(--brand)',
      valueColor: 'var(--brand)',
      trend: userTrend.trend,
      trendDir: userTrend.trendDir
    },
    {
      label: '行程数量',
      value: dashboardData.value.tripCount.toLocaleString(),
      icon: Van,
      iconBg: 'linear-gradient(135deg, rgba(16, 185, 129, 0.12) 0%, rgba(16, 185, 129, 0.04) 100%)',
      iconColor: 'var(--success)',
      valueColor: 'var(--success)',
      trend: tripTrend.trend,
      trendDir: tripTrend.trendDir
    },
    {
      label: '评论数量',
      value: dashboardData.value.commentCount.toLocaleString(),
      icon: ChatLineSquare,
      iconBg: 'linear-gradient(135deg, rgba(245, 158, 11, 0.12) 0%, rgba(245, 158, 11, 0.04) 100%)',
      iconColor: 'var(--warning)',
      valueColor: 'var(--warning)',
      trend: commentTrend.trend,
      trendDir: commentTrend.trendDir
    },
    {
      label: '成功匹配',
      value: dashboardData.value.matchedTripCount.toLocaleString(),
      icon: CircleCheck,
      iconBg: 'linear-gradient(135deg, rgba(168, 85, 247, 0.12) 0%, rgba(168, 85, 247, 0.04) 100%)',
      iconColor: '#a855f7',
      valueColor: '#a855f7',
      trend: matchedTrend.trend,
      trendDir: matchedTrend.trendDir
    },
    {
      label: '平均评分',
      value: dashboardData.value.averageRating.toFixed(1),
      icon: Tickets,
      iconBg: 'linear-gradient(135deg, rgba(236, 72, 153, 0.12) 0%, rgba(236, 72, 153, 0.04) 100%)',
      iconColor: '#ec4899',
      valueColor: '#ec4899',
      trend: ratingTrend.trend,
      trendDir: ratingTrend.trendDir
    }
  ];
});

onMounted(async () => {
  try {
    const response = await axios.get('/api/admin/dashboard');
    dashboardData.value = response.data;
    
    // 在获取真实数据后，模拟之前的数据用于计算趋势
    previousDashboardData.value = {
      userCount: Math.floor(dashboardData.value.userCount * 0.9),
      tripCount: Math.floor(dashboardData.value.tripCount * 0.9),
      commentCount: Math.floor(dashboardData.value.commentCount * 0.9),
      matchedTripCount: Math.floor(dashboardData.value.matchedTripCount * 0.9),
      averageRating: Math.max(0, dashboardData.value.averageRating - 0.2)
    };
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
  }
});
</script>

<style scoped>
.admin-dashboard {
  padding: var(--space-6);
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--space-8);
}

.page-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin: 0 0 var(--space-2) 0;
}

.page-title .el-icon {
  color: var(--brand);
}

.page-subtitle {
  color: var(--text-tertiary);
  font-size: var(--font-size-md);
  margin: 0;
  padding-left: calc(var(--space-3) + 28px);
}

.dashboard-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--space-5);
  margin-bottom: var(--space-8);
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-5) var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  transition: var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--brand-gradient);
  opacity: 0;
  transition: var(--transition-fast);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-card-hover);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-weight-medium);
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  line-height: 1.2;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  margin-top: var(--space-1);
}

.stat-trend.up {
  color: var(--success);
}

.stat-trend.down {
  color: var(--danger);
}

.stat-trend.flat {
  color: var(--text-tertiary);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-6);
}

.info-card {
  border: none;
  box-shadow: var(--shadow-card);
  border-radius: var(--radius-xl);
}

.info-card :deep(.el-card__header) {
  border-bottom: 1px solid var(--border-color);
  padding: var(--space-4) var(--space-6);
}

.info-card :deep(.el-card__body) {
  padding: var(--space-6);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.card-title .el-icon {
  color: var(--brand);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.info-label {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-weight-medium);
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.action-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: var(--transition-fast);
  background: var(--bg-section);
  border: 1px solid transparent;
}

.action-item:hover {
  background: rgba(79, 110, 247, 0.06);
  border-color: rgba(79, 110, 247, 0.12);
  color: var(--brand);
}

.action-item .el-icon:first-child {
  color: var(--brand);
}

.action-arrow {
  margin-left: auto;
  color: var(--text-tertiary);
  transition: var(--transition-fast);
}

.action-item:hover .action-arrow {
  color: var(--brand);
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding: var(--space-3);
  }

  .dashboard-cards {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: var(--space-3);
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: var(--space-4);
  }

  .page-subtitle {
    padding-left: 0;
  }
}
</style>
