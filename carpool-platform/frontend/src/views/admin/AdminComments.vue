<template>
  <div class="admin-comments">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><ChatDotRound /></el-icon>
        评论管理
      </h1>
      <p class="page-subtitle">共 {{ total }} 条评论</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <el-table
        :data="comments"
        stripe
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: 'var(--bg-section)', color: 'var(--text-secondary)', fontWeight: '600', fontSize: '13px' }"
      >
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="28" class="comment-avatar">
                {{ row.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="user-name">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="rating" label="评分" width="120" align="center">
          <template #default="{ row }">
            <div class="rating-cell">
              <el-rate :model-value="row.rating || 0" disabled size="small" />
              <span class="rating-num">{{ row.rating?.toFixed(1) || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="likes" label="点赞" width="80" align="center">
          <template #default="{ row }">
            <span class="likes-badge">
              <el-icon><Star /></el-icon>
              {{ row.likes || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" min-width="160">
          <template #default="{ row }">
            <span class="time-text">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-popconfirm
              title="确定要删除这条评论吗？"
              confirm-button-text="删除"
              cancel-button-text="取消"
              confirm-button-type="danger"
              @confirm="deleteComment(row.id)"
            >
              <template #reference>
                <el-button type="danger" size="small" round plain>
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-popconfirm>
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
        @current-change="fetchComments"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ChatDotRound, Star, Delete } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const comments = ref<any[]>([]);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const loading = ref(false);

const fetchComments = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`/api/admin/comments?page=${page.value}&size=${size.value}`);
    comments.value = response.data.comments;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取评论列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const deleteComment = async (id: number) => {
  try {
    await axios.delete(`/api/admin/comments/${id}`);
    ElMessage.success('评论已删除');
    fetchComments();
  } catch (error) {
    console.error('删除评论失败:', error);
    ElMessage.error('删除失败，请重试');
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

onMounted(() => {
  fetchComments();
});
</script>

<style scoped>
.admin-comments {
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
  gap: var(--space-2);
}

.comment-avatar {
  background: linear-gradient(135deg, var(--brand) 0%, var(--brand-light) 100%);
  color: white;
  font-weight: var(--font-weight-bold);
  font-size: 12px;
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

.likes-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--warning);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
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
  .admin-comments {
    padding: var(--space-3);
  }

  .page-subtitle {
    padding-left: 0;
  }
}
</style>
