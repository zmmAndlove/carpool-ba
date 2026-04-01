<template>
  <Layout>
    <div class="publish-container">
      <el-page-header @back="$router.go(-1)" class="page-header">
        <template #content>
          <span class="header-title">发布行程</span>
        </template>
      </el-page-header>

      <el-card class="publish-card" shadow="hover">
        <el-form
          ref="publishFormRef"
          :model="publishForm"
          :rules="publishRules"
          label-width="120px"
          class="publish-form"
          @submit.prevent="handlePublish"
        >
          <!-- 行程类型 -->
          <el-form-item label="行程类型" prop="tripType">
            <el-radio-group v-model="publishForm.tripType">
              <el-radio label="car_owner" size="large">
                <div class="radio-content">
                  <el-icon size="24"><Van /></el-icon>
                  <div>
                    <div class="radio-title">我是车主，车找人</div>
                    <div class="radio-desc">我有车，寻找同行乘客</div>
                  </div>
                </div>
              </el-radio>
              <el-radio label="passenger" size="large">
                <div class="radio-content">
                  <el-icon size="24"><User /></el-icon>
                  <div>
                    <div class="radio-title">我是乘客，人找车</div>
                    <div class="radio-desc">我需要搭车，寻找车主</div>
                  </div>
                </div>
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 出发地信息 -->
          <div class="form-section">
            <h3 class="section-title">出发地信息</h3>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="8">
                <el-form-item label="省份" prop="departureProvince">
                  <el-input
                    v-model="publishForm.departureProvince"
                    placeholder="请输入省份"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="城市" prop="departureCity">
                  <el-input
                    v-model="publishForm.departureCity"
                    placeholder="请输入城市"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="区县" prop="departureDistrict">
                  <el-input
                    v-model="publishForm.departureDistrict"
                    placeholder="请输入区县"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="详细地址" prop="departureDetail">
              <el-input
                v-model="publishForm.departureDetail"
                placeholder="例如：望京SOHO T3座、清华大学东门"
                :prefix-icon="Location"
              />
            </el-form-item>
          </div>

          <!-- 目的地信息 -->
          <div class="form-section">
            <h3 class="section-title">目的地信息</h3>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="8">
                <el-form-item label="省份" prop="destinationProvince">
                  <el-input
                    v-model="publishForm.destinationProvince"
                    placeholder="请输入省份"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="城市" prop="destinationCity">
                  <el-input
                    v-model="publishForm.destinationCity"
                    placeholder="请输入城市"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="区县" prop="destinationDistrict">
                  <el-input
                    v-model="publishForm.destinationDistrict"
                    placeholder="请输入区县"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="详细地址" prop="destinationDetail">
              <el-input
                v-model="publishForm.destinationDetail"
                placeholder="例如：火车站北广场、万象城"
                :prefix-icon="Location"
              />
            </el-form-item>
          </div>

          <!-- 行程时间 -->
          <div class="form-section">
            <h3 class="section-title">行程时间</h3>
            <el-form-item label="出发时间" prop="departureTime">
              <el-date-picker
                v-model="publishForm.departureTime"
                type="datetime"
                placeholder="选择出发日期和时间"
                :disabled-date="disabledDate"
                :shortcuts="dateShortcuts"
                style="width: 100%"
              />
            </el-form-item>
          </div>

          <!-- 座位信息 -->
          <div class="form-section">
            <h3 class="section-title">座位信息</h3>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="可用座位" prop="seatsAvailable">
                  <el-input-number
                    v-model="publishForm.seatsAvailable"
                    :min="1"
                    :max="publishForm.seatsTotal"
                    controls-position="right"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="总座位数" prop="seatsTotal">
                  <el-input-number
                    v-model="publishForm.seatsTotal"
                    :min="publishForm.seatsAvailable"
                    :max="7"
                    controls-position="right"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 价格信息 -->
          <div class="form-section" v-if="publishForm.tripType === 'car_owner'">
            <h3 class="section-title">价格信息</h3>
            <el-form-item label="人均价格" prop="pricePerPerson">
              <el-input-number
                v-model="publishForm.pricePerPerson"
                :min="0"
                :max="1000"
                :step="10"
                controls-position="right"
                style="width: 200px"
              >
                <template #append>元/人</template>
              </el-input-number>
              <div class="price-tip">系统建议价格：{{ suggestedPrice }}元/人</div>
            </el-form-item>
          </div>

          <!-- 车辆信息（仅车主） -->
          <div class="form-section" v-if="publishForm.tripType === 'car_owner'">
            <h3 class="section-title">车辆信息</h3>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="车型" prop="carModel">
                  <el-input
                    v-model="publishForm.carModel"
                    placeholder="例如：丰田卡罗拉"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="车牌号" prop="carPlate">
                  <el-input
                    v-model="publishForm.carPlate"
                    placeholder="例如：京A·12345"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 补充信息 -->
          <div class="form-section">
            <h3 class="section-title">补充信息</h3>
            <el-form-item label="行程描述" prop="description">
              <el-input
                v-model="publishForm.description"
                type="textarea"
                :rows="4"
                placeholder="请描述您的行程，例如：可放行李，走高速，不吸烟，可到地铁站接等"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </div>

          <!-- 提交按钮 -->
          <el-form-item>
            <div class="form-actions">
              <el-button @click="$router.go(-1)">取消</el-button>
              <el-button
                type="primary"
                :loading="loading"
                @click="handlePublish"
                class="submit-button"
              >
                发布行程
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Van, User, Location } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import Layout from '@/components/Layout.vue'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const publishFormRef = ref<FormInstance>()
const loading = ref(false)



