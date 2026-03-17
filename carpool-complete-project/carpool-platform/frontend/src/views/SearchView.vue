<template>
  <Layout>
    <div class="search-container">
      <!-- 搜索栏 -->
      <el-card class="search-bar" shadow="hover">
        <el-form :model="searchForm" class="search-form">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="10" :md="5">
              <el-form-item>
                <el-input v-model="searchForm.from" placeholder="出发地" :prefix-icon="Location" @keyup.enter="handleSearch" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="10" :md="5">
              <el-form-item>
                <el-input v-model="searchForm.to" placeholder="目的地" :prefix-icon="Location" @keyup.enter="handleSearch" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="4">
              <el-form-item>
                <el-date-picker v-model="searchForm.date" type="date" placeholder="出发日期" style="width: 100%" @change="handleSearch" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="4">
              <el-form-item>
                <el-select v-model="searchForm.tripType" placeholder="行程类型" style="width: 100%" @change="handleSearch">
                  <el-option label="全部" value="" />
                  <el-option label="车找人" value="car_owner" />
                  <el-option label="人找车" value="passenger" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="6">
              <div class="search-actions">
                <el-button type="primary" :icon="Search" @click="handleSearch" :loading="loading" class="search-button">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 筛选条件 -->
      <el-card class="filter-card" shadow="hover">
        <div class="filter-content">
          <div class="filter-item">
            <span class="filter-label">排序：</span>
            <el-radio-group v-model="sortBy" @change="handleSearch">
              <el-radio label="time">出发时间</el-radio>
              <el-radio label="price">价格最低</el-radio>
              <el-radio label="newest">最新发布</el-radio>
            </el-radio-group>
          </div>
          <div class="filter-item">
            <span class="filter-label">筛选：</span>
            <el-checkbox-group v-model="filters" @change="handleSearch">
              <el-checkbox label="hometown">仅显示同乡</el-checkbox>
              <el-checkbox label="available">仅显示有空位</el-checkbox>
              <el-checkbox label="verified">仅显示已认证</el-checkbox>
            </el-checkbox-group>
          </div>
        </div>
      </el-card>

      <!-- 搜索结果 -->
      <div class="results-container">
        <!-- 搜索结果统计 -->
        <div class="results-header">
          <h2 v-if="searchForm.from && searchForm.to">{{ searchForm.from }} → {{ searchForm.to }}</h2>
          <h2 v-else>全部行程</h2>
          <div class="results-count">
            共找到 {{ total }} 条行程
            <span v-if="searchForm.date" class="date-info">· {{ formatDate(searchForm.date) }}</span>
          </div>
        </div>

        <!-- 行程列表 -->
        <div v-if="trips.length > 0" class="trips-list">
          <el-row :gutter="20">
            <el-col v-for="trip in trips" :key="trip.id" :xs="24" :sm="12" :lg="8" class="trip-col">
              <el-card class="trip-card" shadow="hover" @click="viewTripDetail(trip.id)">
                <!-- 行程头部 -->
                <div class="trip-header">
                  <div class="trip-type">
                    <el-tag :type="trip.tripType === 'car_owner' ? 'success' : 'warning'" size="small">{{ trip.tripType === 'car_owner' ? '车找人' : '人找车' }}</el-tag>
                    <el-tag v-if="trip.hometownMatch" type="primary" size="small">同乡</el-tag>
                  </div>
                  <div class="trip-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatTime(trip.departureTime) }}
                  </div>
                </div>

                <!-- 行程路线 -->
                <div class="trip-route">
                  <div class="route-from">
                    <div class="city">{{ trip.departureCity }}</div>
                    <div class="detail">{{ trip.departureDetail || '详细地址' }}</div>
                  </div>
                  <div class="route-arrow">
                    <el-icon><Right /></el-icon>
                  </div>
                  <div class="route-to">
                    <div class="city">{{ trip.destinationCity }}</div>
                    <div class="detail">{{ trip.destinationDetail || '详细地址' }}</div>
                  </div>
                </div>

                <!-- 行程信息 -->
                <div class="trip-info">
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>空位：{{ trip.seatsAvailable }}/{{ trip.seatsTotal }}</span>
                  </div>
                  <div class="info-item" v-if="trip.pricePerPerson">
                    <el-icon><Money /></el-icon>
                    <span>{{ trip.pricePerPerson }}元/人</span>
                  </div>
                  <div class="info-item">
                    <el-icon><View /></el-icon>
                    <span>{{ trip.viewCount }}次查看</span>
                  </div>
                </div>

                <!-- 发布者信息 -->
                <div class="trip-publisher">
                  <el-avatar :src="trip.publisher?.avatar" :size="32">{{ trip.publisher?.realName?.charAt(0) || '用户' }}</el-avatar>
                  <div class="publisher-info">
                    <div class="publisher-name">
                      {{ trip.publisher?.realName || trip.publisher?.phone }}
                      <el-rate :model-value="trip.publisher?.rating || 0" disabled show-score text-color="#ff9900" score-template="{value}" class="publisher-rating" />
                    </div>
                    <div class="publisher-stats">
                      已完成 {{ trip.publisher?.completedTrips || 0 }} 次行程
                    </div>
                  </div>
                </div>

                <!-- 操作按钮 -->
                <div class="trip-actions">
                  <el-button type="primary" size="small" @click.stop="joinTrip(trip)" :disabled="trip.seatsAvailable <= 0 || trip.status !== 'pending'">
                    {{ trip.seatsAvailable > 0 ? '加入行程' : '已满员' }}
                  </el-button>
                  <el-button type="info" size="small" @click.stop="contactPublisher(trip)">联系车主</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <el-empty description="暂无行程">
            <template #image>
              <el-icon size="100" color="#909399"><Search /></el-icon>
            </template>
            <div class="empty-tips">
              <p>尝试调整搜索条件或</p>
              <el-button type="primary" @click="$router.push('/publish')">发布新行程</el-button>
            </div>
          </el-empty>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Location, Clock, Right, User, Money, View } from '@element-plus/icons-vue'
