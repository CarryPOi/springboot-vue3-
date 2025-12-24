<template>
  <div class="admin-dashboard">
    <!-- 统计看板 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="stat in stats" :key="stat.title">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-icon">{{ stat.icon }}</div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 管理功能区 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="admin-main-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">系统管理概览</span>
              <el-button type="primary" link>查看详情</el-button>
            </div>
          </template>
          <div class="admin-welcome">
            <div class="welcome-img">🚀</div>
            <h3>欢迎进入管理后台</h3>
            <p>在这里您可以管理用户数据、发布系统公告、维护健身课程以及监控全站运行状态。</p>
            
            <div class="quick-actions">
              <el-button type="primary" plain @click="$router.push('/admin/users')">用户管理</el-button>
              <el-button type="success" plain @click="$router.push('/admin/courses')">课程管理</el-button>
              <el-button type="warning" plain @click="$router.push('/admin/notices')">公告发布</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="admin-aside-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">系统日志</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(log, index) in logs"
              :key="index"
              :type="log.type"
              :timestamp="log.time"
            >
              {{ log.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminGetUsers, adminGetNotices, adminGetCourses } from '@/api/admin'

const stats = ref([
  { title: '总用户数', value: '0', icon: '👥' },
  { title: '系统公告', value: '0', icon: '📢' },
  { title: '健身课程', value: '0', icon: '📚' },
  { title: '活跃指数', value: '98%', icon: '📈' }
])

const logs = ref([
  { content: '系统维护更新完成', time: '2025-12-23 10:00', type: 'success' },
  { content: '管理员发布了新课程', time: '2025-12-23 15:20', type: 'primary' },
  { content: '用户 hehuicong 登录后台', time: '2025-12-23 14:15', type: 'primary' },
  { content: '数据库备份成功', time: '2025-12-22 04:00', type: 'info' }
])

const fetchStats = async () => {
  try {
    const [users, notices, courses] = await Promise.all([
      adminGetUsers(),
      adminGetNotices(),
      adminGetCourses()
    ])
    if (users.code === 200) stats.value[0].value = users.data.length.toString()
    if (notices.code === 200) stats.value[1].value = notices.data.length.toString()
    if (courses.code === 200) stats.value[2].value = courses.data.length.toString()
  } catch (error) {
    console.error('Fetch stats error:', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  border-radius: 12px;
  border: none;
  background: #fff;
}

.stat-icon {
  font-size: 40px;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.admin-main-card, .admin-aside-card {
  border-radius: 12px;
  border: none;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-weight: 600;
  font-size: 16px;
}

.admin-welcome {
  text-align: center;
  padding: 40px 20px;
}

.welcome-img {
  font-size: 60px;
  margin-bottom: 20px;
}

.admin-welcome h3 {
  font-size: 22px;
  color: #1a1a2e;
  margin-bottom: 10px;
}

.admin-welcome p {
  color: #606266;
  margin-bottom: 30px;
}

.quick-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>