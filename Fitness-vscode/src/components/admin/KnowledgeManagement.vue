<template>
  <div class="knowledge-management">
    <el-card class="knowledge-table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">健身知识库管理</span>
          </div>
          <div class="header-right">
            <el-button type="success" :icon="Plus" @click="handleAddKnowledge">发布知识文章</el-button>
            <el-button type="primary" :icon="Refresh" @click="fetchKnowledge" circle></el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="knowledgeList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coverUrl" label="封面" width="100">
          <template #default="scope">
            <el-image 
              v-if="scope.row.coverUrl"
              style="width: 50px; height: 50px; border-radius: 4px" 
              :src="getCoverUrl(scope.row.coverUrl)" 
              fit="cover"
            />
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="scope">
            <el-tag>{{ scope.row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已发布' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button size="small" :icon="Edit" circle @click="handleEditKnowledge(scope.row)"></el-button>
            <el-popconfirm title="确定删除该文章吗？" @confirm="handleDeleteKnowledge(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger" :icon="Delete" circle></el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 知识编辑弹窗 -->
    <el-dialog v-model="knowledgeDialogVisible" :title="knowledgeForm.id ? '编辑文章' : '发布新文章'" width="700px">
      <el-form :model="knowledgeForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="knowledgeForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="knowledgeForm.category" placeholder="请选择分类" style="width: 100%">
                <el-option label="健身入门" value="健身入门" />
                <el-option label="增肌" value="增肌" />
                <el-option label="减脂" value="减脂" />
                <el-option label="拉伸" value="拉伸" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="knowledgeForm.status" style="width: 100%">
                <el-option label="已发布" value="已发布" />
                <el-option label="草稿" value="草稿" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图URL">
          <el-input v-model="knowledgeForm.coverUrl" placeholder="请输入封面图链接" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input 
            v-model="knowledgeForm.content" 
            type="textarea" 
            :rows="10" 
            placeholder="请输入详细文章内容..." 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="knowledgeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitKnowledge">确定发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { BASE_API_URL, getFullUrl, formatTime } from '@/utils/common'

const loading = ref(false)
const knowledgeList = ref([])
const knowledgeDialogVisible = ref(false)
const knowledgeForm = ref({
  id: null,
  title: '',
  content: '',
  category: '健身入门',
  coverUrl: '',
  status: '已发布'
})

const fetchKnowledge = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${BASE_API_URL}/api/knowledge/admin/list`)
    if (res.data.code === 200 || res.data.code === 0) {
      knowledgeList.value = res.data.data
    }
  } catch (error) {
    console.error('Fetch knowledge error:', error)
    ElMessage.error('获取知识列表失败')
  } finally {
    loading.value = false
  }
}

const handleAddKnowledge = () => {
  knowledgeForm.value = {
    id: null,
    title: '',
    content: '',
    category: '健身入门',
    coverUrl: '',
    status: '已发布'
  }
  knowledgeDialogVisible.value = true
}

const handleEditKnowledge = (row) => {
  knowledgeForm.value = { ...row }
  knowledgeDialogVisible.value = true
}

const submitKnowledge = async () => {
  if (!knowledgeForm.value.title || !knowledgeForm.value.content) {
    ElMessage.warning('请填写完整的标题和内容')
    return
  }
  try {
    const res = await axios.post(`${BASE_API_URL}/api/knowledge/save`, knowledgeForm.value)
    if (res.data.code === 200 || res.data.code === 0) {
      ElMessage.success('保存成功')
      knowledgeDialogVisible.value = false
      fetchKnowledge()
    }
  } catch (error) {
    console.error('Save knowledge error:', error)
    ElMessage.error('保存失败')
  }
}

const handleDeleteKnowledge = async (id) => {
  try {
    const res = await axios.delete(`${BASE_API_URL}/api/knowledge/${id}`)
    if (res.data.code === 200 || res.data.code === 0) {
      ElMessage.success('删除成功')
      fetchKnowledge()
    }
  } catch (error) {
    console.error('Delete knowledge error:', error)
    ElMessage.error('删除失败')
  }
}

const getCoverUrl = (url) => getFullUrl(url)

onMounted(() => {
  fetchKnowledge()
})
</script>

<style scoped>
.knowledge-management {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  font-size: 18px;
  font-weight: bold;
}
</style>
