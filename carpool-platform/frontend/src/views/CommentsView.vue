<template>
  <Layout>
    <div class="comments-container">
      <el-page-header @back="$router.go(-1)" class="page-header">
        <template #content>
          <span class="header-title">用户评论</span>
        </template>
      </el-page-header>

      <el-card class="comments-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3>用户评价</h3>
            <el-button type="primary" @click="handleAddComment" v-if="authStore.isAuthenticated">
              发布评论
            </el-button>
          </div>
        </template>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论" />
          </div>
          <div v-else class="comments">
            <el-card
              v-for="comment in comments"
              :key="comment.id"
              class="comment-item"
              shadow="hover"
            >
              <div class="comment-header">
                <div class="user-info">
                  <el-avatar :src="comment.avatar" :size="40">
                    {{ comment.username?.charAt(0) || '用' }}
                  </el-avatar>
                  <div class="user-details">
                    <div class="username">{{ comment.username }}</div>
                    <div class="comment-time">{{ formatDate(comment.createdAt) }}</div>
                  </div>
                </div>
                <div class="comment-rating">
                  <el-rate
                    v-model="comment.rating"
                    disabled
                    :max="5"
                  />
                </div>
              </div>
              <div class="comment-content">
                {{ comment.content }}
              </div>
              <div class="comment-actions">
                <el-button size="small" @click="handleReply(comment)">
                  回复
                </el-button>
                <el-button size="small" type="text" @click="handleLike(comment.id)">
                  <el-icon><Star /></el-icon>
                  {{ comment.likes }}
                </el-button>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import Layout from '@/components/Layout.vue'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const comments = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 模拟评论数据
const mockComments = [
  {
    id: 1,
    username: '张三',
    avatar: '',
    content: '这个拼车平台真的很棒，让我找到了回家的同伴，节省了不少费用，而且认识了新朋友！',
    rating: 5,
    likes: 12,
    createdAt: new Date('2026-03-20T10:00:00')
  },
  {
    id: 2,
    username: '李四',
    avatar: '',
    content: '车主很准时，车也很干净，一路上聊得很开心，下次还会使用这个平台。',
    rating: 4,
    likes: 8,
    createdAt: new Date('2026-03-19T15:30:00')
  },
  {
    id: 3,
    username: '王五',
    avatar: '',
    content: '平台操作简单，界面友好，客服响应也很及时，推荐给大家！',
    rating: 5,
    likes: 15,
    createdAt: new Date('2026-03-18T09:15:00')
  }
]

const fetchComments = async () => {
  try {
    loading.value = true
    // 实际项目中应该调用API
    // const response = await axios.get('/api/comments', {
    //   params: {
    //     page: currentPage.value,
    //     size: pageSize.value
    //   }
    // })
    // comments.value = response.data.comments
    // total.value = response.data.total
    
    // 使用模拟数据
    comments.value = mockComments
    total.value = mockComments.length
  } catch (error) {
    console.error('获取评论失败:', error)
    ElMessage.error('获取评论失败')
  } finally {
    loading.value = false
  }
}

const handleAddComment = () => {
  ElMessageBox.prompt('请输入您的评论:', '发布评论', {
    confirmButtonText: '发布',
    cancelButtonText: '取消',
    inputPlaceholder: '分享您的拼车体验...',
    inputType: 'textarea',
    inputValidator: (value: string) => {
      if (!value || value.trim().length < 5) {
        return '评论内容至少5个字符'
      }
      return true
    }
  }).then(async ({ value }) => {
    try {
      // 实际项目中应该调用API
      // await axios.post('/api/comments', {
      //   content: value,
      //   rating: 5 // 可以添加评分选择
      // })
      
      // 模拟添加评论
      const newComment = {
        id: Date.now(),
        username: authStore.user?.username || '用户',
        avatar: authStore.user?.avatar || '',
        content: value,
        rating: 5,
        likes: 0,
        createdAt: new Date()
      }
      comments.value.unshift(newComment)
      total.value++
      
      ElMessage.success('评论发布成功')
    } catch (error) {
      ElMessage.error('发布评论失败')
    }
  }).catch(() => {
    // 取消发布
  })
}

const handleReply = (comment: any) => {
  ElMessage.info('回复功能开发中...')
}

const handleLike = (commentId: number) => {
  // 实际项目中应该调用API
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likes++
    ElMessage.success('点赞成功')
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchComments()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchComments()
}

const formatDate = (date: Date) => {
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comments-container {
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;
}

.header-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.comments-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.comments-list {
  margin: 20px 0;
}

.loading-container {
  margin: 20px 0;
}

.empty-comments {
  padding: 40px 0;
  text-align: center;
}

.comment-item {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s;
}

.comment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-rating {
  margin-left: auto;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 12px;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .comments-card {
    margin: 0 15px;
  }
  
  .header-title {
    font-size: 20px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .comment-rating {
    margin-left: 0;
    align-self: flex-start;
  }
}
</style>