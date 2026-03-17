<template>
  <div class="not-found">
    <div class="error-container">
      <h1 class="error-code">404</h1>
      <h2 class="error-message">页面不存在</h2>
      <p class="error-description">
        抱歉，您访问的页面不存在或已被删除。
      </p>
      <el-button type="primary" @click="goHome" class="home-btn">
        <el-icon><Home /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Home } from '@element-plus/icons-vue'

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
  background-color: #f5f5f5;
}

.error-container {
  text-align: center;
  background: white;
  padding: 60px 40px;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

.error-code {
  font-size: 100px;
  font-weight: bold;
  color: #409eff;
  margin: 0 0 20px 0;
}

.error-message {
  font-size: 24px;
  margin: 0 0 15px 0;
  color: #303133;
}

.error-description {
  color: #909399;
  margin: 0 0 30px 0;
}

.home-btn {
  margin-top: 20px;
}
</style>