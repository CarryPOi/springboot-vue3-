<template>
  <div class="course-management">
    <el-card class="user-table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">健身课程列表</span>
          </div>
          <div class="header-right">
            <el-button type="success" :icon="Plus" @click="handleAddCourse">新增课程</el-button>
            <el-button type="primary" :icon="Refresh" @click="fetchCourses" circle></el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="courseList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="课程名称" min-width="150" />
        <el-table-column prop="trainer" label="教练" width="120" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.difficulty === '高级' ? 'danger' : 'success'">
              {{ scope.row.difficulty }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="scope">{{ scope.row.duration }}min</template>
        </el-table-column>
        <el-table-column prop="subscriptionCount" label="订阅人数" width="100">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.subscriptionCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-tooltip content="查看订阅者" placement="top">
              <el-button size="small" :icon="User" circle @click="handleViewSubscribers(scope.row)"></el-button>
            </el-tooltip>
            <el-button size="small" :icon="Edit" circle @click="handleEditCourse(scope.row)"></el-button>
            <el-popconfirm title="确定删除该课程吗？" @confirm="handleDeleteCourse(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger" :icon="Delete" circle></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 订阅者详情弹窗 -->
    <el-dialog v-model="subscribersDialogVisible" :title="'课程订阅者 - ' + selectedCourseTitle" width="600px">
      <el-table :data="subscriberList" stripe v-loading="subLoading">
        <el-table-column label="头像" width="70">
          <template #default="scope">
            <el-avatar :size="32" :src="getFullUrl(scope.row.avatarUrl)">
              {{ (scope.row.realName || scope.row.username).charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="subscribeTime" label="订阅时间">
          <template #default="scope">
            {{ formatTime(scope.row.subscribeTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 课程编辑弹窗 -->
    <el-dialog v-model="courseDialogVisible" :title="courseForm.id ? '编辑课程' : '新增健身课程'" width="600px">
      <el-form :model="courseForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称">
              <el-input v-model="courseForm.title" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教练名称">
              <el-input v-model="courseForm.trainer" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="courseForm.category" style="width: 100%">
                <el-option label="全身训练" value="全身训练" />
                <el-option label="核心力量" value="核心力量" />
                <el-option label="增肌塑形" value="增肌塑形" />
                <el-option label="瑜伽拉伸" value="瑜伽拉伸" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度">
              <el-radio-group v-model="courseForm.difficulty">
                <el-radio-button label="初级" />
                <el-radio-button label="中级" />
                <el-radio-button label="高级" />
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="时长(min)">
          <el-input-number v-model="courseForm.duration" :min="1" :max="300" />
        </el-form-item>
        <el-form-item label="封面图URL">
          <el-input v-model="courseForm.coverImage" placeholder="请输入图片链接或留空使用默认图" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="courseForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="courseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCourse">保存课程</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminGetCourses, adminSaveCourse, adminDeleteCourse, adminGetCourseSubscribers } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Edit, Delete, User } from '@element-plus/icons-vue'
import { getFullUrl, formatTime } from '@/utils/common'

const loading = ref(false)
const subLoading = ref(false)
const courseList = ref([])
const courseDialogVisible = ref(false)
const subscribersDialogVisible = ref(false)
const subscriberList = ref([])
const selectedCourseTitle = ref('')

const courseForm = ref({
  id: null,
  title: '',
  description: '',
  trainer: '',
  duration: 60,
  difficulty: '初级',
  category: '全身训练',
  coverImage: ''
})

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await adminGetCourses()
    if (res.code === 200) {
      courseList.value = res.data
    }
  } catch (error) {
    console.error('Fetch courses error:', error)
  } finally {
    loading.value = false
  }
}

const handleAddCourse = () => {
  courseForm.value = { 
    id: null, title: '', description: '', trainer: '', 
    duration: 60, difficulty: '初级', category: '全身训练', coverImage: '' 
  }
  courseDialogVisible.value = true
}

const handleEditCourse = (row) => {
  courseForm.value = { ...row }
  courseDialogVisible.value = true
}

const handleViewSubscribers = async (row) => {
  selectedCourseTitle.value = row.title
  subscribersDialogVisible.value = true
  subLoading.value = true
  try {
    const res = await adminGetCourseSubscribers(row.id)
    if (res.code === 200) {
      subscriberList.value = res.data
    }
  } catch (error) {
    console.error('Fetch subscribers error:', error)
    ElMessage.error('获取订阅者列表失败')
  } finally {
    subLoading.value = false
  }
}

const submitCourse = async () => {
  try {
    const res = await adminSaveCourse(courseForm.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      courseDialogVisible.value = false
      fetchCourses()
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleDeleteCourse = async (id) => {
  try {
    const res = await adminDeleteCourse(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchCourses()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchCourses()
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