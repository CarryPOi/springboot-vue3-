<template>
  <div class="diet-layout">
    <Sidebar v-model:isCollapsed="isCollapsed" />

    <div class="main-content">
      <!-- 顶部栏 -->
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">🥗</span>
          <span>每日饮食记录</span>
        </div>
        <div class="top-right">
          <div class="date-picker">
            <el-date-picker
              v-model="currentDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :clearable="false"
              @change="fetchData"
            />
          </div>
          <el-dropdown class="user-dropdown-container">
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
      </div>

      <div class="content-container">
        <!-- 核心统计概览 -->
        <el-card class="overview-card" shadow="never">
          <div class="overview-grid">
            <div class="overview-item calories">
              <div class="item-icon">🔥</div>
              <div class="item-info">
                <div class="item-label">总热量</div>
                <div class="item-value">
                  <span class="number">{{ stats.totalCalories || 0 }}</span>
                  <span class="unit">kcal</span>
                </div>
              </div>
            </div>
            <div class="overview-item protein">
              <div class="item-icon">🥩</div>
              <div class="item-info">
                <div class="item-label">蛋白质</div>
                <div class="item-value">
                  <span class="number">{{ stats.totalProtein || 0 }}</span>
                  <span class="unit">g</span>
                </div>
              </div>
            </div>
            <div class="overview-item carbs">
              <div class="item-icon">🍞</div>
              <div class="item-info">
                <div class="item-label">碳水</div>
                <div class="item-value">
                  <span class="number">{{ stats.totalCarbs || 0 }}</span>
                  <span class="unit">g</span>
                </div>
              </div>
            </div>
            <div class="overview-item fat">
              <div class="item-icon">🥑</div>
              <div class="item-info">
                <div class="item-label">脂肪</div>
                <div class="item-value">
                  <span class="number">{{ stats.totalFat || 0 }}</span>
                  <span class="unit">g</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 历史记录小模块 -->
        <div class="history-section">
          <div class="history-header">
            <span class="history-title">最近 7 天热量</span>
            <span class="history-tip">点击切换日期</span>
          </div>
          <div class="history-list">
            <div 
              v-for="item in historyList" 
              :key="item.date" 
              class="history-item"
              :class="{ 'is-active': currentDate === item.date }"
              @click="handleHistoryClick(item.date)"
            >
              <div class="history-date">{{ formatHistoryDate(item.date) }}</div>
              <div class="history-bar-container">
                <div 
                  class="history-bar" 
                  :style="{ height: getBarHeight(item.calories) }"
                ></div>
              </div>
              <div class="history-cal">{{ Math.round(item.calories) }}</div>
            </div>
          </div>
        </div>

        <!-- 饮食记录主体 -->
        <div class="diet-main-section">
          <div class="section-header">
            <div class="header-left">
              <h3 class="section-title">今日菜单</h3>
              <el-tag round type="info" class="date-tag">{{ currentDate }}</el-tag>
            </div>
            <el-button type="primary" round icon="Plus" @click="showAddDialog">记一笔</el-button>
          </div>

          <el-tabs v-model="activeMealTab" class="meal-tabs">
            <el-tab-pane v-for="type in mealTypes" :key="type" :name="type">
              <template #label>
                <span class="tab-label">
                  {{ type }}
                  <el-badge 
                    :value="getDietListByType(type).length" 
                    :hidden="getDietListByType(type).length === 0"
                    type="primary"
                    is-dot
                  />
                </span>
              </template>
              
              <div class="table-wrapper">
                <el-table 
                  :data="getDietListByType(type)" 
                  style="width: 100%" 
                  empty-text="暂无记录，快去记一笔吧"
                  :row-class-name="tableRowClassName"
                  class="custom-table"
                >
                  <el-table-column prop="foodName" label="食物名称" min-width="150">
                    <template #default="{ row }">
                      <span class="food-name-text">{{ row.foodName }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="weight" label="分量" width="100">
                    <template #default="{ row }">{{ row.weight }}g</template>
                  </el-table-column>
                  <el-table-column prop="calories" label="热量" width="120">
                    <template #default="{ row }">
                      <span class="calorie-text">{{ row.calories }} kcal</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="营养成分 (g)" align="center">
                    <el-table-column prop="protein" label="蛋" width="60" />
                    <el-table-column prop="carbs" label="碳" width="60" />
                    <el-table-column prop="fat" label="脂" width="60" />
                  </el-table-column>
                  <el-table-column label="操作" width="80" align="center">
                    <template #default="{ row }">
                      <el-button type="danger" link @click="handleDelete(row.id)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <!-- 添加记录弹窗 -->
    <el-dialog v-model="dialogVisible" title="添加饮食记录" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="餐别">
          <el-radio-group v-model="form.mealType">
            <el-radio-button label="早餐" />
            <el-radio-button label="午餐" />
            <el-radio-button label="晚餐" />
            <el-radio-button label="加餐" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="食物名称">
          <el-input v-model="form.foodName" placeholder="例如：鸡胸肉" />
        </el-form-item>
        <el-form-item label="重量(g)">
          <el-input-number v-model="form.weight" :min="0" :step="10" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="热量">
              <el-input-number v-model="form.calories" :min="0" :step="10" placeholder="kcal" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="蛋白质">
              <el-input-number v-model="form.protein" :min="0" :step="1" placeholder="g" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="碳水">
              <el-input-number v-model="form.carbs" :min="0" :step="1" placeholder="g" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="脂肪">
              <el-input-number v-model="form.fat" :min="0" :step="1" placeholder="g" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, User, SwitchButton, Calendar, ArrowLeft, ArrowRight, Delete, Histogram, Food } from '@element-plus/icons-vue'
