<template>
  <div class="body-data-layout">
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
          <span class="title-icon">📊</span>
          <span>身材数据分析</span>
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

      <!-- 身体数据概览 -->
      <div class="stats-overview">
        <div class="stat-card card-1">
          <div class="stat-icon">⚖️</div>
          <div class="stat-info">
            <div class="stat-value">{{ userBaseInfo.weight || '--' }} <span class="unit">kg</span></div>
            <div class="stat-label">当前体重</div>
            <div v-if="dataDiff" class="stat-diff" :class="{ 'is-down': dataDiff.weight < 0 }">
              {{ dataDiff.weight > 0 ? '+' : '' }}{{ dataDiff.weight }} kg
            </div>
          </div>
        </div>
        <div class="stat-card card-bmi" :style="{ background: bmiInfo.color }">
          <div class="stat-icon">📈</div>
          <div class="stat-info">
            <div class="stat-value">{{ bmiInfo.value }}</div>
            <div class="stat-label">BMI 指数 ({{ bmiInfo.label }})</div>
          </div>
        </div>
        <div class="stat-card card-3">
          <div class="stat-icon">💪</div>
          <div class="stat-info">
            <div class="stat-value">{{ userBaseInfo.bodyFatRate || '--' }} <span class="unit">%</span></div>
            <div class="stat-label">当前体脂率</div>
            <div v-if="dataDiff && dataDiff.bodyFat" class="stat-diff" :class="{ 'is-down': dataDiff.bodyFat < 0 }">
              {{ dataDiff.bodyFat > 0 ? '+' : '' }}{{ dataDiff.bodyFat }} %
            </div>
          </div>
        </div>
        <div class="stat-card card-4">
          <div class="stat-icon">📝</div>
          <div class="stat-info">
            <div class="stat-value">{{ bodyDataList.length }} <span class="unit">次</span></div>
            <div class="stat-label">累计记录</div>
          </div>
        </div>
      </div>

      <!-- 趋势分析图表 -->
      <div class="chart-container-wrapper" v-if="bodyDataList.length > 0">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span class="card-title">身材变化趋势</span>
            </div>
          </template>
          <div ref="chartRef" class="trend-chart"></div>
        </el-card>
      </div>

      <!-- 核心内容区 -->
      <div class="content-wrapper">
        <!-- 左侧：数据录入 -->
        <div class="left-panel">
          <el-card class="form-card">
            <template #header>
              <div class="card-header">
                <span class="card-icon">📝</span>
                <span class="card-title">{{ bodyDataForm.id ? '编辑身材数据' : '记录身材数据' }}</span>
              </div>
            </template>
            <el-form :model="bodyDataForm" label-position="top">
              <el-form-item label="记录日期">
                <el-date-picker 
                  v-model="bodyDataForm.recordDate" 
                  type="date" 
                  placeholder="选择日期"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-row :gutter="16">
                <el-col :span="12">
                  <el-form-item label="体重(kg)">
                    <el-input-number 
                      v-model="bodyDataForm.weight" 
                      :precision="1"
                      :min="20"
                      :max="300"
                      placeholder="体重"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="体脂率(%)">
                    <el-input-number 
                      v-model="bodyDataForm.bodyFatRate" 
                      :precision="1"
                      :min="1"
                      :max="60"
                      placeholder="体脂率"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="12">
                  <el-form-item label="腰围(cm)">
                    <el-input-number 
                      v-model="bodyDataForm.waistCircumference" 
                      :precision="1"
                      :min="40"
                      :max="200"
                      placeholder="腰围"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="臀围(cm)">
                    <el-input-number 
                      v-model="bodyDataForm.hipCircumference" 
                      :precision="1"
                      :min="50"
                      :max="200"
                      placeholder="臀围"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="备注">
                <el-input 
                  v-model="bodyDataForm.remark" 
                  type="textarea" 
                  :rows="2"
                  placeholder="记录今天的状态或饮食情况..."
                />
                <div class="quick-tags">
                  <el-tag 
                    v-for="tag in quickRemarks" 
                    :key="tag" 
                    size="small" 
                    class="quick-tag"
                    @click="addQuickRemark(tag)"
                  >
                    + {{ tag }}
                  </el-tag>
                </div>
              </el-form-item>
              <div class="form-actions">
                <el-button type="primary" @click="saveBodyData" :loading="saving" class="save-btn">
                  <el-icon><Check /></el-icon> {{ saving ? '保存中...' : '保存记录' }}
                </el-button>
                <el-button @click="resetForm">{{ bodyDataForm.id ? '取消' : '重置' }}</el-button>
              </div>
            </el-form>
          </el-card>
        </div>

        <!-- 右侧：历史记录 + AI分析 -->
        <div class="right-panel">
          <!-- 历史记录 -->
          <el-card class="table-card">
            <template #header>
              <div class="card-header">
                <span class="card-icon">📋</span>
                <span class="card-title">历史记录</span>
              </div>
            </template>
            <el-table 
              :data="bodyDataList" 
              style="width: 100%;"
              max-height="280"
              :header-cell-style="{ background: '#fafafa', fontWeight: '600' }"
            >
              <el-table-column prop="recordDate" label="日期" width="110" />
              <el-table-column prop="weight" label="体重" width="80">
                <template #default="scope">
                  {{ scope.row.weight }} kg
                </template>
              </el-table-column>
              <el-table-column prop="bodyFatRate" label="体脂率" width="80">
                <template #default="scope">
                  {{ scope.row.bodyFatRate || '--' }}%
                </template>
              </el-table-column>
              <el-table-column prop="waistCircumference" label="腰围" width="80">
                <template #default="scope">
                  {{ scope.row.waistCircumference || '--' }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="scope">
                  <el-button type="primary" link size="small" @click="editBodyData(scope.row)">
                    编辑
                  </el-button>
                  <el-button type="danger" link size="small" @click="deleteBodyData(scope.row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-if="bodyDataList.length === 0" description="暂无记录" :image-size="80" />
          </el-card>

          <!-- AI分析 -->
          <el-card class="ai-card">
            <template #header>
              <div class="card-header">
                <span class="card-icon">🤖</span>
                <span class="card-title">AI 身材分析</span>
              </div>
            </template>
            <div class="ai-content">
              <el-button 
                type="primary" 
                @click="getAIAnalysis" 
                :loading="aiLoading"
                :disabled="bodyDataList.length === 0"
                class="ai-btn"
              >
                <el-icon><MagicStick /></el-icon>
                {{ aiLoading ? '分析中...' : '生成AI分析建议' }}
              </el-button>
              
              <div v-if="aiAnalysisResult" class="analysis-result">
                <div class="analysis-text" v-html="formattedAnalysis"></div>
              </div>
              <div v-else class="empty-tip">
                <p>{{ bodyDataList.length === 0 ? '请先添加身材数据记录' : '点击按钮获取AI分析建议' }}</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, SwitchButton, Check, MagicStick, TrendCharts, List } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import Sidebar from '@/components/Sidebar.vue'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { getUsername, logout as commonLogout, getFullUrl, BASE_API_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const saving = ref(false)

// 图表实例
let trendChart = null
const chartRef = ref(null)

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userBaseInfo.value.realName || userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userBaseInfo.value.avatarUrl || userInfo.value.avatarUrl))