import Layout from '@/components/Layout.vue'
import axios from 'axios'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const trips = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const sortBy = ref('time')
const filters = ref<string[]>([])

const searchForm = reactive({
  from: '',
  to: '',
  date: null as Date | null,
  tripType: ''
})

// 从URL参数初始化搜索条件
const initFromQuery = () => {
  const query = route.query
  if (query.from) searchForm.from = query.from as string
  if (query.to) searchForm.to = query.to as string
  if (query.date) {
    searchForm.date = new Date(query.date as string)
  }
  if (query.tripType) searchForm.tripType = query.tripType as string
}

// 搜索行程
const handleSearch = async () => {
  try {
    loading.value = true
    
    const params: any = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    if (searchForm.from) params.departureCity = searchForm.from
    if (searchForm.to) params.destinationCity = searchForm.to
    if (searchForm.date) {
      const date = dayjs(searchForm.date)
      params.startTime = date.startOf('day').toISOString()
      params.endTime = date.endOf('day').toISOString()
    }
    if (searchForm.tripType) params.tripType = searchForm.tripType
    
    // 排序参数
    if (sortBy.value === 'price') {
      // 后端需要支持价格排序
    } else if (sortBy.value === 'newest') {
      // 后端需要支持最新发布排序
    }
    
    const response = await axios.get('/api/trips/search', { params })
    
    trips.value = response.data.trips.map((trip: any) => ({
      ...trip,
      hometownMatch: Math.random() > 0.5, // 模拟同乡匹配
      publisher: {
        id: trip.userId,
        realName: '测试用户',
        avatar: '',
        rating: 4.8,
        completedTrips: 15
      }
    }))
    
    total.value = response.data.total
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.from = ''
  searchForm.to = ''
  searchForm.date = null
  searchForm.tripType = ''
  currentPage.value = 1
  handleSearch()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  handleSearch()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  handleSearch()
}

// 查看行程详情
const viewTripDetail = (tripId: number) => {
  router.push(`/trip/${tripId}`)
}

// 加入行程
const joinTrip = async (trip: any) => {
  try {
    const response = await axios.post(`/api/trips/${trip.id}/join`)
    ElMessage.success('加入行程成功')
    // 刷新行程列表
    handleSearch()
  } catch (error: any) {
    if (error.response?.data?.error) {
      ElMessage.error(error.response.data.error)
    } else {
      ElMessage.error('加入失败，请稍后重试')
    }
  }
}

// 联系发布者
const contactPublisher = (trip: any) => {
  ElMessage.info('联系功能开发中')
}

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('MM月DD日 HH:mm')
}

// 格式化日期
const formatDate = (date: Date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

// 监听路由参数变化
watch(
  () => route.query,
  () => {
    initFromQuery()
    handleSearch()
  }
)

// 初始化
onMounted(() => {
  initFromQuery()
  handleSearch()
})
</script>

<style scoped>
.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.search-form {
  margin-top: 10px;
}

.search-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  height: 100%;
}

.search-button {
  flex: 1;
}

.filter-card {
  margin-bottom: 30px;
}

.filter-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.results-container {
  margin-top: 20px;
}

.results-header {
  margin-bottom: 30px;
}

