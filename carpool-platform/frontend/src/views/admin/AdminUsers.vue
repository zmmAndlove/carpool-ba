<template>
  <div class="admin-users">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><User /></el-icon>
        用户管理
      </h1>
      <p class="page-subtitle">共 {{ total }} 位注册用户</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <el-table
        :data="users"
        stripe
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: 'var(--bg-section)', color: 'var(--text-secondary)', fontWeight: '600', fontSize: '13px' }"
        :row-style="{ transition: 'all 0.2s ease' }"
      >
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" class="user-avatar">
                {{ row.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="user-name">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === '男' ? '' : 'danger'" size="small" round>
              {{ row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="70" align="center" />
        <el-table-column prop="rating" label="评分" width="120" align="center">
          <template #default="{ row }">
            <div class="rating-cell">
              <el-rate :model-value="row.rating || 0" disabled size="small" />
              <span class="rating-num">{{ row.rating?.toFixed(1) || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="tripCount" label="行程数" width="90" align="center">
          <template #default="{ row }">
            <span class="badge-num">{{ row.tripCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="completedTrips" label="完成行程" width="100" align="center">
          <template #default="{ row }">
            <span class="badge-num success">{{ row.completedTrips }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" min-width="160">
          <template #default="{ row }">
            <span class="time-text">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev, pager, next"
        background
        @current-change="fetchUsers"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { User } from '@element-plus/icons-vue';

const users = ref<any[]>([]);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const loading = ref(false);

const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`/api/admin/users?page=${page.value}&size=${size.value}`);
    users.value = response.data.users;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取用户列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.admin-users {
  padding: var(--space-6);
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--space-6);
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
  padding-left: calc(var(--space-3) + 24px);
}

.table-card {
  border: none;
  box-shadow: var(--shadow-card);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-6);
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.user-avatar {
  background: linear-gradient(135deg, var(--brand) 0%, var(--brand-light) 100%);
  color: white;
  font-weight: var(--font-weight-bold);
  font-size: var(--font-size-sm);
  flex-shrink: 0;
}

.user-name {
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.rating-cell {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.rating-num {
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-weight-medium);
}

.badge-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  height: 28px;
  padding: 0 var(--space-2);
  border-radius: var(--radius-lg);
  background: rgba(79, 110, 247, 0.08);
  color: var(--brand);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}

.badge-num.success {
  background: rgba(16, 185, 129, 0.08);
  color: var(--success);
}

.time-text {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
}

::deep(.el-pagination.is-background .el-pager li) {
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
}

::deep(.el-pagination.is-background .btn-prev),
::deep(.el-pagination.is-background .btn-next) {
  border-radius: var(--radius-md);
}

::deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: var(--bg-section);
}

::deep(.el-table) {
  --el-table-border-color: var(--border-color);
  --el-table-header-bg-color: var(--bg-section);
  border-radius: var(--radius-xl);
}

@media (max-width: 768px) {
  .admin-users {
    padding: var(--space-3);
  }

  .page-subtitle {
    padding-left: 0;
  }
}
</style>
