<template>
  <div class="admin-trips">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><Van /></el-icon>
        行程管理
      </h1>
      <p class="page-subtitle">共 {{ total }} 条行程记录</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <el-table
        :data="trips"
        stripe
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: 'var(--bg-section)', color: 'var(--text-secondary)', fontWeight: '600', fontSize: '13px' }"
      >
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column prop="tripType" label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.tripType === 'DRIVER' ? 'success' : 'primary'" size="small" round>
              {{ row.tripType === 'DRIVER' ? '车主' : '乘客' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="路线" min-width="200">
          <template #default="{ row }">
            <div class="route-cell">
              <div class="route-from">
                <span class="dot green"></span>
                {{ row.departureProvince }} {{ row.departureCity }}
              </div>
              <el-icon class="route-arrow"><ArrowRight /></el-icon>
              <div class="route-to">
                <span class="dot red"></span>
                {{ row.destinationProvince }} {{ row.destinationCity }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="departureTime" label="出发时间" min-width="160">
          <template #default="{ row }">
            <span class="time-text">{{ formatDate(row.departureTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="座位" width="80" align="center">
          <template #default="{ row }">
            <span class="seat-info">
              <span class="seat-available">{{ row.seatsAvailable }}</span>
              <span class="seat-sep">/</span>
              <span class="seat-total">{{ row.seatsTotal }}</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="pricePerPerson" label="价格" width="100" align="center">
          <template #default="{ row }">
            <span class="price-text">&yen;{{ row.pricePerPerson }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small" round>
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="160">
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
        @current-change="fetchTrips"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { Van, ArrowRight } from '@element-plus/icons-vue';

const trips = ref<any[]>([]);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const loading = ref(false);

const statusMap: Record<string, { label: string; type: string }> = {
  ACTIVE: { label: '进行中', type: 'success' },
  COMPLETED: { label: '已完成', type: 'info' },
  CANCELLED: { label: '已取消', type: 'danger' },
  EXPIRED: { label: '已过期', type: 'warning' }
};

const statusLabel = (status: string) => statusMap[status]?.label || status;
const statusType = (status: string) => (statusMap[status]?.type || 'info') as any;

const fetchTrips = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`/api/admin/trips?page=${page.value}&size=${size.value}`);
    trips.value = response.data.trips;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取行程列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

onMounted(() => {
  fetchTrips();
});
</script>

<style scoped>
.admin-trips {
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

.route-cell {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
}

.route-from,
.route-to {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot.green {
  background: var(--success);
  box-shadow: 0 0 6px rgba(16, 185, 129, 0.4);
}

.dot.red {
  background: var(--danger);
  box-shadow: 0 0 6px rgba(239, 68, 68, 0.4);
}

.route-arrow {
  color: var(--text-tertiary);
  flex-shrink: 0;
}

.time-text {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.seat-info {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.seat-available {
  color: var(--success);
  font-weight: var(--font-weight-bold);
}

.seat-sep {
  color: var(--text-tertiary);
  margin: 0 2px;
}

.seat-total {
  color: var(--text-secondary);
}

.price-text {
  color: var(--brand);
  font-weight: var(--font-weight-bold);
  font-size: var(--font-size-md);
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
  .admin-trips {
    padding: var(--space-3);
  }

  .page-subtitle {
    padding-left: 0;
  }
}
</style>
