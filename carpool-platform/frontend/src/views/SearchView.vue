<template>
  <Layout>
    <div class="search-container">
      <!-- 搜索栏 -->
      <el-card class="search-bar">
        <el-form :model="searchForm" class="search-form">
          <el-row :gutter="16" align="middle">
            <el-col :xs="12" :sm="6" :md="5">
              <el-form-item>
                <el-input v-model="searchForm.from" placeholder="出发地" :prefix-icon="Location" @keyup.enter="handleSearch" clearable />
              </el-form-item>
            </el-col>
            <el-col :xs="12" :sm="6" :md="5">
              <el-form-item>
                <el-input v-model="searchForm.to" placeholder="目的地" :prefix-icon="Location" @keyup.enter="handleSearch" clearable />
              </el-form-item>
            </el-col>
            <el-col :xs="12" :sm="6" :md="4">
              <el-form-item>
                <el-date-picker v-model="searchForm.date" type="date" placeholder="出发日期" style="width: 100%" @change="handleSearch" />
              </el-form-item>
            </el-col>
            <el-col :xs="12" :sm="6" :md="4">
              <el-form-item>
                <el-select v-model="searchForm.tripType" placeholder="行程类型" style="width: 100%" @change="handleSearch" clearable>
                  <el-option label="全部" value="" />
                  <el-option label="车找人" value="car_owner" />
                  <el-option label="人找车" value="passenger" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="6">
              <div class="search-actions">
                <el-button type="primary" :icon="Search" @click="handleSearch" :loading="loading" round>
                  搜索
                </el-button>
                <el-button @click="resetSearch" round>重置</el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 筛选 -->
      <div class="filter-bar">
        <div class="filter-item">
          <span class="filter-label">排序</span>
          <el-radio-group v-model="sortBy" size="small" @change="handleSearch">
            <el-radio-button label="time">出发时间</el-radio-button>
            <el-radio-button label="price">价格最低</el-radio-button>
            <el-radio-button label="newest">最新发布</el-radio-button>
          </el-radio-group>
        </div>
        <div class="filter-item">
          <span class="filter-label">筛选</span>
          <el-checkbox-group v-model="filters" @change="handleSearch" size="small">
            <el-checkbox-button label="hometown">同乡</el-checkbox-button>
            <el-checkbox-button label="available">有空位</el-checkbox-button>
            <el-checkbox-button label="verified">已认证</el-checkbox-button>
          </el-checkbox-group>
        </div>
      </div>

      <!-- 搜索结果 -->
      <div class="results-container">
        <div class="results-header">
          <h2 v-if="searchForm.from && searchForm.to">{{ searchForm.from }} → {{ searchForm.to }}</h2>
          <h2 v-else>全部行程</h2>
          <span class="results-count">
            共 {{ total }} 条
            <span v-if="searchForm.date" class="date-info">· {{ formatDate(searchForm.date) }}</span>
          </span>
        </div>

        <!-- 列表 -->
        <div v-if="trips.length > 0" class="trips-list">
          <el-row :gutter="16">
            <el-col v-for="trip in trips" :key="trip.id" :xs="24" :sm="12" :lg="8" class="trip-col">
              <el-card class="trip-card" @click="viewTripDetail(trip.id)">
                <div class="trip-card-header">
                  <div class="trip-tags">
                    <el-tag :type="trip.tripType === 'car_owner' ? 'success' : 'warning'" size="small" round>
                      {{ trip.tripType === 'car_owner' ? '车找人' : '人找车' }}
                    </el-tag>
                    <el-tag v-if="trip.hometownMatch" type="primary" size="small" effect="plain" round>同乡</el-tag>
                  </div>
                  <div class="trip-time">
                    <el-icon size="14"><Clock /></el-icon>
                    {{ formatTime(trip.departureTime) }}
                  </div>
                </div>

                <div class="trip-route">
                  <div class="route-point">
                    <span class="city">{{ trip.departureCity }}</span>
                    <span class="detail">{{ trip.departureDetail || '详细地址' }}</span>
                  </div>
                  <div class="route-arrow">
                    <el-icon><Right /></el-icon>
                  </div>
                  <div class="route-point">
                    <span class="city">{{ trip.destinationCity }}</span>
                    <span class="detail">{{ trip.destinationDetail || '详细地址' }}</span>
                  </div>
                </div>

                <div class="trip-meta">
                  <div class="meta-item">
                    <el-icon size="14"><User /></el-icon>
                    <span>{{ trip.seatsAvailable }}/{{ trip.seatsTotal }}空位</span>
                  </div>
                  <div class="meta-item price" v-if="trip.pricePerPerson">
                    <el-icon size="14"><Money /></el-icon>
                    <span>{{ trip.pricePerPerson }}元/人</span>
                  </div>
                </div>

                <div class="trip-publisher">
                  <el-avatar :src="trip.publisher?.avatar" :size="28">{{ trip.publisher?.realName?.charAt(0) || '用户' }}</el-avatar>
                  <div class="publisher-detail">
                    <span class="publisher-name">{{ trip.publisher?.realName || trip.publisher?.phone }}</span>
                    <el-rate :model-value="trip.publisher?.rating || 0" disabled :size="14" />
                  </div>
                </div>

                <div class="trip-card-actions">
                  <el-button type="primary" size="small" @click.stop="joinTrip(trip)" :disabled="trip.seatsAvailable <= 0 || trip.status !== 'pending'" round>
                    {{ trip.seatsAvailable > 0 ? '加入行程' : '已满员' }}
                  </el-button>
                  <el-button size="small" @click.stop="contactPublisher(trip)" round plain>联系车主</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              background
            />
          </div>
        </div>

        <div v-else-if="!loading" class="empty-state">
          <el-empty description="暂无匹配行程">
            <el-button type="primary" @click="$router.push('/publish')" round>发布新行程</el-button>
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
import { Search, Location, Clock, Right, User, Money } from '@element-plus/icons-vue'
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

