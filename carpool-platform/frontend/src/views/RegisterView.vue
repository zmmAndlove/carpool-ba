<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>注册账号</h1>
        <p>加入长途拼车，开启便捷出行</p>
      </div>
      
      <el-steps :active="activeStep" finish-status="success" class="register-steps">
        <el-step title="基本信息" />
        <el-step title="个人资料" />
        <el-step title="完成注册" />
      </el-steps>
      
      <!-- 第一步：基本信息 -->
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
      
      <!-- 第二步：个人资料 -->
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
            />
          </el-form-item>
          
          <el-form-item label="家乡" prop="hometown">
            <el-row :gutter="10">
              <el-col :span="12">
                <el-select
                  v-model="step2Form.hometownProvince"
                  placeholder="省份"
                  style="width: 100%"
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
          
          <el-form-item label="当前城市" prop="currentCity">
            <el-row :gutter="10">
              <el-col :span="12">
                <el-select
                  v-model="step2Form.currentProvince"
                  placeholder="省份"
                  style="width: 100%"
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
      
      <!-- 第三步：完成注册 -->
      <div v-if="activeStep === 2" class="step-content success-content">
        <div class="success-icon">
          <el-icon size="80" color="#34C759">
            <CircleCheck />
          </el-icon>
        </div>
        <h2 class="success-title">注册成功！</h2>
        <p class="success-message">欢迎加入长途拼车平台</p>
        <div class="user-info">
          <p>用户名：{{ step1Form.username }}</p>
          <p>姓名：{{ step2Form.realName }}</p>
          <p>家乡：{{ step2Form.hometownProvince }}{{ step2Form.hometownCity }}</p>
        </div>
      </div>
      
      <!-- 导航按钮 -->
      <div class="step-actions">
        <el-button
          v-if="activeStep > 0 && activeStep < 2"
          @click="prevStep"
          class="step-button"
        >
          上一步
        </el-button>
        
        <el-button
          v-if="activeStep < 2"
          type="primary"
          :loading="loading"
          @click="nextStep"
          class="step-button"
        >
          {{ activeStep === 1 ? '完成注册' : '下一步' }}
        </el-button>
        
        <el-button
          v-if="activeStep === 2"
          type="primary"
          @click="goToHome"
          class="step-button"
        >
          开始使用
        </el-button>
        
        <el-button
          v-if="activeStep < 2"
          @click="$router.push('/login')"
          class="step-button"
        >
          已有账号？去登录
        </el-button>
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
  CircleCheck
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const activeStep = ref(0)
const loading = ref(false)
const sendingSms = ref(false)
const smsCountdown = ref(0)

const step1FormRef = ref<FormInstance>()
const step2FormRef = ref<FormInstance>()

// 省市区数据
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
  '北京市': [
    { value: '北京市', label: '北京市' }
  ],
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

const hometownCities = computed(() => {
  return citiesData[step2Form.hometownProvince] || []
})

const currentCities = computed(() => {
  return citiesData[step2Form.currentProvince] || []
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

const validatePassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码至少6位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== step1Form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const step1Rules: FormRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const step2Rules: FormRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度2-20位', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  hometownProvince: [
    { required: true, message: '请选择家乡省份', trigger: 'change' }
  ],
  hometownCity: [
    { required: true, message: '请选择家乡城市', trigger: 'change' }
  ],
  currentProvince: [
    { required: true, message: '请选择当前省份', trigger: 'change' }
  ],
  currentCity: [
    { required: true, message: '请选择当前城市', trigger: 'change' }
  ]
}

const sendSmsCode = async () => {
  if (!step1Form.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(step1Form.phone)) {
    ElMessage.warning('手机号格式不正确')
    return
  }
  
  try {
    sendingSms.value = true
    // 模拟发送短信验证码
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 实际项目中应该调用后端API
    // await axios.post('/api/auth/sms-code', { phone: step1Form.phone })
    
    ElMessage.success('验证码已发送')
    
    // 开始倒计时
    smsCountdown.value = 60
    const timer = setInterval(() => {
      smsCountdown.value--
      if (smsCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    ElMessage.error('发送验证码失败')
  } finally {
    sendingSms.value = false
  }
}

const prevStep = () => {
  if (activeStep.value > 0) {
    activeStep.value--
  }
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
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  margin: 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.register-header p {
  margin: 10px 0 0;
  color: #666;
  font-size: 16px;
}

.register-steps {
  margin-bottom: 40px;
}

.step-content {
  margin-bottom: 40px;
}

.register-form {
  margin-top: 20px;
}

.sms-code-input {
  display: flex;
  gap: 10px;
}

.sms-button {
  white-space: nowrap;
}

.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 20px;
}

.success-title {
  margin: 0 0 10px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.success-message {
  margin: 0 0 30px;
  color: #666;
  font-size: 16px;
}

.user-info {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 20px;
  text-align: left;
}

.user-info p {
  margin: 8px 0;
  color: #333;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.step-button {
  min-width: 120px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
}

@media (max-width: 768px) {
  .register-card {
    padding: 20px;
  }
  
  .step-actions {
    flex-direction: column;
  }
  
  .step-button {
    width: 100%;
  }
}
</style>