const userName = ref(getUsername())

// 个人基础信息
const userBaseInfo = ref({
  realName: '',
  avatarUrl: '',
  age: '',
  height: '',
  weight: '',
  bodyFatRate: '',
  waistCircumference: '',
  hipCircumference: ''
})

// BMI 计算
const bmiInfo = computed(() => {
  const h = userBaseInfo.value.height / 100
  const w = userBaseInfo.value.weight
  if (!h || !w) return { value: '--', label: '未知', color: '#909399' }
  const bmi = (w / (h * h)).toFixed(1)
  let label = '正常'
  let color = '#67C23A'
  if (bmi < 18.5) { label = '偏瘦'; color = '#E6A23C' }
  else if (bmi >= 24 && bmi < 28) { label = '超重'; color = '#E6A23C' }
  else if (bmi >= 28) { label = '肥胖'; color = '#F56C6C' }
  return { value: bmi, label, color }
})

// 数据变动对比
const dataDiff = computed(() => {
  if (bodyDataList.value.length < 2) return null
  const latest = bodyDataList.value[0]
  const prev = bodyDataList.value[1]
  return {
    weight: (latest.weight - prev.weight).toFixed(1),
    bodyFat: latest.bodyFatRate && prev.bodyFatRate ? (latest.bodyFatRate - prev.bodyFatRate).toFixed(1) : null
  }
})

// 身材数据表单
const bodyDataForm = ref({
  id: '',
  username: userName.value,
  recordDate: new Date().toISOString().split('T')[0],
  weight: null,
  bodyFatRate: null,
  waistCircumference: null,
  hipCircumference: null,
  remark: ''
})

// 快速备注标签
const quickRemarks = ['空腹称重', '运动后', '大餐后', '早起记录', '睡前记录']
const addQuickRemark = (tag) => {
  if (bodyDataForm.value.remark) {
    bodyDataForm.value.remark += ` ${tag}`
  } else {
    bodyDataForm.value.remark = tag
  }
}

// 历史数据列表
const bodyDataList = ref([])

// AI分析
const aiLoading = ref(false)
const aiAnalysisResult = ref(null)

