<template>
  <div class="home-layout">
    <!-- 复用侧边栏组件 -->
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
          <span>健身管理系统 - 仪表盘</span>
        </div>
        <div class="top-bar-right">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期查看"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            size="default"
            class="date-picker"
          />
          <el-dropdown>
            <span class="user-dropdown">
              <el-avatar :size="32" class="user-avatar" :src="displayAvatarUrl">
                {{ displayName.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-name">{{ displayName }}</span>
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

      <div class="scroll-content">
        <!-- 欢迎区域 -->
        <div class="welcome-section">
          <div class="welcome-text">
            <h2>👋 欢迎回来，{{ displayName }}！</h2>
            <p>今天也要坚持锻炼，向目标更进一步！</p>
          </div>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/training-plan')">
              <el-icon><Calendar /></el-icon> 训练计划
            </el-button>
            <el-button type="success" @click="$router.push('/diet')">
              <el-icon><Food /></el-icon> 记录饮食
            </el-button>
            <el-button type="warning" @click="$router.push('/body-data')">
              <el-icon><Histogram /></el-icon> 身材记录
            </el-button>
          </div>
        </div>

        <!-- 数据卡片区域 -->
        <div class="card-area">
          <div 
            class="stat-card" 
            v-for="(item, index) in cardList" 
            :key="item.title"
            :class="'card-' + index"
            @click="handleCardClick(item.path)"
          >
            <div class="card-icon">{{ item.icon }}</div>
            <div class="card-info">
              <div class="card-title">{{ item.title }}</div>
              <div class="card-value">{{ item.value }}</div>
              <div class="card-desc">{{ item.desc }}</div>
            </div>
          </div>
        </div>

        <!-- 联动联动展示区 -->
        <div class="linkage-area">
          <!-- 左侧：历史营养摄入统计 -->
          <el-card class="linkage-card diet-summary">
            <template #header>
              <div class="card-header">
                <span>📊 历史营养摄入 (最近7天)</span>
                <el-button link type="primary" @click="$router.push('/diet')">查看详情</el-button>
              </div>
            </template>
            <div class="history-diet-chart-container">
              <div ref="dietHistoryChart" class="diet-chart"></div>
            </div>
          </el-card>

          <!-- 右侧：身材数据 & 课程 -->
          <div class="right-linkage">
            <el-card class="linkage-card body-status">
              <template #header>
                <div class="card-header">
                  <span>📊 最新身材数据</span>
                  <el-button link type="primary" @click="$router.push('/body-data')">记录数据</el-button>
                </div>
              </template>
              <div class="body-info-grid" v-if="homeData.latestBodyData" @click="$router.push('/body-data')" style="cursor: pointer;">
                <div class="info-item">
                  <span class="label">体重</span>
                  <span class="value">{{ homeData.latestBodyData.weight }} kg</span>
                </div>
                <div class="info-item">
                  <span class="label">体脂率</span>
                  <span class="value">{{ homeData.latestBodyData.bodyFatRate }} %</span>
                </div>
                <div class="info-item">
                  <span class="label">腰围</span>
                  <span class="value">{{ homeData.latestBodyData.waistCircumference }} cm</span>
                </div>
                <div class="info-item">
                  <span class="label">日期</span>
                  <span class="value">{{ homeData.latestBodyData.recordDate }}</span>
                </div>
              </div>
              <div v-else class="empty-tip" @click="$router.push('/body-data')" style="cursor: pointer;">暂无记录，快去开启你的健身之旅吧！</div>
            </el-card>

            <el-card class="linkage-card course-status">
              <template #header>
                <div class="card-header">
                  <span>📚 我的订阅课程</span>
                  <el-button link type="primary" @click="$router.push('/courses')">浏览更多</el-button>
                </div>
              </template>
              <div class="course-count-display" @click="$router.push('/courses')" style="cursor: pointer;">
                <div class="count-value">{{ homeData.courseCount || 0 }}</div>
                <div class="count-label">门已选课程</div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 图表区域 -->
        <div class="chart-area">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">📈 训练时长统计</span>
              </div>
            </template>
            <div ref="chart1" class="chart-container"></div>
          </el-card>
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">🎯 训练类型分布</span>
              </div>
            </template>
            <div ref="chart2" class="chart-container"></div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted, reactive, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, SwitchButton, Calendar, Food, Histogram } from '@element-plus/icons-vue'
import Sidebar from '@/components/Sidebar.vue'
import * as echarts from 'echarts'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { getUsername, logout as commonLogout, formatDuration, getFullUrl, BASE_API_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const selectedDate = ref(new Date().toISOString().split('T')[0])

// 使用共享的响应式用户信息
const userInfo = useUserInfo()

const displayName = computed(() => {
  return homeData.userInfo?.realName || userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(homeData.userInfo?.avatarUrl || userInfo.value.avatarUrl))

const logout = () => {
  commonLogout()
}

// 聚合数据
const homeData = reactive({
  userInfo: {},
  dietStats: { totalCalories: 0, totalProtein: 0, totalFat: 0, totalCarbs: 0 },
  courseCount: 0,
  latestBodyData: null,
  todayWorkouts: []
})

// 卡片数据
const cardList = ref([
  { title: '训练计划', value: '加载中...', desc: '我的训练计划', icon: '📋', path: '/training-plan' },
  { title: '今日训练', value: '未开始', desc: '点击去打卡', icon: '🔥', path: '/workout-log' },
  { title: '累计打卡', value: '0次', desc: '加油坚持', icon: '⏱️', path: '/workout-log' },
  { title: '身体数据', value: '加载中...', desc: '请完善资料', icon: '💪', path: '/body-data' }
])

// 图表
const chart1 = ref(null)
const chart2 = ref(null)
const dietHistoryChart = ref(null)
let barChart = null
let pieChart = null
let dietChart = null
let historyListForChart = []

const initCharts = () => {
  if (chart1.value && chart2.value && dietHistoryChart.value) {
    // 初始化图表
    barChart = echarts.init(chart1.value)
    pieChart = echarts.init(chart2.value)
    dietChart = echarts.init(dietHistoryChart.value)
    
    // 设置基础配置
    barChart.setOption({
      title: { text: '训练时长统计', left: 'center' },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value', name: '分钟' },
      series: [{
        name: '时长',
        type: 'bar',
        data: [],
        itemStyle: { color: '#409eff' }
      }]
    })

    pieChart.setOption({
      title: { text: '训练类型分布', left: 'center' },
      tooltip: { trigger: 'item' },
      legend: { bottom: '0' },
      series: [{
        name: '训练类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: '16', fontWeight: 'bold' } },
        labelLine: { show: false },
        data: []
      }]
    })

    dietChart.setOption({
      tooltip: { 
        trigger: 'axis',
        formatter: (params) => {
          const item = params[0]
          const dataIndex = item.dataIndex
          const historyData = historyListForChart[dataIndex]
          if (!historyData) return ''
          return `${item.name}<br/>` +
                 `🔥 热量: ${historyData.calories} kcal<br/>` +
                 `🥩 蛋白质: ${historyData.protein} g<br/>` +
                 `🍞 碳水: ${historyData.carbs} g<br/>` +
                 `🥑 脂肪: ${historyData.fat} g`
        }
      },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value', name: 'kcal' },
      series: [{
        name: '热量',
        type: 'line',
        smooth: true,
        data: [],
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245, 108, 108, 0.5)' },
            { offset: 1, color: 'rgba(245, 108, 108, 0.1)' }
          ])
        },
        itemStyle: { color: '#f56c6c' }
      }]
    })

    window.addEventListener('resize', handleResize)
  }
}

