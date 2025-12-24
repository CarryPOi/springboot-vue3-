<template>
  <div class="plan-layout">
    <!-- 侧边栏 -->
    <Sidebar 
      v-model:isCollapsed="isCollapsed" 
      @menu-click="handleMenuClick"
    />

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部栏 -->
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">📅</span>
          <span>训练计划</span>
        </div>
        <el-dropdown>
          <span class="user-dropdown">
            <el-avatar :size="32" class="user-avatar" :src="displayAvatarUrl">
              {{ displayName.charAt(0).toUpperCase() }}
            </el-avatar>
            <span class="user-name-text">{{ displayName }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/user')">
                <el-icon><User /></el-icon> 个人中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card card-purple">
          <div class="stat-icon">📋</div>
          <div class="stat-info">
            <div class="stat-value">{{ planList.length }}</div>
            <div class="stat-label">训练计划总数</div>
          </div>
        </div>
        <div class="stat-card card-pink">
          <div class="stat-icon">⏱️</div>
          <div class="stat-info">
            <div class="stat-value">{{ totalDuration }}</div>
            <div class="stat-label">总训练时长(分钟)</div>
          </div>
        </div>
        <div class="stat-card card-blue">
          <div class="stat-icon">🏋️</div>
          <div class="stat-info">
            <div class="stat-value">{{ typeCount }}</div>
            <div class="stat-label">训练类型数</div>
          </div>
        </div>
      </div>

      <!-- 操作区 -->
      <div class="operate-area">
        <el-button type="primary" @click="openAddDialog" class="add-btn">
          <el-icon><Plus /></el-icon> 新增计划
        </el-button>
        <el-input 
          v-model="searchKey" 
          placeholder="搜索计划名称..." 
          prefix-icon="Search"
          class="search-input"
          clearable
        />
      </div>

      <!-- 计划列表 -->
      <div class="plan-list">
        <el-card class="table-card">
          <el-table 
            :data="paginatedList" 
            style="width: 100%;"
            :header-cell-style="{ background: '#fafafa', fontWeight: '600' }"
          >
            <el-table-column prop="name" label="计划名称" min-width="180">
              <template #default="scope">
                <div class="plan-name">
                  <span class="name-icon">📌</span>
                  {{ scope.row.name }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="type" label="训练类型" width="140">
              <template #default="scope">
                <el-tag :type="getTypeTagColor(scope.row.type)" size="small">
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="时长(分钟)" width="120" align="center">
              <template #default="scope">
                <span class="duration-text">{{ scope.row.duration }} 分钟</span>
              </template>
            </el-table-column>
            <el-table-column prop="frequency" label="训练频率" width="130">
              <template #default="scope">
                <el-tag type="info" size="small" effect="plain">
                  {{ scope.row.frequency }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="scope">
                <span class="time-text">{{ formatTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button type="primary" link @click="openEditDialog(scope.row)">
                  <el-icon><Edit /></el-icon> 编辑
                </el-button>
                <el-button type="danger" link @click="deletePlan(scope.row.id)">
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 空状态 -->
          <el-empty v-if="filterPlanList.length === 0" description="暂无训练计划，快去添加一个吧！" />

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="filterPlanList.length > 0">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[5, 10, 20]"
              :total="filterPlanList.length"
              layout="total, sizes, prev, pager, next, jumper"
              background
            />
          </div>
        </el-card>
      </div>

      <!-- 新增/编辑弹窗 -->
      <el-dialog 
        v-model="dialogVisible" 
        :title="isEdit ? '编辑训练计划' : '新增训练计划'" 
        width="500px"
        class="plan-dialog"
      >
        <el-form :model="formData" label-width="100px" :rules="formRules" ref="formRef">
          <el-form-item label="计划名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入计划名称" />
          </el-form-item>
          <el-form-item label="训练类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择训练类型" style="width: 100%">
              <el-option label="力量训练" value="力量训练" />
              <el-option label="有氧运动" value="有氧运动" />
              <el-option label="柔韧性训练" value="柔韧性训练" />
              <el-option label="核心训练" value="核心训练" />
              <el-option label="HIIT" value="HIIT" />
              <el-option label="瑜伽" value="瑜伽" />
            </el-select>
          </el-form-item>
          <el-form-item label="单次时长" prop="duration">
            <el-input-number v-model="formData.duration" :min="1" :max="500" style="width: 100%" />
            <span class="form-tip">分钟</span>
          </el-form-item>
          <el-form-item label="训练频率" prop="frequency">
            <el-select v-model="formData.frequency" placeholder="请选择训练频率" style="width: 100%">
              <el-option label="每日" value="每日" />
              <el-option label="每周3次" value="每周3次" />
              <el-option label="每周5次" value="每周5次" />
              <el-option label="隔日" value="隔日" />
              <el-option label="每周1次" value="每周1次" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePlan" :loading="saveLoading">
            {{ saveLoading ? '保存中...' : '确定' }}
          </el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, User, SwitchButton } from '@element-plus/icons-vue'
import Sidebar from '@/components/Sidebar.vue'
import { useUserInfo } from '@/utils/storage'
import { getUsername, logout as commonLogout, getFullUrl, BASE_API_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)

const userInfo = useUserInfo()
const displayName = computed(() => userInfo.value.realName || userInfo.value.username || '用户')

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const logout = () => {
  commonLogout()
}

const planList = ref([])
const searchKey = ref('')
const loading = ref(false)
const saveLoading = ref(false)

const handleMenuClick = (item) => {
  console.log('点击菜单：', item.title)
}

// 过滤后的列表
const filterPlanList = computed(() => {
  return planList.value.filter(item => item.name.includes(searchKey.value))
})

// 统计数据
const totalDuration = computed(() => {
  return planList.value.reduce((sum, item) => sum + (item.duration || 0), 0)
})

const typeCount = computed(() => {
  const types = new Set(planList.value.map(item => item.type))
  return types.size
})

// 分页
const currentPage = ref(1)
const pageSize = ref(5)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filterPlanList.value.slice(start, start + pageSize.value)
})

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = ref({
  id: '',
  name: '',
  type: '',
  duration: 30,
  frequency: ''
})

const formRules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择训练类型', trigger: 'change' }],
  duration: [{ required: true, message: '请输入训练时长', trigger: 'blur' }],
  frequency: [{ required: true, message: '请选择训练频率', trigger: 'change' }]
}

// 时间格式化
const formatTime = (time) => {
  if (!time) return '--'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 获取标签颜色
const getTypeTagColor = (type) => {
  const colorMap = {
    '力量训练': 'danger',
    '有氧运动': 'success',
    '柔韧性训练': 'warning',
    '核心训练': 'primary',
    'HIIT': 'danger',
    '瑜伽': 'info'
  }
  return colorMap[type] || ''
}

// 获取计划列表
const getPlanList = async () => {
  const username = getUsername()
  if (!username) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/plan/list?username=${username}`)
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      planList.value = data.data || []
    } else {
      ElMessage.error(`获取计划失败：${data.msg}`)
    }
  } catch (err) {
    console.error('获取计划列表失败：', err)
  }
}

// 新增计划
const addPlan = async () => {
  const username = getUsername()
  try {
    const res = await fetch(`${BASE_API_URL}/api/plan/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify({
        username: username,
        name: formData.value.name,
        type: formData.value.type,
        duration: formData.value.duration,
        frequency: formData.value.frequency
      })
    })
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      ElMessage.success('新增计划成功')
      return true
    } else {
      ElMessage.error(`新增失败：${data.msg}`)
      return false
    }
  } catch (err) {
    console.error('新增计划失败：', err)
    ElMessage.error('网络错误，新增计划失败')
    return false
  }
}