const publishForm = reactive({
  tripType: 'car_owner',
  departureProvince: '',
  departureCity: '',
  departureDistrict: '',
  departureDetail: '',
  destinationProvince: '',
  destinationCity: '',
  destinationDistrict: '',
  destinationDetail: '',
  departureTime: null as Date | null,
  seatsAvailable: 1,
  seatsTotal: 4,
  pricePerPerson: 100,
  carModel: '',
  carPlate: '',
  description: ''
})



const suggestedPrice = computed(() => {
  // 简单的价格计算逻辑
  const basePrice = 100
  const distanceFactor = 1.2
  const timeFactor = publishForm.departureTime ?
    (new Date(publishForm.departureTime).getHours() < 12 ? 1 : 1.1) : 1

  return Math.round(basePrice * distanceFactor * timeFactor / 10) * 10
})

const publishRules: FormRules = {
  tripType: [
    { required: true, message: '请选择行程类型', trigger: 'change' }
  ],
  departureProvince: [
    { required: true, message: '请选择出发省份', trigger: 'change' }
  ],
  departureCity: [
    { required: true, message: '请选择出发城市', trigger: 'change' }
  ],
  destinationProvince: [
    { required: true, message: '请选择目的省份', trigger: 'change' }
  ],
  destinationCity: [
    { required: true, message: '请选择目的城市', trigger: 'change' }
  ],
  departureTime: [
    { required: true, message: '请选择出发时间', trigger: 'change' }
  ],
  seatsAvailable: [
    { required: true, message: '请填写可用座位数', trigger: 'blur' }
  ],
  pricePerPerson: [
    { required: true, message: '请填写人均价格', trigger: 'blur' }
  ]
}

const dateShortcuts = [
  {
    text: '明天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24)
      return date
    }
  },
  {
    text: '一周后',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 7)
      return date
    }
  }
]

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 3600 * 1000 * 24
}



