<template>
  <div class="register-page">
    <div class="register-bg">
      <div class="bg-shape bg-shape-1"></div>
      <div class="bg-shape bg-shape-2"></div>
    </div>
    
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <div class="register-logo">
            <div class="logo-icon-wrap">
              <el-icon size="28" color="#FFFFFF"><Van /></el-icon>
            </div>
            <h1>注册账号</h1>
          </div>
          <p>加入长途拼车，开启便捷出行</p>
        </div>
        
        <el-steps :active="activeStep" finish-status="success" class="register-steps" simple>
          <el-step title="基本信息" />
          <el-step title="个人资料" />
          <el-step title="完成注册" />
        </el-steps>
        
        <!-- 第一步 -->
        <div v-if="activeStep === 0" class="step-content">
          <el-form
            ref="step1FormRef"
            :model="step1Form"
            :rules="step1Rules"
            class="register-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="step1Form.username"
                placeholder="请输入用户名（3-20位）"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="step1Form.password"
                type="password"
                placeholder="请输入密码（至少6位）"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="step1Form.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 第二步 -->
        <div v-if="activeStep === 1" class="step-content">
          <el-form
            ref="step2FormRef"
            :model="step2Form"
            :rules="step2Rules"
            class="register-form"
          >
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="step2Form.realName"
                placeholder="请输入真实姓名"
                size="large"
              />
            </el-form-item>
            
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="step2Form.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
                <el-radio label="其他">其他</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="年龄" prop="age">
              <el-input-number
                v-model="step2Form.age"
                :min="18"
                :max="80"
                controls-position="right"
                style="width: 100%"
                size="large"
              />
            </el-form-item>
            
            <el-form-item label="家乡" prop="hometownProvince">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-select
                    v-model="step2Form.hometownProvince"
                    placeholder="省份"
                    style="width: 100%"
                    size="large"
                    @change="step2Form.hometownCity = ''"
                  >
                    <el-option
                      v-for="province in provinces"
                      :key="province.value"
                      :label="province.label"
                      :value="province.value"
                    />
                  </el-select>
                </el-col>
                <el-col :span="12">
                  <el-select
                    v-model="step2Form.hometownCity"
                    placeholder="城市"
                    style="width: 100%"
                    size="large"
                    :disabled="!step2Form.hometownProvince"
                  >
                    <el-option
                      v-for="city in hometownCities"
                      :key="city.value"
                      :label="city.label"
                      :value="city.value"
                    />
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item label="当前城市" prop="currentProvince">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-select
                    v-model="step2Form.currentProvince"
                    placeholder="省份"
                    style="width: 100%"
                    size="large"
                    @change="step2Form.currentCity = ''"
                  >
                    <el-option
                      v-for="province in provinces"
                      :key="province.value"
                      :label="province.label"
                      :value="province.value"
                    />
                  </el-select>
                </el-col>
                <el-col :span="12">
                  <el-select
                    v-model="step2Form.currentCity"
                    placeholder="城市"
                    style="width: 100%"
                    size="large"
                    :disabled="!step2Form.currentProvince"
                  >
                    <el-option
                      v-for="city in currentCities"
                      :key="city.value"
                      :label="city.label"
                      :value="city.value"
                    />
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item label="毕业院校" prop="university">
              <el-input
                v-model="step2Form.university"
                placeholder="请输入毕业院校（选填）"
                size="large"
              />
            </el-form-item>
            
            <el-form-item label="兴趣爱好" prop="interests">
              <el-input
                v-model="step2Form.interests"
                type="textarea"
                placeholder="请输入兴趣爱好，用逗号分隔（选填）"
                :rows="3"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 第三步 -->
        <div v-if="activeStep === 2" class="step-content success-content">
          <div class="success-icon">
            <el-icon size="64" color="#10B981">
              <CircleCheck />
            </el-icon>
          </div>
          <h2 class="success-title">注册成功！</h2>
          <p class="success-message">欢迎加入长途拼车平台</p>
          <div class="user-info-summary">
            <div class="info-row">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ step1Form.username }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">姓名</span>
              <span class="info-value">{{ step2Form.realName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">家乡</span>
              <span class="info-value">{{ step2Form.hometownProvince }}{{ step2Form.hometownCity }}</span>
            </div>
          </div>
        </div>
        
        <!-- 导航按钮 -->
        <div class="step-actions">
          <el-button
            v-if="activeStep > 0 && activeStep < 2"
            @click="prevStep"
            round
          >
            上一步
          </el-button>
          
          <el-button
            v-if="activeStep < 2"
            type="primary"
            :loading="loading"
            @click="nextStep"
            round
          >
            {{ activeStep === 1 ? '完成注册' : '下一步' }}
          </el-button>
          
          <el-button
            v-if="activeStep === 2"
            type="primary"
            @click="goToHome"
            round
          >
            开始使用
          </el-button>
          
          <el-button
            v-if="activeStep < 2"
            @click="$router.push('/login')"
            type="text"
          >
            已有账号？去登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  User,
  Lock,
  CircleCheck,
  Van
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const activeStep = ref(0)
const loading = ref(false)

const step1FormRef = ref<FormInstance>()
const step2FormRef = ref<FormInstance>()

const provinces = ref([
  { value: '北京市', label: '北京市' },
  { value: '上海市', label: '上海市' },
  { value: '广东省', label: '广东省' },
  { value: '江苏省', label: '江苏省' },
  { value: '浙江省', label: '浙江省' },
  { value: '四川省', label: '四川省' },
  { value: '湖北省', label: '湖北省' },
  { value: '河北省', label: '河北省' },
  { value: '河南省', label: '河南省' },
  { value: '山东省', label: '山东省' }
])

const citiesData: Record<string, Array<{value: string, label: string}>> = {
  '北京市': [{ value: '北京市', label: '北京市' }],
  '河北省': [
    { value: '石家庄市', label: '石家庄市' },
    { value: '保定市', label: '保定市' },
    { value: '唐山市', label: '唐山市' },
    { value: '邯郸市', label: '邯郸市' }
  ],
  '广东省': [
    { value: '广州市', label: '广州市' },
    { value: '深圳市', label: '深圳市' },
    { value: '东莞市', label: '东莞市' },
    { value: '佛山市', label: '佛山市' }
  ]
}

const step1Form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const step2Form = reactive({
  realName: '',
  gender: '男',
  age: 25,
  hometownProvince: '',
  hometownCity: '',
  currentProvince: '',
  currentCity: '',
  university: '',
  interests: ''
})

const hometownCities = computed(() => citiesData[step2Form.hometownProvince] || [])
const currentCities = computed(() => citiesData[step2Form.currentProvince] || [])

const validateUsername = (rule: any, value: string, callback: any) => {
  if (!value) callback(new Error('请输入用户名'))
  else if (value.length < 3 || value.length > 20) callback(new Error('用户名长度3-20位'))
  else callback()
}

const validatePassword = (rule: any, value: string, callback: any) => {
  if (!value) callback(new Error('请输入密码'))
  else if (value.length < 6) callback(new Error('密码至少6位'))
  else callback()
}

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (!value) callback(new Error('请确认密码'))
  else if (value !== step1Form.password) callback(new Error('两次输入密码不一致'))
  else callback()
}

const step1Rules: FormRules = {
  username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }]
}

