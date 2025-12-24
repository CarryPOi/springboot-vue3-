<template>
  <div class="notice-management">
    <el-card class="user-table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">系统公告列表</span>
          </div>
          <div class="header-right">
            <el-button type="success" :icon="Plus" @click="handleAddNotice">新增公告</el-button>
            <el-button type="primary" :icon="Refresh" @click="fetchNotices" circle></el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="noticeList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="['info', 'warning', 'danger'][scope.row.type] || 'info'">
              {{ ['系统公告', '活动通知', '维护通知'][scope.row.type] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="发布者" width="120" />
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button size="small" :icon="Edit" circle @click="handleEditNotice(scope.row)"></el-button>
            <el-popconfirm title="确定删除该公告吗？" @confirm="handleDeleteNotice(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger" :icon="Delete" circle></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 公告编辑弹窗 -->
    <el-dialog v-model="noticeDialogVisible" :title="noticeForm.id ? '编辑公告' : '发布新公告'" width="500px">
      <el-form :model="noticeForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="noticeForm.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="系统公告" :value="0" />
            <el-option label="活动通知" :value="1" />
            <el-option label="维护通知" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="noticeForm.content" type="textarea" :rows="5" placeholder="请输入详细内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noticeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNotice">确定发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { adminGetNotices, adminSaveNotice, adminDeleteNotice } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { useUserInfo } from '@/utils/storage'
import { formatTime } from '@/utils/common'

const loading = ref(false)
const noticeList = ref([])
const noticeDialogVisible = ref(false)
const noticeForm = ref({
  id: null,
  title: '',
  content: '',
  type: 0,
  status: 1,
  author: ''
})

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || 'Admin'
})

const fetchNotices = async () => {
  loading.value = true
  try {
    const res = await adminGetNotices()
    if (res.code === 200) {
      noticeList.value = res.data
    }
  } catch (error) {
    console.error('Fetch notices error:', error)
  } finally {
    loading.value = false
  }
}

const handleAddNotice = () => {
  noticeForm.value = { id: null, title: '', content: '', type: 0, status: 1, author: displayName.value }
  noticeDialogVisible.value = true
}

const handleEditNotice = (row) => {
  noticeForm.value = { ...row }
  noticeDialogVisible.value = true
}

const submitNotice = async () => {
  try {
    const res = await adminSaveNotice(noticeForm.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      noticeDialogVisible.value = false
      fetchNotices()
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleDeleteNotice = async (id) => {
  try {
    const res = await adminDeleteNotice(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchNotices()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchNotices()
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
