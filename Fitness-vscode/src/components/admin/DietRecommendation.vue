<template>
  <div class="diet-recommendation">
    <el-card class="user-table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">膳食推荐发布</span>
          </div>
          <div class="header-right">
            <el-button type="success" :icon="Plus" @click="handleAddRecommendation">发布推荐</el-button>
            <el-button type="primary" :icon="Refresh" @click="fetchRecommendations" circle></el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="recommendationList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="推荐标题" min-width="150" />
        <el-table-column prop="mealType" label="餐别" width="100" />
        <el-table-column prop="calories" label="总热量" width="120">
          <template #default="scope">{{ scope.row.calories }} kcal</template>
        </el-table-column>
        <el-table-column prop="author" label="发布者" width="120" />
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button size="small" :icon="Edit" circle @click="handleEdit(scope.row)"></el-button>
            <el-popconfirm title="确定删除该推荐吗？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger" :icon="Delete" circle></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 推荐编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑推荐' : '发布膳食推荐'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="如：减脂期高蛋白早餐推荐" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="餐别">
              <el-select v-model="form.mealType" style="width: 100%">
                <el-option label="早餐" value="早餐" />
                <el-option label="午餐" value="午餐" />
                <el-option label="晚餐" value="晚餐" />
                <el-option label="加餐" value="加餐" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总热量">
              <el-input-number v-model="form.calories" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="推荐内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入详细的食材搭配和营养建议..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getDietRecommendationList, addDietRecommendation, updateDietRecommendation, deleteDietRecommendation } from '@/api/dietRecommendation'
import { useUserInfo } from '@/utils/storage'

const loading = ref(false)
const recommendationList = ref([])
const dialogVisible = ref(false)
const form = ref({
  id: null,
  title: '',
  mealType: '早餐',
  calories: 400,
  content: '',
  author: ''
})

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || 'Admin'
})

const fetchRecommendations = async () => {
  loading.value = true
  try {
    const res = await getDietRecommendationList()
    if (res.code === 0 || res.code === 200) {
      recommendationList.value = res.data
    }
  } catch (error) {
    console.error('Fetch recommendations error:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleAddRecommendation = () => {
  form.value = { id: null, title: '', mealType: '早餐', calories: 400, content: '', author: displayName.value }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    let res
    if (form.value.id) {
      res = await updateDietRecommendation(form.value)
    } else {
      res = await addDietRecommendation(form.value)
    }

    if (res.code === 0 || res.code === 200) {
      ElMessage.success(form.value.id ? '更新成功' : '发布成功')
      dialogVisible.value = false
      fetchRecommendations()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('Submit diet recommendation error:', error)
    ElMessage.error('网络错误')
  }
}

const handleDelete = async (id) => {
  try {
    const res = await deleteDietRecommendation(id)
    if (res.code === 0 || res.code === 200) {
      ElMessage.success('删除成功')
      fetchRecommendations()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    console.error('Delete diet recommendation error:', error)
    ElMessage.error('网络错误')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

onMounted(() => {
  fetchRecommendations()
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
