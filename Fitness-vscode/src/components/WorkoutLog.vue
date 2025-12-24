<template>
  <div class="workout-log-layout">
    <Sidebar v-model:isCollapsed="isCollapsed" />
    
    <div class="main-content">
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">🔥</span>
          <span>训练打卡记录</span>
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

      <div class="scroll-content">
        <!-- 统计看板 -->
        <div class="stats-overview">
          <div class="stat-card">
            <div class="stat-icon">📅</div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalWorkouts || 0 }} 次</div>
              <div class="stat-label">累计打卡</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">⏱️</div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalDuration || 0 }} min</div>
              <div class="stat-label">累计时长</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">⚡</div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCalories || 0 }} kcal</div>
              <div class="stat-label">累计消耗</div>
            </div>
          </div>
        </div>

        <div class="content-wrapper">
          <!-- 左侧：添加打卡 -->
          <div class="left-panel">
            <el-card class="form-card">
              <template #header>
                <div class="card-header">
                  <span>📝 新增训练日志</span>
                </div>
              </template>
              <el-form :model="logForm" label-position="top">
                <el-form-item label="训练名称">
                  <el-input v-model="logForm.workoutName" placeholder="如：今日胸肩训练" />
                </el-form-item>
                
                <el-row :gutter="15">
                  <el-col :span="12">
                    <el-form-item label="训练类型">
                      <el-select v-model="logForm.type" placeholder="选择类型" style="width: 100%" @change="autoCalculateCalories">
                        <el-option label="力量训练" value="力量训练" />
                        <el-option label="有氧运动" value="有氧运动" />
                        <el-option label="拉伸/瑜伽" value="拉伸/瑜伽" />
                        <el-option label="其他" value="其他" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="训练强度">
                      <el-radio-group v-model="logForm.intensity" size="small" @change="autoCalculateCalories">
                        <el-radio-button label="轻松" />
                        <el-radio-button label="适中" />
                        <el-radio-button label="高强度" />
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  </el-row>

                <el-row :gutter="15">
                  <el-col :span="12">
                    <el-form-item label="训练日期">
                      <el-date-picker v-model="logForm.recordDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="时长 (min)">
                      <el-input-number v-model="logForm.duration" :min="1" style="width: 100%" @change="autoCalculateCalories" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="预计消耗 (kcal)">
                  <div style="display: flex; gap: 10px; width: 100%">
                    <el-input-number v-model="logForm.calories" :min="0" style="flex: 1" />
                    <el-tooltip content="基于MET模型自动计算热量消耗">
                      <el-button type="success" plain @click="autoCalculateCalories">自动计算</el-button>
                    </el-tooltip>
                  </div>
                  <div v-if="userWeight" style="font-size: 12px; color: #909399; margin-top: 4px">
                    当前体重: {{ userWeight }}kg (数据来自身材记录)
                  </div>
                </el-form-item>

                <el-form-item label="训练详情 (备注)">
                  <el-input 
                    v-model="logForm.content" 
                    type="textarea" 
                    :rows="3" 
                    placeholder="记录动作、组数、重量等..."
                  />
                </el-form-item>

                <el-button type="primary" @click="saveLog" :loading="saving" style="width: 100%; height: 45px; margin-top: 10px">
                  保存今日打卡
                </el-button>
              </el-form>
            </el-card>
          </div>

          <!-- 右侧：历史日志 -->
          <div class="right-panel">
            <el-card class="list-card">
              <template #header>
                <div class="card-header">
                  <span>📋 历史日志</span>
                </div>
              </template>
              <el-timeline v-if="logs.length > 0">
                <el-timeline-item
                  v-for="log in logs"
                  :key="log.id"
                  :timestamp="log.recordDate"
                  placement="top"
                  type="primary"
                >
                  <el-card shadow="hover" class="log-item">
                    <div class="log-header">
                      <h4>{{ log.workoutName }}</h4>
                      <div class="log-tags">
                        <el-tag size="small">{{ log.type }}</el-tag>
                        <el-tag size="small" type="info" v-if="log.intensity">{{ log.intensity }}</el-tag>
                      </div>
                    </div>
                    <div class="log-details">
                      <span>⏱️ {{ log.duration }} min</span>
                      <span>⚡ {{ log.calories }} kcal</span>
                    </div>
                    <p class="log-content">{{ log.content }}</p>
                    <div class="log-actions">
                      <el-button type="danger" link size="small" @click="deleteLog(log.id)">删除</el-button>
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-else description="暂无打卡记录" />
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from './Sidebar.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, SwitchButton } from '@element-plus/icons-vue'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { getUsername, logout as commonLogout, getFullUrl, BASE_API_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const saving = ref(false)
const logs = ref([])
const stats = ref({ totalWorkouts: 0, totalDuration: 0, totalCalories: 0 })
const userWeight = ref(70) // 默认体重

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const logout = () => {
  commonLogout()
}

