<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-shape bg-shape-1"></div>
      <div class="bg-shape bg-shape-2"></div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="login-logo">
            <div class="logo-icon-wrap">
              <el-icon size="28" color="#FFFFFF"><Van /></el-icon>
            </div>
            <h1>长途拼车</h1>
          </div>
          <p>安全 · 便捷 · 同乡拼车</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="login-button"
              round
            >
              登录
            </el-button>
          </el-form-item>
          
          <div class="login-footer">
            <el-link type="primary" @click="$router.push('/register')">
              还没有账号？立即注册
            </el-link>
            <el-link @click="$router.push('/')">
              返回首页
            </el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Van } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const validateUsername = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度3-20位'))
  } else {
    callback()
  }
}

const loginRules: FormRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    await authStore.login(loginForm)
    
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error: any) {
    if (error.response?.data?.error) {
      ElMessage.error(error.response.data.error)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('登录失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4F6EF7 0%, #38BDF8 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
}

.bg-shape-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -150px;
}

.bg-shape-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
}

.login-container {
  width: 100%;
  padding: var(--space-5);
  position: relative;
  z-index: 1;
}

.login-card {
  max-width: 420px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.15);
  padding: var(--space-10) var(--space-8);
}

.login-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
}

.logo-icon-wrap {
  width: 44px;
  height: 44px;
  background: var(--brand-gradient);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-header h1 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
}

.login-header p {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

.login-form {
  margin-top: var(--space-4);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-top: var(--space-2);
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: var(--space-5);
}

/* ===== 响应式 ===== */
@media (max-width: 480px) {
  .login-card {
    padding: var(--space-8) var(--space-5);
    border-radius: var(--radius-lg);
  }
  
  .login-logo {
    flex-direction: column;
    gap: var(--space-2);
  }
  
  .login-header h1 {
    font-size: var(--font-size-xl);
  }
  
  .login-footer {
    flex-direction: column;
    gap: var(--space-3);
    text-align: center;
  }
}
</style>