.results-header h2 {
  margin: 0 0 10px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.results-count {
  color: #666;
  font-size: 14px;
}

.date-info {
  color: #007AFF;
}

.trips-list {
  margin-bottom: 40px;
}

.trip-col {
  margin-bottom: 20px;
}

.trip-card {
  height: 100%;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.trip-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.trip-type {
  display: flex;
  gap: 8px;
}

.trip-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
}

.trip-route {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.route-from,
.route-to {
  flex: 1;
}

.city {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.detail {
  font-size: 12px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.route-arrow {
  padding: 0 12px;
  color: #007AFF;
}

.trip-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.trip-publisher {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.publisher-info {
  flex: 1;
}

.publisher-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  font-weight: 500;
  color: #333;
}

.publisher-rating {
  display: inline-block;
}

.publisher-stats {
  font-size: 12px;
  color: #666;
}

.trip-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.empty-state {
  padding: 60px 0;
}

.empty-tips {
  text-align: center;
  margin-top: 20px;
}

.empty-tips p {
  margin-bottom: 20px;
  color: #666;
}

/* 响应式 */
@media (max-width: 768px) {
  .search-container {
    padding: 10px;
  }
  
  .search-bar {
    margin-bottom: 20px;
    border-radius: 16px;
  }
  
  .search-header {
    font-size: 16px;
  }
  
  .search-form :deep(.el-form-item) {
    margin-bottom: 12px;
  }
  
  .filter-card {
    margin-bottom: 20px;
    border-radius: 16px;
  }
  
  .filter-header {
    font-size: 16px;
  }
  
  .filter-content {
    padding: 15px;
  }
  
  .filter-group {
    margin-bottom: 15px;
  }
  
  .filter-label {
    font-size: 14px;
    margin-bottom: 10px;
  }
  
  .results-header {
    margin-bottom: 20px;
  }
  
  .results-title {
    font-size: 20px;
  }
  
  .trip-col {
    margin-bottom: 15px;
  }
  
  .trip-card {
    border-radius: 12px;
  }
  
  .trip-header {
    margin-bottom: 12px;
  }
  
  .trip-route {
    padding: 10px;
    margin-bottom: 12px;
  }
  
  .city {
    font-size: 16px;
  }
  
  .detail {
    font-size: 11px;
  }
  
  .trip-info {
    padding: 10px 0;
    margin-bottom: 12px;
  }
  
  .info-item {
    font-size: 11px;
  }
  
  .trip-publisher {
    margin-bottom: 12px;
  }
  
  .publisher-name {
    font-size: 14px;
  }
  
  .publisher-stats {
    font-size: 11px;
  }
  
  .pagination-container {
    margin-top: 30px;
  }
  
  .empty-state {
    padding: 40px 0;
  }
}

@media (max-width: 480px) {
  .search-container {
    padding: 8px;
  }
  
  .search-bar {
    margin-bottom: 15px;
    border-radius: 14px;
  }
  
  .search-header {
    font-size: 15px;
    gap: 6px;
  }
  
  .search-form :deep(.el-row) {
    margin: 0 !important;
  }
  
  .search-form :deep(.el-col) {
    padding: 0 5px !important;
  }
  
  .search-form :deep(.el-form-item) {
    margin-bottom: 10px;
  }
  
  .filter-card {
    margin-bottom: 15px;
    border-radius: 14px;
  }
  
  .filter-header {
    font-size: 15px;
  }
  
  .filter-content {
    padding: 12px;
  }
  
  .filter-group {
    margin-bottom: 12px;
  }
  
  .filter-label {
    font-size: 13px;
    margin-bottom: 8px;
  }
  
  .results-header {
    margin-bottom: 15px;
  }
  
  .results-title {
    font-size: 18px;
  }
  
  .results-count {
    font-size: 13px;
  }
  
  .trip-col {
    margin-bottom: 12px;
  }
  
  .trip-card {
    border-radius: 10px;
  }
  
  .trip-header {
    margin-bottom: 10px;
  }
  
  .trip-type :deep(.el-tag) {
    font-size: 11px;
    padding: 2px 6px;
  }
  
  .trip-route {
    padding: 8px;
    margin-bottom: 10px;
  }
  
  .city {
    font-size: 15px;
  }
  
  .detail {
    font-size: 10px;
  }
  
  .route-arrow {
    padding: 0 8px;
  }
  
  .trip-info {
    padding: 8px 0;
    margin-bottom: 10px;
  }
  
  .info-item {
    font-size: 10px;
    gap: 3px;
  }
  
  .trip-publisher {
    margin-bottom: 10px;
    gap: 8px;
  }
  
  .publisher-name {
    font-size: 13px;
  }
  
  .publisher-stats {
    font-size: 10px;
  }
  
  .trip-actions {
    flex-direction: column;
  }
  
  .trip-actions :deep(.el-button) {
    width: 100%;
  }
  
  .pagination-container {
    margin-top: 20px;
  }
  
  .empty-state {
    padding: 30px 0;
  }
  
  .empty-tips p {
    font-size: 14px;
  }
}
</style>