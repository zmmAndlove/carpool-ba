<template>
  <div class="admin-trips">
    <h1>行程管理</h1>
    <div class="trips-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户ID</th>
            <th>行程类型</th>
            <th>出发地</th>
            <th>目的地</th>
            <th>出发时间</th>
            <th>座位数</th>
            <th>价格</th>
            <th>状态</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trip in trips" :key="trip.id">
            <td>{{ trip.id }}</td>
            <td>{{ trip.userId }}</td>
            <td>{{ trip.tripType }}</td>
            <td>{{ trip.departureProvince }} {{ trip.departureCity }}</td>
            <td>{{ trip.destinationProvince }} {{ trip.destinationCity }}</td>
            <td>{{ formatDate(trip.departureTime) }}</td>
            <td>{{ trip.seatsAvailable }}/{{ trip.seatsTotal }}</td>
            <td>{{ trip.pricePerPerson }}</td>
            <td>{{ trip.status }}</td>
            <td>{{ formatDate(trip.createdAt) }}</td>
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

const trips = ref<any[]>([]);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const totalPages = computed(() => Math.ceil(total.value / size.value));

const fetchTrips = async () => {
  try {
    const response = await axios.get(`/api/admin/trips?page=${page.value}&size=${size.value}`);
    trips.value = response.data.trips;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取行程列表失败:', error);
  }
};

const prevPage = () => {
  if (page.value > 1) {
    page.value--;
    fetchTrips();
  }
};

const nextPage = () => {
  if (page.value < totalPages.value) {
    page.value++;
    fetchTrips();
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString();
};

onMounted(() => {
  fetchTrips();
});
</script>

<style scoped>
.admin-trips {
  padding: 20px;
}

.trips-table {
  margin-top: 20px;
  overflow-x: auto;
}

.trips-table table {
  width: 100%;
  border-collapse: collapse;
}

.trips-table th, .trips-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.trips-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.trips-table tr:hover {
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
