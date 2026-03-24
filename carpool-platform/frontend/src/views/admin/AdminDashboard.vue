<template>
  <div class="admin-dashboard">
    <h1>管理后台</h1>
    <div class="dashboard-cards">
      <div class="card">
        <h2>注册用户</h2>
        <p class="number">{{ dashboardData.userCount }}</p>
      </div>
      <div class="card">
        <h2>行程数量</h2>
        <p class="number">{{ dashboardData.tripCount }}</p>
      </div>
      <div class="card">
        <h2>评论数量</h2>
        <p class="number">{{ dashboardData.commentCount }}</p>
      </div>
      <div class="card">
        <h2>成功匹配</h2>
        <p class="number">{{ dashboardData.matchedTripCount }}</p>
      </div>
      <div class="card">
        <h2>用户评分</h2>
        <p class="number">{{ dashboardData.averageRating.toFixed(1) }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

const dashboardData = ref({
  userCount: 0,
  tripCount: 0,
  commentCount: 0,
  matchedTripCount: 0,
  averageRating: 0
});

onMounted(async () => {
  try {
    const response = await axios.get('/api/admin/dashboard');
    dashboardData.value = response.data;
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
  }
});
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.dashboard-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.card h2 {
  font-size: 18px;
  color: #6c757d;
  margin-bottom: 10px;
}

.card .number {
  font-size: 36px;
  font-weight: bold;
  color: #007bff;
  margin: 0;
}
</style>
