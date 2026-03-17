<template>
  <div class="trip-detail-container">
    <!-- 返回按钮 -->
    <el-page-header @back="$router.go(-1)" class="page-header">
      <template #content>
        <span class="header-title">行程详情</span>
      </template>
    </el-page-header>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="trip" class="trip-content">
      <!-- 行程信息卡片 -->
      <el-card class="trip-info-card" shadow="hover">
        <!-- 行程头部 -->
        <div class="trip-header">
          <div class="trip-type-badge">
            <el-tag :type="trip.tripType === 'car_owner' ? 'success' : 'warning'" size="large">
              {{ trip.tripType === 'car_owner' ? '车找人' : '人找车' }}
            </el-tag>
            <el-tag v-if="trip.hometownMatch" type="primary" size="large">
              同乡行程
            </el-tag>
            <el-tag :type="statusColor" size="large">
              {{ statusText }}
            </el-tag>
          </div>
          
          <div class="trip-actions">
            <el-button
              v-if="canJoin"
              type="primary"
              size="large"
              @click="handleJoin"
              :loading="joining"
            >
              加入行程
            </el-button>
            <el-button
              v-if="canCancel"
              type="warning"
              size="large"
              @click="handleCancel"
              :loading="cancelling"
            >
              取消加入
            </el-button>
            <el-button
              v-if="canComplete"
              type="success"
              size="large"
              @click="handleComplete"
              :loading="completing"
            >
              完成行程
            </el-button>
            <el-button
              v-if="canEdit"
              type="info"
              size="large"
              @click="handleEdit"
            >
              编辑行程
            </el-button>
            <el-button
              v-if="canDelete"
              type="danger"
              size="large"
              @click="handleDelete"
              :loading="deleting"
            >
              删除行程
            </el-button>
            <el-button
              icon="Share"
              size="large"
              @click="handleShare"
            >
              分享
            </el-button>
          </div>
        </div>

        <!-- 行程路线 -->
        <div class="trip-route-section">
          <h3 class="section-title">行程路线</h3>
          <div class="route-display">
            <div class="route-point departure">
              <div class="point-icon">
                <el-icon size="24" color="#007AFF">
                  <Location />
                </el-icon>
              </div>
              <div class="point-info">
                <div class="point-city">{{ trip.departureCity }}</div>
                <div class="point-detail">{{ trip.departureDetail || '详细地址' }}</div>
                <div class="point-district">{{ trip.departureDistrict }}</div>
              </div>
            </div>
            
            <div class="route-line">
              <div class="line-duration">
                <el-icon><Clock /></el-icon>
                <span>约4小时 · 300公里</span>
              </div>
              <div class="line-arrow">
                <el-icon size="24" color="#666">
                  <Right />
                </el-icon>
              </div>
            </div>
            
            <div class="route-point destination">
              <div class="point-icon">
                <el-icon size="24" color="#34C759">
                  <LocationFilled />
                </el-icon>
              </div>
              <div class="point-info">
                <div class="point-city">{{ trip.destinationCity }}</div>
                <div class="point-detail">{{ trip.destinationDetail || '详细地址' }}</div>
                <div class="point-district">{{ trip.destinationDistrict }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 行程详情 -->
        <div class="trip-details-section">
          <h3 class="section-title">行程详情</h3>
          <el-row :gutter="40">
            <el-col :xs="24" :sm="12" :md="8">
              <div class="detail-item">
                <div class="detail-label">
                  <el-icon><Calendar /></el-icon>
                  <span>出发时间</span>
                </div>
                <div class="detail-value">{{ formatDateTime(trip.departureTime) }}</div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8">
              <div class="detail-item">
                <div class="detail-label">
                  <el-icon><User /></el-icon>
                  <span>座位情况</span>
                </div>
                <div class="detail-value">
                  {{ trip.seatsAvailable }}/{{ trip.seatsTotal }} 空位
                </div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8" v-if="trip.pricePerPerson">
              <div class="detail-item">
                <div class="detail-label">
                  <el-icon><Money /></el-icon>
                  <span>人均价格</span>
                </div>
                <div class="detail-value price">
                  {{ trip.pricePerPerson }} 元/人
                </div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8" v-if="trip.carModel">
              <div class="detail-item">
                <div class="detail-label">
                  <el-icon><Van /></el-icon>
                  <span>车型</span>
                </div>
                <div class="detail-value">{{ trip.carModel }}</div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8" v-if="trip.carPlate">
              <div class="detail-item">
                <div class="detail-label">
                    <el-icon><Van /></el-icon>
                    <span>车牌号</span>
                  </div>
                <div class="detail-value">{{ trip.carPlate }}</div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8">
              <div class="detail-item">
                <div class="detail-label">
                  <el-icon><View /></el-icon>
                  <span>查看次数</span>
                </div>
                <div class="detail-value">{{ trip.viewCount }}次</div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8">
              <div class="detail-item">
                <div class="detail-label">
                  <el-icon><Connection /></el-icon>
                  <span>匹配次数</span>
                </div>
                <div class="detail-value">{{ trip.matchCount }}次</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 行程描述 -->
        <div v-if="trip.description" class="trip-description-section">
          <h3 class="section-title">行程描述</h3>
          <div class="description-content">
            {{ trip.description }}
          </div>
        </div>
      </el-card>

      <!-- 发布者信息 -->
      <el-card class="publisher-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3>发布者信息</h3>
            <el-button
              v-if="!isPublisher && authStore.isAuthenticated"
              type="primary"
              size="small"
              @click="startChat"
            >
              联系车主
            </el-button>
          </div>
        </template>
        
        <div class="publisher-info">
          <div class="publisher-avatar">
            <el-avatar :src="trip.publisher?.avatar" :size="80">
              {{ trip.publisher?.realName?.charAt(0) || '用户' }}
            </el-avatar>
            <div class="publisher-verification">
              <el-tag type="success" size="small">已认证</el-tag>
            </div>
          </div>
          
          <div class="publisher-details">
            <h4 class="publisher-name">{{ trip.publisher?.realName || trip.publisher?.phone }}</h4>
            
            <div class="publisher-rating">
              <el-rate
                :model-value="trip.publisher?.rating || 0"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}分"
              />
            </div>
            
            <div class="publisher-stats">
              <div class="stat-item">
                <span class="stat-label">行程次数</span>
                <span class="stat-value">{{ trip.publisher?.tripCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">完成行程</span>
                <span class="stat-value">{{ trip.publisher?.completedTrips || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">好评率</span>
                <span class="stat-value">98%</span>
              </div>
            </div>
            
            <div class="publisher-location">
              <el-icon><Location /></el-icon>
              <span>家乡：{{ trip.publisher?.hometownProvince }}{{ trip.publisher?.hometownCity }}</span>
            </div>
            
            <div class="publisher-location">
              <el-icon><LocationFilled /></el-icon>
              <span>当前：{{ trip.publisher?.currentProvince }}{{ trip.publisher?.currentCity }}</span>
            </div>
            
            <div v-if="trip.publisher?.university" class="publisher-education">
              <el-icon><School /></el-icon>
              <span>{{ trip.publisher.university }}</span>
            </div>
            
            <div v-if="trip.publisher?.interests" class="publisher-interests">
              <el-icon><Star /></el-icon>
              <span>兴趣：{{ trip.publisher.interests }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 同行乘客 -->
      <el-card class="passengers-card" shadow="hover" v-if="trip.seatsTotal > 1">
        <template #header>
          <h3>同行乘客 ({{ passengers.length }}/{{ trip.seatsTotal - 1 }})</h3>
        </template>
        
        <div v-if="passengers.length > 0" class="passengers-list">
          <div v-for="passenger in passengers" :key="passenger.id" class="passenger-item">
            <el-avatar :src="passenger.avatar" :size="40">
              {{ passenger.realName?.charAt(0) || '乘' }}
            </el-avatar>
            <div class="passenger-info">
              <div class="passenger-name">{{ passenger.realName || passenger.phone }}</div>
              <div class="passenger-rating">
                <el-rate
                  v-model="passenger.rating"
                  disabled
                  size="small"
                />
              </div>
            </div>
            <div class="passenger-status">
              <el-tag :type="passenger.status === 'confirmed' ? 'success' : 'warning'" size="small">
                {{ passenger.status === 'confirmed' ? '已确认' : '待确认' }}
              </el-tag>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-passengers">
          <el-empty description="暂无同行乘客">
            <p>成为第一个加入行程的人</p>
          </el-empty>
        </div>
      </el-card>

      <!-- 安全提示 -->
      <el-card class="safety-card" shadow="hover">
        <template #header>
          <h3>安全提示</h3>
        </template>
        
        <div class="safety-tips">
          <div class="safety-tip">
            <el-icon color="#34C759"><CircleCheck /></el-icon>
            <span>请确认对方身份信息，建议出发前视频通话验证</span>
          </div>
          <div class="safety-tip">
            <el-icon color="#34C759"><CircleCheck /></el-icon>
            <span>行程中请保持手机畅通，及时与家人朋友分享位置</span>
          </div>
          <div class="safety-tip">
            <el-icon color="#34C759"><CircleCheck /></el-icon>
            <span>如遇紧急情况，请立即拨打110或联系平台客服</span>
          </div>
          <div class="safety-tip">
            <el-icon color="#34C759"><CircleCheck /></el-icon>
            <span>行程完成后请及时互相评价，帮助其他用户参考</span>
          </div>
        </div>
      </el-card>
    </div>

    <div v-else class="error-container">
      <el-empty description="行程不存在或已被删除">
        <el-button type="primary" @click="$router.push('/search')">
          返回搜索
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Location,
  LocationFilled,
  Clock,
  Right,
  Calendar,
  User,
  Money,
  Van,
  View,
  Connection,
  School,
  Star,
  CircleCheck,
  Share
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const trip = ref<any>(null)
const passengers = ref<any[]>([])
const joining = ref(false)
const cancelling = ref(false)
const completing = ref(false)
const deleting = ref(false)

const tripId = computed(() => route.params.id as string)

const statusText = computed(() => {
  const statusMap: Record<string, string> = {
    pending: '待匹配',
    matched: '已匹配',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[trip.value?.status] || '未知状态'
})

const statusColor = computed(() => {
  const colorMap: Record<string, string> = {
    pending: 'warning',
    matched: 'success',
    completed: 'info',
    cancelled: 'danger'
  }
  return colorMap[trip.value?.status] || 'info'
})

const isPublisher = computed(() => {
  return authStore.isAuthenticated && trip.value?.userId === authStore.user?.id
})

const canJoin = computed(() => {
  return (
    authStore.isAuthenticated &&
    !isPublisher.value &&
    trip.value?.status === 'pending' &&
    trip.value?.seatsAvailable > 0
  )
})

const canCancel = computed(() => {
  // 简化逻辑：用户可以看到取消按钮
  return authStore.isAuthenticated && !isPublisher.value
})

const canComplete = computed(() => {
  return isPublisher.value && trip.value?.status === 'matched'
})

const canEdit = computed(() => {
  return isPublisher.value && trip.value?.status === 'pending'
})

const canDelete = computed(() => {
  return isPublisher.value && trip.value?.status === 'pending'
})

// 格式化日期时间
const formatDateTime = (time: string) => {
  return dayjs(time).format('YYYY年MM月DD日 HH:mm')
}

// 获取行程详情
const fetchTripDetail = async () => {
  try {
    loading.value = true
    const response = await axios.get(`/api/trips/${tripId.value}`)
    trip.value = response.data.trip
    
    // 模拟发布者信息
    trip.value.publisher = {
      id: trip.value.userId,
      realName: '测试用户',
      avatar: '',
      rating: 4.8,
      tripCount: 15,
      completedTrips: 12,
      hometownProvince: '河北省',
      hometownCity: '石家庄市',
      currentProvince: '北京市',
      currentCity: '北京市',
      university: '清华大学',
      interests: '篮球, 音乐, 旅游'
    }
    
    // 模拟同乡匹配
    trip.value.hometownMatch = Math.random() > 0.5
    
    // 模拟乘客数据
    passengers.value = [
      {
        id: 1,
        realName: '李四',
        phone: '13800138001',
        avatar: '',
        rating: 4.9,
        status: 'confirmed'
      },
      {
        id: 2,
        realName: '王五',
        phone: '13800138002',
        avatar: '',
        rating: 4.7,
        status: 'pending'
      }
    ]
  } catch (error) {
    console.error('获取行程详情失败:', error)
    ElMessage.error('获取行程详情失败')
  } finally {
    loading.value = false
  }
}

// 加入行程
const handleJoin = async () => {
  try {
    joining.value = true
    await axios.post(`/api/trips/${tripId.value}/join`)
    ElMessage.success('加入行程成功')
    fetchTripDetail() // 刷新数据
  } catch (error: any) {
    if (error.response?.data?.error) {
      ElMessage.error(error.response.data.error)
    } else {
      ElMessage.error('加入失败，请稍后重试')
    }
  } finally {
    joining.value = false
  }
}

// 取消加入
const handleCancel = async () => {
  try {
    cancelling.value = true
    await axios.delete(`/api/trips/${tripId.value}/join`)
    ElMessage.success('已取消加入')
    fetchTripDetail() // 刷新数据
  } catch (error: any) {
    if (error.response?.data?.error) {
      ElMessage.error(error.response.data.error)
    } else {
      ElMessage.error('取消失败，请稍后重试')
    }
  } finally {
    cancelling.value = false
  }
}

// 完成行程
const handleComplete = async () => {
  try {
    completing.value = true
    await axios.put(`/api/trips/${tripId.value}/complete`)
    ElMessage.success('行程已完成')
    fetchTripDetail() // 刷新数据
  } catch (error: any) {
    if (error.response?.data?.error) {
      ElMessage.error(error.response.data.error)
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    completing.value = false
  }
}

// 编辑行程
const handleEdit = () => {
  router.push(`/trip/${tripId.value}/edit`)
}

// 删除行程
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个行程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    deleting.value = true
    await axios.delete(`/api/trips/${tripId.value}`)
    ElMessage.success('行程已删除')
    router.push('/user?tab=trips')
  } catch (error: any) {
    if (error !== 'cancel') {
      if (error.response?.data?.error) {
        ElMessage.error(error.response.data.error)
      } else {
        ElMessage.error('删除失败，请稍后重试')
      }
    }
  } finally {
    deleting.value = false
  }
}

// 分享行程
const handleShare = () => {
  const shareUrl = window.location.href
  if (navigator.share) {
    navigator.share({
      title: '长途拼车 - 行程分享',
      text: `${trip.value.departureCity}到${trip.value.destinationCity}的拼车行程`,
      url: shareUrl
    }).catch(() => {
      // 用户取消分享
    })
  } else {
    // 复制链接
    navigator.clipboard.writeText(shareUrl).then(() => {
      ElMessage.success('链接已复制到剪贴板')
    }).catch(() => {
      ElMessage.error('复制失败，请手动复制链接')
    })
  }
}

// 开始聊天
const startChat = () => {
  ElMessage.info('聊天功能开发中...')
}

onMounted(() => {
  fetchTripDetail()
})
</script>

<style scoped>
.trip-detail-container {
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

.loading-container {
  padding: 40px 20px;
}

.trip-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.trip-info-card {
  margin-bottom: 20px;
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 16px;
}

.trip-type-badge {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.trip-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.section-title {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.route-display {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.route-point {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.point-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.point-info {
  flex: 1;
}

.point-city {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.point-detail {
  color: #666;
  margin-bottom: 4px;
}

.point-district {
  color: #999;
  font-size: 14px;
}

.route-line {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 10px 0;
}

.line-duration {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.line-arrow {
  color: #666;
}

.trip-details-section {
  margin-top: 30px;
}

.detail-item {
  margin-bottom: 20px;
}

.detail-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.detail-value {
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.detail-value.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 600;
}

.trip-description-section {
  margin-top: 30px;
}

.description-content {
  color: #666;
  line-height: 1.8;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.publisher-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.publisher-info {
  display: flex;
  gap: 20px;
}

.publisher-avatar {
  position: relative;
}

.publisher-verification {
  position: absolute;
  bottom: -5px;
  right: -5px;
}

.publisher-details {
  flex: 1;
}

.publisher-name {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.publisher-rating {
  margin-bottom: 16px;
}

.publisher-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  color: #999;
  font-size: 12px;
}

.stat-value {
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.publisher-location {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  margin-bottom: 8px;
}

.publisher-education {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  margin-bottom: 8px;
}

.publisher-interests {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.passengers-card {
  margin-bottom: 20px;
}

.passengers-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.passenger-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.passenger-info {
  flex: 1;
}

.passenger-name {
  color: #333;
  font-weight: 500;
  margin-bottom: 4px;
}

.passenger-rating {
  margin-top: 4px;
}

.empty-passengers {
  text-align: center;
  padding: 40px 20px;
}

.safety-card {
  margin-bottom: 20px;
}

.safety-tips {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.safety-tip {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  color: #666;
  line-height: 1.6;
}

.error-container {
  text-align: center;
  padding: 60px 20px;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-button) {
  border-radius: 6px;
}

@media (max-width: 768px) {
  .trip-detail-container {
    padding: 10px;
  }
  
  .trip-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .trip-actions {
    flex-direction: column;
  }
  
  .trip-actions .el-button {
    width: 100%;
  }
  
  .publisher-info {
    flex-direction: column;
  }
  
  .publisher-stats {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .route-display {
    padding: 16px;
  }
}
</style>