const handleResize = () => {
  if (barChart && !barChart.isDisposed()) barChart.resize()
  if (pieChart && !pieChart.isDisposed()) pieChart.resize()
  if (dietChart && !dietChart.isDisposed()) dietChart.resize()
}

// 计算百分比
const calculatePercentage = (value, target) => {
  const val = parseFloat(value) || 0
  const tar = parseFloat(target) || 1
  const p = Math.round((val / tar) * 100)
  return p > 100 ? 100 : p
}

// 获取聚合首页数据
const fetchHomeData = async () => {
  const username = getUsername()
  if (!username) return
  console.log('开始获取首页数据，用户名：', username)
  try {
    const res = await fetch(`${BASE_API_URL}/api/home/data?username=${username}&date=${selectedDate.value}`)
    const resData = await res.json()
    
    if (resData.code === 0 && resData.data) {
      const data = resData.data
      Object.assign(homeData, data)
      
      const userInfo = data.userInfo || {}
      
      // 更新顶部卡片
      cardList.value[0].value = userInfo.trainingPlan || '未设置'
      
      const todayWorkouts = data.todayWorkouts || []
      if (todayWorkouts.length > 0) {
        const totalDuration = todayWorkouts.reduce((sum, w) => sum + w.duration, 0)
        cardList.value[1].value = `${totalDuration} 分钟`
        cardList.value[1].desc = `今日已打卡 ${todayWorkouts.length} 次`
      } else {
        cardList.value[1].value = '未打卡'
        cardList.value[1].desc = '点击去打卡'
      }

      // 获取累计打卡次数
      fetchWorkoutStats()

      // 获取饮食历史数据
      fetchDietHistory()

      cardList.value[3].value = data.latestBodyData ? `${data.latestBodyData.weight}kg` : '未记录'
      cardList.value[3].desc = data.latestBodyData ? `体脂率 ${data.latestBodyData.bodyFatRate}%` : '点击去记录'

      // 更新饼图 (今日训练类型分布)
      if (pieChart) {
        const typeMap = {}
        const todayWorkouts = data.todayWorkouts || []
        todayWorkouts.forEach(w => {
          typeMap[w.type] = (typeMap[w.type] || 0) + 1
        })
        const pieData = Object.keys(typeMap).map(name => ({ name, value: typeMap[name] }))
        
        pieChart.setOption({
          series: [{ data: pieData }]
        })
      }
      
      // 更新柱状图 (今日各次训练时长)
      if (barChart && !barChart.isDisposed()) {
        const todayWorkouts = data.todayWorkouts || []
        barChart.setOption({
          xAxis: { data: todayWorkouts.map((_, i) => `训练${i+1}`) },
          series: [{ data: todayWorkouts.map(w => w.duration) }]
        })
      }
    } else {
      ElMessage.warning(resData.msg || '获取首页数据失败')
    }
  } catch (err) {
    console.error('获取首页数据异常：', err)
    ElMessage.error('网络请求失败，请检查后端服务是否启动')
  }
}

