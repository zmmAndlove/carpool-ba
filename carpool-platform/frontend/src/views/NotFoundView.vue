<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="error-visual">
        <span class="error-4">4</span>
        <span class="error-0">
          <div class="error-car">
            <el-icon size="48"><Van /></el-icon>
          </div>
        </span>
        <span class="error-4">4</span>
      </div>
      <h2 class="error-title">页面走丢了</h2>
      <p class="error-desc">抱歉，您访问的页面不存在或已被删除</p>
      <div class="error-actions">
        <el-button type="primary" @click="goHome" round>
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button @click="$router.go(-1)" round plain>返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { HomeFilled, Van } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  background: var(--bg-section);
  padding: var(--space-5);
}

.not-found-content {
  text-align: center;
  max-width: 480px;
}

.error-visual {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  margin-bottom: var(--space-8);
}

.error-4 {
  font-size: 80px;
  font-weight: var(--font-weight-bold);
  color: var(--text-tertiary);
  line-height: 1;
}

.error-0 {
  width: 100px;
  height: 100px;
  background: var(--brand-gradient);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 80px;
  font-weight: var(--font-weight-bold);
  line-height: 1;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.error-car {
  color: white;
}

.error-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3);
}

.error-desc {
  color: var(--text-tertiary);
  font-size: var(--font-size-md);
  margin: 0 0 var(--space-8);
  line-height: var(--line-height-relaxed);
}

.error-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: center;
}

@media (max-width: 480px) {
  .error-4 {
    font-size: 56px;
  }
  
  .error-0 {
    width: 72px;
    height: 72px;
    font-size: 56px;
  }
  
  .error-title {
    font-size: var(--font-size-2xl);
  }
  
  .error-actions {
    flex-direction: column;
  }
}
</style>