const step2Rules: FormRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度2-20位', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  hometownProvince: [{ required: true, message: '请选择家乡省份', trigger: 'change' }],
  hometownCity: [{ required: true, message: '请选择家乡城市', trigger: 'change' }],
  currentProvince: [{ required: true, message: '请选择当前省份', trigger: 'change' }],
  currentCity: [{ required: true, message: '请选择当前城市', trigger: 'change' }]
}

const prevStep = () => {
  if (activeStep.value > 0) activeStep.value--
}

const nextStep = async () => {
  try {
    if (activeStep.value === 0) {
      await step1FormRef.value?.validate()
      activeStep.value++
    } else if (activeStep.value === 1) {
      await step2FormRef.value?.validate()
      await handleRegister()
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const handleRegister = async () => {
  try {
    loading.value = true
    const registerData = {
      username: step1Form.username,
      password: step1Form.password,
      realName: step2Form.realName,
      gender: step2Form.gender,
      age: step2Form.age,
      hometownProvince: step2Form.hometownProvince,
      hometownCity: step2Form.hometownCity,
      currentProvince: step2Form.currentProvince,
      currentCity: step2Form.currentCity,
      university: step2Form.university,
      interests: step2Form.interests
    }
    await authStore.register(registerData)
    activeStep.value = 2
  } catch (error: any) {
    if (error.response?.data?.error) {
      ElMessage.error(error.response.data.error)
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #4F6EF7 0%, #38BDF8 50%, #6EE7B7 100%);
  position: relative;
  overflow: hidden;
  padding: var(--space-5);
}

.register-bg {
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

.register-container {
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 1;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.15);
  padding: var(--space-8) var(--space-8);
}

.register-header {
  text-align: center;
  margin-bottom: var(--space-6);
}

.register-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
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

.register-header h1 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
}

.register-header p {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

.register-steps {
  margin-bottom: var(--space-6);
}

.step-content {
  margin-bottom: var(--space-6);
}

.register-form {
  margin-top: var(--space-2);
}

.success-content {
  text-align: center;
  padding: var(--space-4) 0;
}

.success-icon {
  margin-bottom: var(--space-4);
}

.success-title {
  margin: 0 0 var(--space-2);
  color: var(--text-primary);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
}

.success-message {
  margin: 0 0 var(--space-6);
  color: var(--text-tertiary);
  font-size: var(--font-size-base);
}

.user-info-summary {
  background: var(--bg-section);
  border-radius: var(--radius-md);
  padding: var(--space-4);
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: var(--space-2) 0;
  border-bottom: 1px solid var(--border-light);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

.info-value {
  color: var(--text-primary);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .register-card {
    padding: var(--space-6) var(--space-5);
  }
  
  .step-actions {
    flex-direction: column;
  }
  
  .step-actions .el-button {
    width: 100%;
  }
}
</style>
