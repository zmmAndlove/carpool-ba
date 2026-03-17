<template>
  <div class="user-center">
    <h1>个人中心</h1>
    <div class="user-info">
      <el-avatar :size="80">
        {{ user?.username?.charAt(0) || 'U' }}
      </el-avatar>
      <h2>{{ user?.username || '未登录' }}</h2>
      <p>{{ user?.phone || '' }}</p>
    </div>
    <el-tabs>
      <el-tab-pane label="我的行程">
        <div class="trip-list">
          <el-empty v-if="trips.length === 0" description="暂无行程" />
          <el-card v-for="trip in trips" :key="trip.id" class="trip-card">
            <template #header>
              <div class="trip-header">
                <h3>{{ trip.title }}</h3>
                <el-tag :type="trip.type === 'CAR' ? 'success' : 'primary'">
                  {{ trip.type === 'CAR' ? '车找人' : '人找车' }}
                </el-tag>
              </div>
            </template>
            <div class="trip-info">
              <p><el-icon><Location /></el-icon> {{ trip.startPlace }} → {{ trip.endPlace }}</p>
              <p><el-icon><Timer /></el-icon> {{ trip.departureTime }}</p>
              <p><el-icon><Money /></el-icon> ¥{{ trip.price }}</p>
            </div>
            <div class="trip-actions">
              <el-button type="primary" size="small">查看详情</el-button>
              <el-button size="small">编辑</el-button>
              <el-button type="danger" size="small">删除</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的收藏">
        <el-empty description="暂无收藏" />
      </el-tab-pane>
      <el-tab-pane label="设置">
        <el-form :model="userForm">
          <el-form-item label="用户名">
            <el-input v-model="userForm.username" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userForm.phone" disabled />
          </el-form-item>
          <el-form-item label="密码">
            <el-input type="password" placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveSettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    <el-button type="danger" @click="logout" class="logout-btn">退出登录</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Timer, Money } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const user = ref({
  username: '测试用户',
  phone: '13800138001'
})

const userForm = ref({
  username: user.value.username,
  phone: user.value.phone
})

const trips = ref([
  {
    id: 1,
    title: '北京到上海',
    type: 'CAR',
    startPlace: '北京市朝阳区',
    endPlace: '上海市浦东新区',
    departureTime: '2024-01-20 08:00',
    price: 300
  },
  {
    id: 2,
    title: '上海到杭州',
    type: 'PERSON',
    startPlace: '上海市浦东新区',
    endPlace: '杭州市西湖区',
    departureTime: '2024-01-21 10:00',
    price: 100
  }
])

const saveSettings = () => {
  ElMessage.success('设置保存成功')
}

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.user-center {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.user-info {
  text-align: center;
  margin-bottom: 30px;
}

.trip-list {
  margin-top: 20px;
}

.trip-card {
  margin-bottom: 20px;
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trip-info {
  margin: 15px 0;
}

.trip-info p {
  margin: 5px 0;
}

.trip-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

.logout-btn {
  margin-top: 30px;
}
</style>