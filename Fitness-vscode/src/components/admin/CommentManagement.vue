<template>
  <div class="comment-management">
    <el-card class="user-table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">社区评论审核</span>
            <el-tag size="small" type="info" class="user-count">共 {{ commentList.length }} 条评论</el-tag>
          </div>
          <el-button type="primary" :icon="Refresh" @click="fetchComments">刷新数据</el-button>
        </div>
      </template>
      
      <el-table :data="commentList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="评论用户" width="150">
          <template #default="scope">
            <span>{{ scope.row.realName || scope.row.username }}</span>
            <el-tag v-if="scope.row.realName" size="small" type="info" style="margin-left: 5px">{{ scope.row.username }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="评论时间" width="180" sortable>
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="scope">
            <el-popconfirm title="确定要删除这条不当评论吗？" @confirm="handleDeleteComment(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger" plain>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="commentList.length"
          :page-size="10"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCommentList, deleteComment } from '@/api/comment'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { useUserInfo } from '@/utils/storage'

const commentList = ref([])
const loading = ref(false)

const userInfo = useUserInfo()

const fetchComments = async () => {
  loading.value = true
  try {
    console.log('Fetching comments...')
    const res = await getCommentList()
    console.log('Comments response:', res)
    if (res.code === 200 || res.code === 0) {
      commentList.value = res.data || []
    }
  } catch (error) {
    console.error('Fetch comments error:', error)
    ElMessage.error('获取评论失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteComment = async (id) => {
  try {
    const res = await deleteComment(id, userInfo.value.id)
    if (res.code === 200 || res.code === 0) {
      ElMessage.success('评论已删除')
      fetchComments()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const formatDate = (time) => {
  if (!time) return '-'
  
  let date
  if (Array.isArray(time)) {
    // 处理 LocalDateTime 序列化为数组的情况
    date = new Date(time[0], time[1] - 1, time[2], time[3] || 0, time[4] || 0, time[5] || 0)
  } else {
    date = new Date(time)
  }
  
  if (isNaN(date.getTime())) return '时间错误'
  
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comment-management {
  padding: 20px;
}
.user-table-card {
  border-radius: 12px;
  border: none;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-title {
  font-weight: 600;
  font-size: 16px;
}
</style>