import Sidebar from '@/components/Sidebar.vue'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { getUsername, logout as commonLogout, getFullUrl, BASE_API_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const currentDate = ref(new Date().toISOString().split('T')[0])

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const logout = () => {
  commonLogout()
}
const dietList = ref([])
const stats = ref({})
const historyList = ref([])
const dialogVisible = ref(false)
const mealTypes = ['早餐', '午餐', '晚餐', '加餐']
const activeMealTab = ref('早餐')

const form = reactive({
  mealType: '早餐',
  foodName: '',
  weight: 100,
  calories: 0,
  protein: 0,
  carbs: 0,
  fat: 0
})



const fetchData = async () => {
  const currentUsername = getUsername()
  if (!currentUsername) return
  
  // 获取列表
  try {
    const res = await fetch(`${BASE_API_URL}/api/diet/list?username=${currentUsername}&date=${currentDate.value}`)
    const data = await res.json()
    if (data.code === 0) {
      dietList.value = data.data
    }
  } catch (e) {
    console.error(e)
  }

  // 获取统计
  try {
    const res = await fetch(`${BASE_API_URL}/api/diet/stats?username=${currentUsername}&date=${currentDate.value}`)
    const data = await res.json()
    if (data.code === 0) {
      stats.value = data.data
    }
  } catch (e) {
    console.error(e)
  }

  // 获取历史
  fetchHistory(currentUsername)
}

const fetchHistory = async (currentUsername) => {
  const uname = currentUsername || getUsername()
  try {
    const res = await fetch(`${BASE_API_URL}/api/diet/history?username=${uname}&days=7`)
    const data = await res.json()
    if (data.code === 0) {
      // 后端返回的是按日期降序的，前端展示建议按升序（左往右）
      historyList.value = data.data.reverse()
    }
  } catch (e) {
    console.error(e)
  }
}

const formatHistoryDate = (dateStr) => {
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

const getBarHeight = (calories) => {
  if (!calories) return '4px'
  const max = Math.max(...historyList.value.map(i => i.calories), 2000)
  const height = (calories / max) * 40
  return `${Math.max(height, 4)}px`
}

const handleHistoryClick = (date) => {
  currentDate.value = date
  fetchData()
}

const getDietListByType = (type) => {
  return dietList.value.filter(item => item.mealType === type)
}

const showAddDialog = () => {
  form.mealType = '早餐'
  form.foodName = ''
  form.weight = 100
  form.calories = 0
  form.protein = 0
  form.carbs = 0
  form.fat = 0
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.foodName) {
    ElMessage.warning('请输入食物名称')
    return
  }

  try {
    const res = await fetch(`${BASE_API_URL}/api/diet/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...form,
        username: getUsername(),
        recordDate: currentDate.value
      })
    })
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(data.msg)
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除这条记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await fetch(`${BASE_API_URL}/api/diet/delete/${id}`, { method: 'DELETE' })
      const data = await res.json()
      if (data.code === 0) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(data.msg)
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.diet-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  background-color: #f5f7fa;
  overflow: hidden;
}

.main-content {
  flex: 1;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

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

.top-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.user-name-text {
  font-size: 14px;
  color: #606266;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.content-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  max-width: 1000px;
  margin: 0 auto;
  width: 100%;
}

/* 概览卡片优化 */
.overview-card {
  margin-bottom: 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
  border: 1px solid #edf2f7;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding: 10px 5px;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.overview-item:hover {
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.item-icon {
  font-size: 24px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.item-value {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.item-value .number {
  font-size: 20px;
  font-weight: 700;
  color: #2d3748;
}

.item-value .unit {
  font-size: 11px;
  color: #a0aec0;
}

/* 颜色风格 */
.calories .item-icon { background: #fff5f5; }
.calories .number { color: #f56c6c; }
.protein .item-icon { background: #f0f7ff; }
.protein .number { color: #409eff; }
.carbs .item-icon { background: #fffaf0; }
.carbs .number { color: #e6a23c; }
.fat .item-icon { background: #f0fff4; }
.fat .number { color: #67c23a; }

/* 历史记录小模块样式 */
.history-section {
  background: #fff;
  padding: 16px 20px;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.history-title {
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
}

.history-tip {
  font-size: 12px;
  color: #a0aec0;
}

.history-list {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.history-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 8px 4px;
  border-radius: 8px;
  transition: all 0.2s;
}

.history-item:hover {
  background-color: #f7fafc;
}

.history-item.is-active {
  background-color: #ebf8ff;
}

.history-item.is-active .history-date {
  color: #4299e1;
  font-weight: 600;
}

.history-date {
  font-size: 12px;
  color: #718096;
}

.history-bar-container {
  height: 40px;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.history-bar {
  width: 16px;
  background: linear-gradient(180deg, #63b3ed 0%, #4299e1 100%);
  border-radius: 4px 4px 2px 2px;
  transition: height 0.3s ease;
}

.history-cal {
  font-size: 11px;
  color: #4a5568;
  font-weight: 500;
}

/* 饮食记录主体 */
.diet-main-section {
  background: #fff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
}

.date-tag {
  font-weight: 500;
}

/* Tabs 优化 */
.meal-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.meal-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f0f2f5;
}

.meal-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  height: 45px;
  transition: all 0.3s;
}

.meal-tabs :deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 表格优化 */
.table-wrapper {
  border-radius: 8px;
  overflow: hidden;
}

.custom-table :deep(.el-table__header th) {
  background-color: #f8fafc;
  color: #718096;
  font-weight: 600;
  font-size: 13px;
  height: 44px;
}

.food-name-text {
  font-weight: 500;
  color: #2d3748;
}

.calorie-text {
  color: #f56c6c;
  font-weight: 600;
}

@media (max-width: 900px) {
  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
