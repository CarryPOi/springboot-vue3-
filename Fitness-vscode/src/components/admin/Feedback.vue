<template>
  <div class="user-feedback">
    <el-card class="user-table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">用户反馈管理</span>
          </div>
          <el-button type="primary" :icon="Refresh" @click="fetchFeedback" circle></el-button>
        </div>
      </template>
      
      <el-table :data="feedbackList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="反馈用户" width="150">
          <template #default="scope">
            <span>{{ scope.row.realName || scope.row.username }}</span>
            <el-tag v-if="scope.row.realName" size="small" type="info" style="margin-left: 5px">{{ scope.row.username }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="反馈类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'Bug' ? 'danger' : 'primary'">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已处理' ? 'success' : 'warning'">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === '待处理'"
              size="small" 
              type="primary" 
              plain
              @click="handleProcess(scope.row)"
            >处理</el-button>
            <el-button 
              v-else
              size="small" 
              type="info" 
              disabled
            >已处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import axios from 'axios'
import { BASE_API_URL } from '@/utils/common'

const loading = ref(false)
const feedbackList = ref([])

const fetchFeedback = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${BASE_API_URL}/api/feedback/list`)
    if (response.data.code === 200 || response.data.code === 0) {
      feedbackList.value = response.data.data
    } else {
      ElMessage.error(response.data.msg || '获取数据失败')
    }
  } catch (error) {
    console.error('Fetch feedback error:', error)
    ElMessage.error('无法连接到服务器')
  } finally {
    loading.value = false
  }
}

const handleProcess = async (row) => {
  try {
    const response = await axios.put(`${BASE_API_URL}/api/feedback/process/${row.id}`)
    if (response.data.code === 200 || response.data.code === 0) {
      ElMessage.success(`已成功处理用户 ${row.username} 的反馈`)
      fetchFeedback() // 刷新列表
    } else {
      ElMessage.error(response.data.msg || '处理失败')
    }
  } catch (error) {
    console.error('Process feedback error:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

onMounted(() => {
  fetchFeedback()
})
</script>

<style scoped>
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
}

.header-title {
  font-weight: 600;
  font-size: 16px;
}
</style>