const formattedAnalysis = computed(() => {
  if (!aiAnalysisResult.value) return ''
  const text = typeof aiAnalysisResult.value === 'string' 
    ? aiAnalysisResult.value 
    : aiAnalysisResult.value.analysis || ''
    
  return text
    .replace(/### (.*)/g, '<h3 class="analysis-h3">$1</h3>')
    .replace(/\*\*(.*)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
    .replace(/(\d\.) /g, '<span class="list-num">$1</span> ')
})

// 初始化图表
const initChart = () => {
  if (!chartRef.value || bodyDataList.value.length === 0) return
  
  if (!trendChart) {
    trendChart = echarts.init(chartRef.value)
  }
  
  const sortedData = [...bodyDataList.value].reverse()
  const dates = sortedData.map(item => item.recordDate)
  const weights = sortedData.map(item => item.weight)
  const bodyFats = sortedData.map(item => item.bodyFatRate)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderWidth: 0,
      boxShadow: '0 0 10px rgba(0,0,0,0.1)',
      textStyle: { color: '#333' }
    },
    legend: {
      data: ['体重 (kg)', '体脂率 (%)'],
      bottom: 0
    },
    grid: {
      top: '10%',
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#999' }
    },
    yAxis: [
      {
        type: 'value',
        name: '体重',
        position: 'left',
        splitLine: { lineStyle: { type: 'dashed' } }
      },
      {
        type: 'value',
        name: '体脂率',
        position: 'right',
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '体重 (kg)',
        type: 'line',
        data: weights,
        smooth: true,
        showSymbol: false,
        itemStyle: { color: '#667eea' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
            { offset: 1, color: 'rgba(102, 126, 234, 0)' }
          ])
        }
      },
      {
        name: '体脂率 (%)',
        type: 'line',
        yAxisIndex: 1,
        data: bodyFats,
        smooth: true,
        showSymbol: false,
        itemStyle: { color: '#f5576c' }
      }
    ]
  }
  
  if (trendChart && !trendChart.isDisposed()) {
    trendChart.setOption(option)
  }
}

const handleResize = () => {
  trendChart?.resize()
}

// 监听窗口大小变化
onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
})

const handleMenuClick = (item) => {
  console.log('点击菜单：', item.title)
}

const logout = () => {
  commonLogout()
}

// 获取个人基础信息
const getUserBaseInfo = async () => {
  const currentUsername = getUsername()
  if (!currentUsername) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/user/info/get?username=${currentUsername}`)
    const data = await res.json()
    if ((data.code === 0 || data.code === 200) && data.data) {
      const userData = data.data
      userBaseInfo.value = {
        realName: userData.realName || '',
        avatarUrl: userData.avatarUrl || '',
        age: userData.age || '',
        height: userData.height ? Number(userData.height) : '',
        weight: userData.weight ? Number(userData.weight) : '',
        bodyFatRate: userData.bodyFatRate ? Number(userData.bodyFatRate) : '',
        waistCircumference: userData.waistCircumference ? Number(userData.waistCircumference) : '',
        hipCircumference: userData.hipCircumference ? Number(userData.hipCircumference) : ''
      }
      
      // 智能回填：如果表单没有值，使用最新基础信息
      if (!bodyDataForm.value.weight) {
        bodyDataForm.value.weight = userBaseInfo.value.weight
        bodyDataForm.value.bodyFatRate = userBaseInfo.value.bodyFatRate
        bodyDataForm.value.waistCircumference = userBaseInfo.value.waistCircumference
        bodyDataForm.value.hipCircumference = userBaseInfo.value.hipCircumference
      }
    }
  } catch (e) {
    console.error('获取个人信息失败：', e)
  }
}

// 获取历史数据
const getBodyDataList = async () => {
  const currentUsername = getUsername()
  if (!currentUsername) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/body-data/list?username=${currentUsername}`)
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      bodyDataList.value = (data.data || []).map(item => ({
        ...item,
        weight: Number(item.weight),
        bodyFatRate: item.bodyFatRate ? Number(item.bodyFatRate) : null,
        waistCircumference: item.waistCircumference ? Number(item.waistCircumference) : null,
        hipCircumference: item.hipCircumference ? Number(item.hipCircumference) : null
      }))
      
      nextTick(() => initChart())
    }
  } catch (e) {
    console.error('获取历史数据失败：', e)
  }
}

// 保存数据
const saveBodyData = async () => {
  const currentUsername = getUsername()
  if (!currentUsername) {
    ElMessage.error('用户未登录')
    return
  }

  if (!bodyDataForm.value.recordDate || !bodyDataForm.value.weight) {
    ElMessage.warning('记录日期和体重为必填项')
    return
  }

  saving.value = true
  try {
    const res = await fetch(`${BASE_API_URL}/api/body-data/save`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...bodyDataForm.value,
        username: currentUsername
      })
    })
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      ElMessage.success('保存成功！')
      resetForm()
      getBodyDataList()
      getUserBaseInfo()
    } else {
      ElMessage.error(data.msg || '保存失败')
    }
  } catch (e) {
    console.error('保存数据失败：', e)
    ElMessage.error('网络异常，保存失败')
  } finally {
    saving.value = false
  }
}

