<template>
  <div class="course-layout">
    <Sidebar v-model:isCollapsed="isCollapsed" />

    <div class="main-content">
      <!-- 顶部栏 -->
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">📚</span>
          <span>课程订阅</span>
        </div>
        <div class="top-right">
          <div class="filter-options">
            <el-radio-group v-model="filterCategory" size="small">
              <el-radio-button label="全部" />
              <el-radio-button label="减脂" />
              <el-radio-button label="增肌" />
              <el-radio-button label="瑜伽" />
              <el-radio-button label="力量" />
            </el-radio-group>
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
        <!-- 统计栏 -->
        <div class="subscription-stats">
          <el-alert
            title="订阅说明"
            type="info"
            description="选择你感兴趣的课程点击订阅，订阅后课程将同步至你的个人训练中心。部分高级课程需要坚持基础训练后解锁。"
            show-icon
            :closable="false"
          />
        </div>

        <!-- 课程网格 -->
        <div class="course-grid">
          <el-card 
            v-for="course in filteredCourseList" 
            :key="course.id" 
            class="course-card" 
            :body-style="{ padding: '0px', display: 'flex', flexDirection: 'column', height: '100%' }"
            shadow="hover"
          >
            <div class="course-cover">
              <img :src="course.coverImage" class="image" />
              <div class="course-tag">{{ course.category }}</div>
            </div>
            <div class="course-info">
              <div class="course-title-row">
                <span class="course-title">{{ course.title }}</span>
                <el-tag :type="getDifficultyType(course.difficulty)" size="small">
                  {{ course.difficulty }}
                </el-tag>
              </div>
              <p class="course-desc">{{ course.description }}</p>
              <div class="course-meta">
                <span><el-icon><User /></el-icon> {{ course.trainer }}</span>
                <span><el-icon><Timer /></el-icon> {{ course.duration }}分钟</span>
              </div>
              <div class="course-action">
                <el-button 
                  v-if="!course.isSubscribed" 
                  type="primary" 
                  class="action-btn"
                  @click="handleSubscribe(course)"
                >
                  立即订阅
                </el-button>
                <el-button 
                  v-else 
                  type="success" 
                  plain 
                  class="action-btn"
                  @click="handleUnsubscribe(course)"
                >
                  已订阅 (取消)
                </el-button>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="filteredCourseList.length === 0" description="暂无相关课程" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Timer, SwitchButton } from '@element-plus/icons-vue'
import Sidebar from '@/components/Sidebar.vue'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { getUsername, logout as commonLogout, getFullUrl, BASE_API_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const courseList = ref([])
const filterCategory = ref('全部')

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const logout = () => {
  commonLogout()
}

const activeTab = ref('all')

const fetchCourses = async () => {
  const currentUsername = getUsername()
  if (!currentUsername) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/course/list?username=${currentUsername}`)
    const data = await res.json()
    if (data.code === 0) {
      courseList.value = data.data
    }
  } catch (e) {
    ElMessage.error('获取课程列表失败')
  }
}

const filteredCourseList = computed(() => {
  if (filterCategory.value === '全部') return courseList.value
  return courseList.value.filter(c => c.category === filterCategory.value)
})

const getDifficultyType = (diff) => {
  switch (diff) {
    case '初级': return 'success'
    case '中级': return 'warning'
    case '高级': return 'danger'
    default: return 'info'
  }
}

const handleSubscribe = async (course) => {
  const currentUsername = getUsername()
  try {
    const res = await fetch(`${BASE_API_URL}/api/course/subscribe`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: currentUsername, courseId: course.id })
    })
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success(`订阅成功：${course.title}`)
      fetchCourses()
    } else {
      ElMessage.error(data.msg)
    }
  } catch (e) {
    ElMessage.error('订阅失败')
  }
}

const handleUnsubscribe = (course) => {
  const currentUsername = getUsername()
  ElMessageBox.confirm(`确定取消订阅《${course.title}》吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await fetch(`${BASE_API_URL}/api/course/unsubscribe`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: currentUsername, courseId: course.id })
      })
      const data = await res.json()
      if (data.code === 0) {
        ElMessage.success('已取消订阅')
        fetchCourses()
      } else {
        ElMessage.error(data.msg)
      }
    } catch (e) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.course-layout {
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
  padding: 24px;
  overflow-y: auto;
}

.subscription-stats {
  margin-bottom: 24px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.course-card {
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
  border: none;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-cover {
  flex-shrink: 0;
  position: relative;
  height: 160px;
  overflow: hidden;
}

.course-cover .image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.course-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
}

.course-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.course-desc {
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
  margin: 8px 0 16px;
  height: 40px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #606266;
  margin-bottom: 16px;
}

.course-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-action {
  margin-top: auto;
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.action-btn {
  width: 100%;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .course-grid {
    grid-template-columns: 1fr;
  }
}
</style>
