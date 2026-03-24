<template>
  <div class="admin-comments">
    <h1>评论管理</h1>
    <div class="comments-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户ID</th>
            <th>用户名</th>
            <th>内容</th>
            <th>评分</th>
            <th>点赞数</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="comment in comments" :key="comment.id">
            <td>{{ comment.id }}</td>
            <td>{{ comment.userId }}</td>
            <td>{{ comment.username }}</td>
            <td>{{ comment.content }}</td>
            <td>{{ comment.rating }}</td>
            <td>{{ comment.likes }}</td>
            <td>{{ formatDate(comment.createdAt) }}</td>
            <td>
              <button @click="deleteComment(comment.id)" class="delete-btn">删除</button>
            </td>
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

const comments = ref<any[]>([]);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const totalPages = computed(() => Math.ceil(total.value / size.value));

const fetchComments = async () => {
  try {
    const response = await axios.get(`/api/admin/comments?page=${page.value}&size=${size.value}`);
    comments.value = response.data.comments;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取评论列表失败:', error);
  }
};

const prevPage = () => {
  if (page.value > 1) {
    page.value--;
    fetchComments();
  }
};

const nextPage = () => {
  if (page.value < totalPages.value) {
    page.value++;
    fetchComments();
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString();
};

const deleteComment = async (id: number) => {
  try {
    await axios.delete(`/api/admin/comments/${id}`);
    // 重新获取评论列表
    fetchComments();
  } catch (error) {
    console.error('删除评论失败:', error);
  }
};

onMounted(() => {
  fetchComments();
});
</script>

<style scoped>
.admin-comments {
  padding: 20px;
}

.comments-table {
  margin-top: 20px;
  overflow-x: auto;
}

.comments-table table {
  width: 100%;
  border-collapse: collapse;
}

.comments-table th, .comments-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.comments-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.comments-table tr:hover {
  background-color: #f5f5f5;
}

.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  background-color: #dc3545;
  color: white;
  cursor: pointer;
}

.delete-btn:hover {
  background-color: #c82333;
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