const logForm = ref({
  username: getUsername(),
  workoutName: '',
  recordDate: new Date().toISOString().split('T')[0],
  duration: 60,
  calories: 0,
  type: '力量训练',
  intensity: '适中',
  content: ''
})

// 获取用户最新体重
const fetchUserWeight = async () => {
  const username = getUsername()
  if (!username) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/user/info/get?username=${username}`)
    const data = await res.json()
    if (data.code === 0 && data.data && data.data.weight) {
      userWeight.value = data.data.weight
    }
  } catch (e) {
    console.error('获取体重失败', e)
  }
}

// 基于 MET 自动计算热量
const autoCalculateCalories = () => {
  // MET 系数映射表
  const metMap = {
    '力量训练': { '轻松': 3.0, '适中': 5.0, '高强度': 8.0 },
    '有氧运动': { '轻松': 4.0, '适中': 7.0, '高强度': 10.0 },
    '拉伸/瑜伽': { '轻松': 2.0, '适中': 2.5, '高强度': 3.5 },
    '其他': { '轻松': 3.0, '适中': 4.5, '高强度': 6.0 }
  }

  const type = logForm.value.type || '其他'
  const intensity = logForm.value.intensity || '适中'
  const durationHour = (logForm.value.duration || 0) / 60
  
  const met = metMap[type][intensity]
  // 公式: 消耗 = MET * 体重(kg) * 时间(小时)
  const result = Math.round(met * userWeight.value * durationHour)
  logForm.value.calories = result
}

const fetchLogs = async () => {
  const username = getUsername()
  if (!username) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/workout-log/list?username=${username}`)
    const data = await res.json()
    if (data.code === 0) logs.value = data.data
  } catch (e) {
    console.error(e)
  }
}

const fetchStats = async () => {
  const username = getUsername()
  if (!username) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/workout-log/stats?username=${username}`)
    const data = await res.json()
    if (data.code === 0) stats.value = data.data
  } catch (e) {
    console.error(e)
  }
}

const saveLog = async () => {
  if (!logForm.value.workoutName) {
    ElMessage.warning('请输入训练名称')
    return
  }
  // 确保提交时使用最新的用户名
  logForm.value.username = getUsername()
  saving.value = true
  try {
    const res = await fetch(`${BASE_API_URL}/api/workout-log/save`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(logForm.value)
    })
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('打卡成功！')
      resetForm()
      fetchLogs()
      fetchStats()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const deleteLog = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条日志吗？')
    const res = await fetch(`${BASE_API_URL}/api/workout-log/delete?id=${id}`, { method: 'POST' })
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('删除成功')
      fetchLogs()
      fetchStats()
    }
  } catch (e) {}
}

const resetForm = () => {
  logForm.value = {
    username: getUsername(),
    workoutName: '',
    recordDate: new Date().toISOString().split('T')[0],
    duration: 60,
    calories: 0,
    type: '力量训练',
    intensity: '适中',
    content: ''
  }
  autoCalculateCalories()
}

onMounted(() => {
  fetchUserWeight().then(() => {
    autoCalculateCalories() // 初始计算一次
  })
  fetchLogs()
  fetchStats()
})
</script>

<style scoped>
.workout-log-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f4f7f6;
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
  padding: 24px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  position: sticky;
  top: 0;
  z-index: 100;
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
  font-size: 24px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.stat-icon {
  font-size: 32px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
}

.content-wrapper {
  display: flex;
  gap: 20px;
}

.left-panel {
  width: 400px;
}

.right-panel {
  flex: 1;
}

.log-item {
  margin-bottom: 10px;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.log-tags {
  display: flex;
  gap: 8px;
}

.log-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.log-details {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 10px;
}

.log-content {
  font-size: 14px;
  color: #475569;
  white-space: pre-wrap;
  background: #f8fafc;
  padding: 10px;
  border-radius: 6px;
}

.log-actions {
  text-align: right;
  margin-top: 10px;
}
</style>