// 编辑计划
const editPlan = async () => {
  try {
    const res = await fetch(`${BASE_API_URL}/api/plan/edit`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify({
        id: formData.value.id,
        name: formData.value.name,
        type: formData.value.type,
        duration: formData.value.duration,
        frequency: formData.value.frequency
      })
    })
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      ElMessage.success('编辑计划成功')
      return true
    } else {
      ElMessage.error(`编辑失败：${data.msg}`)
      return false
    }
  } catch (err) {
    console.error('编辑计划失败：', err)
    ElMessage.error('网络错误，编辑计划失败')
    return false
  }
}

// 删除计划
const deletePlan = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个训练计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await fetch(`${BASE_API_URL}/api/plan/delete`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify({ id })
    })
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      ElMessage.success('删除计划成功')
      getPlanList()
    } else {
      ElMessage.error(`删除失败：${data.msg}`)
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('删除计划失败：', err)
      ElMessage.error('网络错误，删除计划失败')
    }
  }
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  formData.value = { id: '', name: '', type: '', duration: 30, frequency: '' }
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  formData.value = { ...row }
  dialogVisible.value = true
}

// 保存计划
const savePlan = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    saveLoading.value = true
    let success = false
    if (isEdit.value) {
      success = await editPlan()
    } else {
      success = await addPlan()
    }
    saveLoading.value = false
    
    if (success) {
      dialogVisible.value = false
      getPlanList()
    }
  })
}

onMounted(() => {
  getPlanList()
})
</script>

<style scoped>
.plan-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  height: 100%;
  overflow: auto;
}

/* 顶部栏 */
.top-bar {
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.title-icon {
  font-size: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 600;
}

.user-name-text {
  color: #606266;
}

/* 统计卡片 */
.stats-cards {
  padding: 20px 24px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: #fff;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.card-purple { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.card-pink { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.card-blue { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }

.stat-icon {
  font-size: 36px;
  opacity: 0.9;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
}

/* 操作区 */
.operate-area {
  padding: 0 24px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-btn {
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.search-input {
  width: 300px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 计划列表 */
.plan-list {
  padding: 0 24px 24px;
}

.table-card {
  border-radius: 12px;
  overflow: hidden;
}

.plan-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.name-icon {
  font-size: 16px;
}

.duration-text {
  color: #409eff;
  font-weight: 500;
}

.time-text {
  color: #909399;
  font-size: 13px;
}

.pagination-wrapper {
  padding: 20px 0;
  display: flex;
  justify-content: flex-end;
}

/* 弹窗 */
.plan-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
}

.plan-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 1000px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .operate-area {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-input {
    width: 100%;
  }
}
</style>