const handlePublish = async () => {
  if (!publishFormRef.value) return

  try {
    await publishFormRef.value.validate()

    if (!authStore.isAuthenticated) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    loading.value = true

    const formData = {
      ...publishForm,
      departureTime: publishForm.departureTime ? publishForm.departureTime.toISOString() : null
    }

    const response = await axios.post('/api/trips', formData)

    ElMessage.success('行程发布成功')
    router.push(`/trip/${response.data.trip.id}`)
  } catch (error: any) {
    if (error.response?.data?.error) {
      ElMessage.error(error.response.data.error)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('发布失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.publish-container {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-6);
}

.page-header {
  margin-bottom: var(--space-5);
}

.header-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.publish-card {
  margin-bottom: var(--space-5);
  border: none;
  box-shadow: var(--shadow-card);
}

.publish-form {
  margin-top: var(--space-5);
}

.form-section {
  margin-bottom: var(--space-7);
  padding: var(--space-5);
  background: var(--bg-section);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.section-title {
  margin: 0 0 var(--space-5) 0;
  color: var(--text-primary);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  position: relative;
  padding-left: 14px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: var(--brand-gradient);
  border-radius: 2px;
}

.radio-content {
  display: flex;
  align-items: center;
  gap: 14px;
}

.radio-title {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.radio-desc {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
}

.price-tip {
  margin-top: var(--space-2);
  color: var(--success);
  font-size: var(--font-size-sm);
  display: flex;
  align-items: center;
  gap: 4px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-4);
  margin-top: var(--space-5);
}

.submit-button {
  min-width: 160px;
  padding: 12px 32px;
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-semibold);
}

::deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  box-shadow: 0 0 0 1px var(--border-color) inset;
  transition: var(--transition-fast);
}

::deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--brand) inset;
}

::deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--brand) inset, 0 0 0 3px rgba(79, 110, 247, 0.1);
}

::deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
}

::deep(.el-radio) {
  display: flex;
  align-items: center;
  height: auto;
  padding: var(--space-4);
  margin-bottom: var(--space-3);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-lg);
  transition: var(--transition-fast);
  background: var(--bg-card);
}

::deep(.el-radio:hover) {
  border-color: var(--brand);
  box-shadow: var(--shadow-card);
}

::deep(.el-radio.is-checked) {
  border-color: var(--brand);
  background: linear-gradient(135deg, rgba(79, 110, 247, 0.04) 0%, rgba(56, 189, 248, 0.04) 100%);
}

::deep(.el-textarea__inner) {
  border-radius: var(--radius-md);
}

::deep(.el-date-editor) {
  width: 100% !important;
}

::deep(.el-input-number) {
  width: 100%;
}

::deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
}

@media (max-width: 768px) {
  .publish-container {
    padding: var(--space-4);
  }

  .publish-form {
    margin-top: var(--space-4);
  }

  .publish-form :deep(.el-form-item) {
    margin-bottom: var(--space-4);
  }

  .publish-form :deep(.el-form-item__label) {
    font-size: var(--font-size-sm);
    width: 100px;
  }

  .form-section {
    padding: var(--space-4);
    border-radius: var(--radius-lg);
    margin-bottom: var(--space-5);
  }

  .section-title {
    font-size: var(--font-size-md);
    margin-bottom: var(--space-4);
  }

  .radio-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }

  .radio-title {
    font-size: var(--font-size-md);
  }

  .radio-desc {
    font-size: var(--font-size-xs);
  }

  .form-actions {
    flex-direction: column;
    gap: var(--space-3);
  }

  .submit-button {
    width: 100%;
    padding: 14px;
    font-size: var(--font-size-md);
  }
}

@media (max-width: 480px) {
  .publish-container {
    padding: var(--space-3);
  }

  .form-section {
    padding: var(--space-3);
    border-radius: var(--radius-md);
    margin-bottom: var(--space-4);
  }

  .section-title {
    font-size: var(--font-size-sm);
    margin-bottom: var(--space-3);
  }

  .submit-button {
    width: 100%;
    padding: 12px;
    font-size: var(--font-size-sm);
  }

  .price-tip {
    font-size: var(--font-size-xs);
  }
}
</style>