const handleDateChange = (val) => {
  selectedDate.value = val
  fetchHomeData()
}

const handleCardClick = (path) => {
  if (path) router.push(path)
}

const fetchWorkoutStats = async () => {
  const username = getUsername()
  if (!username) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/workout-log/stats?username=${username}`)
    const data = await res.json()
    if (data.code === 0 && data.data) {
      cardList.value[2].value = `${data.data.totalWorkouts} 次`
    }
  } catch (err) {
    console.error('获取训练统计异常：', err)
  }
}

const fetchDietHistory = async () => {
  const username = getUsername()
  if (!username) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/diet/history?username=${username}&days=7`)
    const data = await res.json()
    if (data.code === 0 && data.data) {
      const history = data.data.reverse()
      historyListForChart = history
      if (dietChart && !dietChart.isDisposed()) {
        dietChart.setOption({
          xAxis: { 
            data: history.map(h => {
              const d = new Date(h.date)
              return `${d.getMonth() + 1}/${d.getDate()}`
            })
          },
          series: [{ data: history.map(h => h.calories) }]
        })
      }
    }
  } catch (err) {
    console.error('获取饮食历史异常：', err)
  }
}

const handleMenuClick = (item) => {
  console.log('点击菜单：', item.title)
}

onMounted(async () => {
  await nextTick()
  initCharts()
  fetchHomeData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  pieChart?.dispose()
  dietChart?.dispose()
})
</script>

<style scoped>
.home-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.scroll-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 30px;
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

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.date-picker {
  width: 150px !important;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
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

/* 欢迎区域 */
.welcome-section {
  margin: 20px 24px;
  padding: 24px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.welcome-text h2 {
  font-size: 22px;
  margin-bottom: 8px;
}

.welcome-text p {
  font-size: 14px;
  opacity: 0.9;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

/* 数据卡片区域 */
.card-area {
  padding: 0 24px 20px;
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
  transition: all 0.3s;
  cursor: pointer;
  color: #fff;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.card-0 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.card-1 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.card-2 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.card-3 { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.card-icon { font-size: 40px; opacity: 0.9; }
.card-title { font-size: 13px; opacity: 0.9; margin-bottom: 6px; }
.card-value { font-size: 20px; font-weight: 700; margin-bottom: 4px; }
.card-desc { font-size: 12px; opacity: 0.8; }

/* 联动展示区 */
.linkage-area {
  padding: 0 24px 20px;
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
  min-height: 280px;
}

.history-diet-chart-container {
  height: 200px;
  width: 100%;
}

.diet-chart {
  height: 100%;
  width: 100%;
}

.linkage-card {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.linkage-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.nutrition-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding: 10px 0;
}

.nutrition-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.progress-label {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress-label .value {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.progress-label .unit {
  font-size: 12px;
  color: #909399;
}

.item-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.right-linkage {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.body-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px;
  background: #f8f9fb;
  border-radius: 8px;
}

.info-item .label { font-size: 12px; color: #909399; }
.info-item .value { font-size: 16px; font-weight: 600; color: #303133; }

.course-count-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0;
}

.count-value { font-size: 36px; font-weight: 700; color: #409eff; }
.count-label { font-size: 14px; color: #909399; }

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 20px 0;
  font-style: italic;
}

/* 图表区域 */
.chart-area {
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-container { width: 100%; height: 300px; }

@media (max-width: 1200px) {
  .linkage-area { grid-template-columns: 1fr; }
  .card-area { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .nutrition-stats { grid-template-columns: repeat(2, 1fr); }
  .card-area { grid-template-columns: 1fr; }
}
</style>
