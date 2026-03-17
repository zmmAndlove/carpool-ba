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
                  <el-select
                    v-model="publishForm.departureProvince"
                    placeholder="请选择省份"
                    @change="handleProvinceChange('departure')"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="province in provinces"
                      :key="province.value"
                      :label="province.label"
                      :value="province.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="城市" prop="departureCity">
                  <el-select
                    v-model="publishForm.departureCity"
                    placeholder="请选择城市"
                    @change="handleCityChange('departure')"
                    style="width: 100%"
                    :disabled="!publishForm.departureProvince"
                  >
                    <el-option
                      v-for="city in departureCities"
                      :key="city.value"
                      :label="city.label"
                      :value="city.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="区县" prop="departureDistrict">
                  <el-select
                    v-model="publishForm.departureDistrict"
                    placeholder="请选择区县"
                    style="width: 100%"
                    :disabled="!publishForm.departureCity"
                  >
                    <el-option
                      v-for="district in departureDistricts"
                      :key="district.value"
                      :label="district.label"
                      :value="district.value"
                    />
                  </el-select>
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
                  <el-select
                    v-model="publishForm.destinationProvince"
                    placeholder="请选择省份"
                    @change="handleProvinceChange('destination')"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="province in provinces"
                      :key="province.value"
                      :label="province.label"
                      :value="province.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="城市" prop="destinationCity">
                  <el-select
                    v-model="publishForm.destinationCity"
                    placeholder="请选择城市"
                    @change="handleCityChange('destination')"
                    style="width: 100%"
                    :disabled="!publishForm.destinationProvince"
                  >
                    <el-option
                      v-for="city in destinationCities"
                      :key="city.value"
                      :label="city.label"
                      :value="city.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="区县" prop="destinationDistrict">
                  <el-select
                    v-model="publishForm.destinationDistrict"
                    placeholder="请选择区县"
                    style="width: 100%"
                    :disabled="!publishForm.destinationCity"
                  >
                    <el-option
                      v-for="district in destinationDistricts"
                      :key="district.value"
                      :label="district.label"
                      :value="district.value"
                    />
                  </el-select>
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

// 省市区数据（简化版）
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

const districtsData: Record<string, Array<{value: string, label: string}>> = {
  '北京市': [
    { value: '朝阳区', label: '朝阳区' },
    { value: '海淀区', label: '海淀区' },
    { value: '东城区', label: '东城区' },
    { value: '西城区', label: '西城区' }
  ],
  '石家庄市': [
    { value: '长安区', label: '长安区' },
    { value: '桥西区', label: '桥西区' },
    { value: '新华区', label: '新华区' },
    { value: '裕华区', label: '裕华区' }
  ]
}

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

const departureCities = computed(() => {
  return citiesData[publishForm.departureProvince] || []
})

const departureDistricts = computed(() => {
  return districtsData[publishForm.departureCity] || []
})

const destinationCities = computed(() => {
  return citiesData[publishForm.destinationProvince] || []
})

const destinationDistricts = computed(() => {
  return districtsData[publishForm.destinationCity] || []
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

const handleProvinceChange = (type: 'departure' | 'destination') => {
  if (type === 'departure') {
    publishForm.departureCity = ''
    publishForm.departureDistrict = ''
  } else {
    publishForm.destinationCity = ''
    publishForm.destinationDistrict = ''
  }
}

const handleCityChange = (type: 'departure' | 'destination') => {
  if (type === 'departure') {
    publishForm.departureDistrict = ''
  } else {
    publishForm.destinationDistrict = ''
  }
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
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
}

.publish-card {
  margin-bottom: 20px;
}

.publish-form {
  margin-top: 20px;
}

.form-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.section-title {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.radio-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.radio-title {
  font-weight: 600;
  color: #333;
}

.radio-desc {
  font-size: 14px;
  color: #666;
}

.price-tip {
  margin-top: 8px;
  color: #67c23a;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 20px;
}

.submit-button {
  min-width: 150px;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-button) {
  border-radius: 6px;
}

:deep(.el-radio) {
  display: flex;
  align-items: center;
  height: auto;
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  transition: all 0.3s;
}

:deep(.el-radio:hover) {
  border-color: #409eff;
}

:deep(.el-radio.is-checked) {
  border-color: #409eff;
  background: #ecf5ff;
}

@media (max-width: 768px) {
  .publish-container {
    padding: 10px;
  }
  
  .form-section {
    padding: 15px;
    border-radius: 16px;
  }
  
  .section-title {
    font-size: 18px;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-label {
    font-size: 14px;
    margin-bottom: 8px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .submit-button {
    width: 100%;
    padding: 14px;
  }
  
  .tips-section {
    padding: 15px;
    border-radius: 16px;
  }
  
  .tips-title {
    font-size: 16px;
  }
  
  .tips-list li {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .publish-container {
    padding: 8px;
  }
  
  .form-section {
    padding: 12px;
    border-radius: 14px;
  }
  
  .section-title {
    font-size: 16px;
    margin-bottom: 16px;
  }
  
  .form-group {
    margin-bottom: 16px;
  }
  
  .form-label {
    font-size: 13px;
    margin-bottom: 6px;
  }
  
  .form-group :deep(.el-input__wrapper),
  .form-group :deep(.el-textarea__inner) {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .form-group :deep(.el-select) {
    width: 100%;
  }
  
  .form-group :deep(.el-date-picker) {
    width: 100%;
  }
  
  .form-actions {
    gap: 10px;
  }
  
  .submit-button {
    width: 100%;
    padding: 12px;
    font-size: 15px;
  }
  
  .tips-section {
    padding: 12px;
    border-radius: 14px;
  }
  
  .tips-title {
    font-size: 15px;
    margin-bottom: 12px;
  }
  
  .tips-list {
    padding-left: 18px;
  }
  
  .tips-list li {
    font-size: 12px;
    margin-bottom: 8px;
    line-height: 1.5;
  }
}
</style>