// 编辑数据
const editBodyData = (row) => {
  bodyDataForm.value = { ...row }
  // 滚动到表单
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 删除数据
const deleteBodyData = async (id) => {
  const currentUsername = getUsername()
  try {
    await ElMessageBox.confirm('确认删除这条记录吗？', '提示', { type: 'warning' })
    const res = await fetch(`${BASE_API_URL}/api/body-data/delete?id=${id}`, { method: 'POST' })
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      ElMessage.success('删除成功')
      getBodyDataList()
    } else {
      ElMessage.error(data.msg || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

// 重置表单
const resetForm = () => {
  bodyDataForm.value = {
    id: '',
    username: userName.value,
    recordDate: new Date().toISOString().split('T')[0],
    weight: userBaseInfo.value.weight || null,
    bodyFatRate: userBaseInfo.value.bodyFatRate || null,
    waistCircumference: userBaseInfo.value.waistCircumference || null,
    hipCircumference: userBaseInfo.value.hipCircumference || null,
    remark: ''
  }
}

// AI分析
const getAIAnalysis = async () => {
  if (bodyDataList.value.length === 0) {
    ElMessage.warning('暂无历史数据，无法生成分析')
    return
  }

  aiLoading.value = true
  try {
    const res = await fetch(`${BASE_API_URL}/api/body-data/analyze?username=${userName.value}`)
    const data = await res.json()
    if (data.code === 0) {
      aiAnalysisResult.value = data.data
      ElMessage.success('分析完成！')
    } else {
      ElMessage.error(data.msg || '分析失败')
    }
  } catch (e) {
    console.error('AI分析失败：', e)
    ElMessage.error('网络异常，分析失败')
  } finally {
    aiLoading.value = false
  }
}

onMounted(() => {
  getUserBaseInfo()
  getBodyDataList()
})
</script>

<style scoped>
.body-data-layout {
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

.title-icon { font-size: 20px; }

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

.user-name-text { color: #606266; }

/* 数据概览 */
.stats-overview {
  padding: 20px 24px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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

.card-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.card-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.card-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.card-4 { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-icon { font-size: 36px; opacity: 0.9; }
.stat-value { font-size: 24px; font-weight: 700; }
.stat-label { font-size: 13px; opacity: 0.9; }

.unit { font-size: 14px; font-weight: normal; margin-left: 2px; }

.stat-diff {
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 2px;
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 4px;
  width: fit-content;
}

.stat-diff.is-down {
  color: #a0f0a0;
}

/* 图表 */
.chart-container-wrapper {
  padding: 0 24px 24px;
}

.chart-card {
  border-radius: 12px;
}

.trend-chart {
  height: 350px;
  width: 100%;
}

/* 内容区 */
.content-wrapper {
  display: flex;
  gap: 20px;
  padding: 0 24px 24px;
}

.left-panel { width: 400px; flex-shrink: 0; }
.right-panel { flex: 1; display: flex; flex-direction: column; gap: 20px; }

.form-card, .table-card, .ai-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-icon { font-size: 18px; }
.card-title { font-size: 15px; font-weight: 600; color: #303133; }

.quick-tags {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.quick-tag:hover {
  background-color: #409eff;
  color: #fff;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 10px;
}

.save-btn {
  flex: 1;
  height: 42px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

/* AI分析 */
.ai-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.analysis-text {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  line-height: 1.8;
  font-size: 14px;
  color: #444;
  border-left: 4px solid #409eff;
}

:deep(.analysis-h3) {
  margin: 16px 0 12px;
  color: #2c3e50;
  font-size: 16px;
  display: flex;
  align-items: center;
}

:deep(.list-num) {
  color: #409eff;
  font-weight: bold;
  margin-right: 4px;
}

:deep(strong) {
  color: #2c3e50;
  font-weight: 600;
}

.ai-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 15px;
}

.analysis-result {
  margin-top: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  border-radius: 12px;
}

.result-section {
  margin-bottom: 16px;
}

.result-section:last-child { margin-bottom: 0; }

.result-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #303133;
  margin-bottom: 10px;
}

.section-icon { font-size: 18px; }

.result-section p {
  line-height: 1.7;
  color: #606266;
}

.result-section ul {
  margin: 0;
  padding-left: 20px;
}

.result-section li {
  line-height: 1.8;
  color: #606266;
  margin-bottom: 6px;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 30px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-overview { grid-template-columns: repeat(2, 1fr); }
  .content-wrapper { flex-direction: column; }
  .left-panel { width: 100%; }
}
</style>