const initFromQuery = () => {
  const query = route.query
  if (query.from) searchForm.from = query.from as string
  if (query.to) searchForm.to = query.to as string
  if (query.date) searchForm.date = new Date(query.date as string)
  if (query.tripType) searchForm.tripType = query.tripType as string
}

const handleSearch = async () => {
  try {
    loading.value = true
    const params: any = { page: currentPage.value, size: pageSize.value }
    if (searchForm.from) params.departureCity = searchForm.from
    if (searchForm.to) params.destinationCity = searchForm.to
    if (searchForm.date) {
      const date = dayjs(searchForm.date)
      params.startTime = date.startOf('day').toISOString()
      params.endTime = date.endOf('day').toISOString()
    }
    if (searchForm.tripType) params.tripType = searchForm.tripType

    const response = await axios.get('/api/trips/search', { params })
    trips.value = response.data.trips.map((trip: any) => ({
      ...trip,
      hometownMatch: Math.random() > 0.5,
      publisher: { id: trip.userId, realName: '测试用户', avatar: '', rating: 4.8, completedTrips: 15 }
    }))
    total.value = response.data.total
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.from = ''
  searchForm.to = ''
  searchForm.date = null
  searchForm.tripType = ''
  currentPage.value = 1
  handleSearch()
}

const handleSizeChange = (size: number) => { pageSize.value = size; currentPage.value = 1; handleSearch() }
const handleCurrentChange = (page: number) => { currentPage.value = page; handleSearch() }
const viewTripDetail = (tripId: number) => { router.push(`/trip/${tripId}`) }

const joinTrip = async (trip: any) => {
  try {
    await axios.post(`/api/trips/${trip.id}/join`)
    ElMessage.success('加入行程成功')
    handleSearch()
  } catch (error: any) {
    if (error.response?.data?.error) ElMessage.error(error.response.data.error)
    else ElMessage.error('加入失败，请稍后重试')
  }
}

const contactPublisher = (trip: any) => { ElMessage.info('联系功能开发中') }
const formatTime = (time: string) => dayjs(time).format('MM月DD日 HH:mm')
const formatDate = (date: Date) => dayjs(date).format('YYYY年MM月DD日')

watch(() => route.query, () => { initFromQuery(); handleSearch() })
onMounted(() => { initFromQuery(); handleSearch() })
</script>

<style scoped>
.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
}

.search-bar {
  margin-bottom: var(--space-4);
  border-radius: var(--radius-lg);
}

.search-form {
  margin-top: var(--space-2);
}

.search-actions {
  display: flex;
  gap: var(--space-2);
  align-items: center;
  height: 100%;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
  padding: var(--space-4) var(--space-5);
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
}

.filter-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.filter-label {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-weight-medium);
}

.results-container {
  margin-top: var(--space-2);
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: var(--space-6);
}

.results-header h2 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
}

.results-count {
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

.date-info {
  color: var(--brand);
}

.trips-list {
  margin-bottom: var(--space-8);
}

.trip-col {
  margin-bottom: var(--space-4);
}

.trip-card {
  height: 100%;
  cursor: pointer;
  border-radius: var(--radius-lg);
  transition: all var(--transition-normal);
}

.trip-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.trip-tags {
  display: flex;
  gap: 6px;
}

.trip-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--text-tertiary);
  font-size: var(--font-size-xs);
}

.trip-route {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-section);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-3);
}

.route-point {
  flex: 1;
  min-width: 0;
}

.route-point .city {
  display: block;
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  font-size: var(--font-size-md);
  margin-bottom: 2px;
}

.route-point .detail {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.route-arrow {
  color: var(--brand);
  flex-shrink: 0;
}

.trip-meta {
  display: flex;
  gap: var(--space-4);
  margin-bottom: var(--space-3);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.meta-item.price {
  color: var(--danger);
  font-weight: var(--font-weight-semibold);
}

.trip-publisher {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-light);
  margin-bottom: var(--space-3);
}

.publisher-detail {
  display: flex;
  flex-direction: column;
}

.publisher-name {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.trip-card-actions {
  display: flex;
  gap: var(--space-2);
}

.trip-card-actions .el-button {
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: var(--space-6) 0;
}

.empty-state {
  padding: 60px 0;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    gap: var(--space-3);
    padding: var(--space-3) var(--space-4);
  }
  
  .filter-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }
  
  .results-header {
    flex-direction: column;
    gap: var(--space-1);
  }
  
  .search-actions {
    flex-direction: column;
  }
  
  .search-actions .el-button {
    width: 100%;
  }
  
  .trip-card-actions {
    flex-direction: column;
  }
}
</style>
