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
                <el-icon size="24">
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
                <el-icon size="24">
                  <Right />
                </el-icon>
              </div>
            </div>
            
            <div class="route-point destination">
              <div class="point-icon">
                <el-icon size="24">
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
            <el-icon class="safety-icon"><CircleCheck /></el-icon>
            <span>请确认对方身份信息，建议出发前视频通话验证</span>
          </div>
          <div class="safety-tip">
            <el-icon class="safety-icon"><CircleCheck /></el-icon>
            <span>行程中请保持手机畅通，及时与家人朋友分享位置</span>
          </div>
          <div class="safety-tip">
            <el-icon class="safety-icon"><CircleCheck /></el-icon>
            <span>如遇紧急情况，请立即拨打110或联系平台客服</span>
          </div>
          <div class="safety-tip">
            <el-icon class="safety-icon"><CircleCheck /></el-icon>
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

.loading-container {
  padding: var(--space-10) var(--space-5);
}

.trip-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.trip-info-card {
  border: none;
  box-shadow: var(--shadow-card);
  border-radius: var(--radius-lg);
  overflow: visible;
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
  flex-wrap: wrap;
  gap: var(--space-4);
}

.trip-type-badge {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.trip-type-badge :deep(.el-tag) {
  border-radius: 20px;
  padding: 6px 14px;
  font-weight: var(--font-weight-medium);
  border: none;
}

.trip-actions {
  display: flex;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.section-title {
  margin: 0 0 var(--space-5) 0;
  color: var(--text-primary);
  font-size: var(--font-size-lg);
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

.route-display {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
  padding: var(--space-5);
  background: var(--bg-section);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.route-point {
  display: flex;
  gap: var(--space-4);
  align-items: flex-start;
}

.point-icon {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-card);
  border-radius: 50%;
  box-shadow: var(--shadow-card);
  color: var(--brand);
}

.route-point.destination .point-icon {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(16, 185, 129, 0.05) 100%);
  color: var(--success);
}

.point-info {
  flex: 1;
}

.point-city {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.point-detail {
  color: var(--text-secondary);
  margin-bottom: 2px;
}

.point-district {
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

.route-line {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-2) 0;
}

.line-duration {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

.line-arrow {
  color: var(--text-tertiary);
}

.trip-details-section {
  margin-top: var(--space-8);
}

.detail-item {
  margin-bottom: var(--space-5);
}

.detail-label {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
  margin-bottom: var(--space-2);
}

.detail-value {
  color: var(--text-primary);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
}

.detail-value.price {
  color: var(--danger);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
}

.trip-description-section {
  margin-top: var(--space-8);
}

.description-content {
  color: var(--text-secondary);
  line-height: 1.8;
  padding: var(--space-4);
  background: var(--bg-section);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.publisher-card {
  border: none;
  box-shadow: var(--shadow-card);
  border-radius: var(--radius-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
}

.publisher-info {
  display: flex;
  gap: var(--space-5);
}

.publisher-avatar {
  position: relative;
}

.publisher-avatar :deep(.el-avatar) {
  border: 3px solid var(--bg-card);
  box-shadow: var(--shadow-card);
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
  margin: 0 0 var(--space-3) 0;
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.publisher-rating {
  margin-bottom: var(--space-4);
}

.publisher-stats {
  display: flex;
  gap: var(--space-6);
  margin-bottom: var(--space-4);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  color: var(--text-tertiary);
  font-size: var(--font-size-xs);
}

.stat-value {
  color: var(--text-primary);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
}

.publisher-location,
.publisher-education,
.publisher-interests {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.passengers-card {
  border: none;
  box-shadow: var(--shadow-card);
  border-radius: var(--radius-lg);
}

.passengers-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.passenger-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--bg-section);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  transition: var(--transition-fast);
}

.passenger-item:hover {
  box-shadow: var(--shadow-card-hover);
  border-color: transparent;
}

.passenger-info {
  flex: 1;
}

.passenger-name {
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
  margin-bottom: 2px;
}

.passenger-rating {
  margin-top: 2px;
}

.empty-passengers {
  text-align: center;
  padding: var(--space-10) var(--space-5);
}

.safety-card {
  border: none;
  box-shadow: var(--shadow-card);
  border-radius: var(--radius-lg);
}

.safety-card :deep(.el-card__header) {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.06) 0%, rgba(16, 185, 129, 0.02) 100%);
  border-bottom: 1px solid var(--border-color);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.safety-tips {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.safety-tip {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  color: var(--text-secondary);
  line-height: 1.6;
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  background: var(--bg-section);
  transition: var(--transition-fast);
}

.safety-tip:hover {
  background: rgba(16, 185, 129, 0.06);
}

.safety-icon {
  color: var(--success);
  flex-shrink: 0;
  font-size: 18px;
}

.error-container {
  text-align: center;
  padding: var(--space-16) var(--space-5);
}

::deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
}

::deep(.el-tag) {
  border-radius: 20px;
}

@media (max-width: 768px) {
  .trip-detail-container {
    padding: var(--space-3);
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
    gap: var(--space-4);
  }

  .route-display {
    padding: var(--space-4);
  }
}
</style>
