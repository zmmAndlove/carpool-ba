<template>
  <div class="admin-users">
    <h1>用户管理</h1>
    <div class="users-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>性别</th>
            <th>年龄</th>
            <th>评分</th>
            <th>行程数</th>
            <th>完成行程数</th>
            <th>注册时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.realName }}</td>
            <td>{{ user.gender }}</td>
            <td>{{ user.age }}</td>
            <td>{{ user.rating }}</td>
            <td>{{ user.tripCount }}</td>
            <td>{{ user.completedTrips }}</td>
            <td>{{ formatDate(user.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <button @click="prevPage" :disabled="page === 1">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="page === totalPages">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

const users = ref<any[]>([]);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const totalPages = computed(() => Math.ceil(total.value / size.value));

const fetchUsers = async () => {
  try {
    const response = await axios.get(`/api/admin/users?page=${page.value}&size=${size.value}`);
    users.value = response.data.users;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取用户列表失败:', error);
  }
};

const prevPage = () => {
  if (page.value > 1) {
    page.value--;
    fetchUsers();
  }
};

const nextPage = () => {
  if (page.value < totalPages.value) {
    page.value++;
    fetchUsers();
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString();
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.admin-users {
  padding: 20px;
}

.users-table {
  margin-top: 20px;
  overflow-x: auto;
}

.users-table table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th, .users-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.users-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.users-table tr:hover {
  background-color: #f5f5f5;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
}

.pagination button:hover {
  background-color: #f2f2f2;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
