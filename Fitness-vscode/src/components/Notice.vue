<template>
  <div class="notice-layout">
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
          <span class="title-icon">📢</span>
          <span>每日公告</span>
        </div>
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

      <!-- 公告列表区域 -->
      <div class="notice-container">
        <div class="notice-main">
          <!-- 搜索与筛选 -->
          <div class="filter-bar">
            <el-input 
              v-model="searchKey" 
              placeholder="搜索公告标题..." 
              prefix-icon="Search"
              clearable
              style="width: 100%"
            />
          </div>

          <!-- 公告时间轴列表 -->
          <el-timeline class="notice-timeline" v-if="filteredNoticeList.length > 0">
            <el-timeline-item
              v-for="(item, index) in filteredNoticeList"
              :key="item.id"
              :timestamp="formatTime(item.createTime)"
              :type="getTimelineType(item.type)"
              :hollow="index === 0"
              placement="top"
            >
              <el-card class="notice-card" :class="'type-' + item.type">
                <template #header>
                  <div class="notice-header">
                    <div class="header-left">
                      <el-tag :type="getTagType(item.type)" effect="dark" size="small" class="type-tag">
                        {{ getTypeName(item.type) }}
                      </el-tag>
                      <span class="notice-title">{{ item.title }}</span>
                    </div>
                    <div class="header-right">
                      <span class="author-info">
                        <el-icon><User /></el-icon> {{ item.author }}
                      </span>
                    </div>
                  </div>
                </template>
                <div class="notice-content">
                  {{ item.content }}
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <el-empty description="暂无公告通知" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, SwitchButton, Search, Notification } from '@element-plus/icons-vue'
import Sidebar from '@/components/Sidebar.vue'
import { useUserInfo } from '@/utils/storage'
import { logout as commonLogout, getFullUrl, BASE_API_URL, formatTime as centralizedFormatTime } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const searchKey = ref('')

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const noticeList = ref([])

const logout = () => {
  commonLogout()
}

// 获取公告列表
const fetchNoticeList = async () => {
  try {
    const res = await fetch(`${BASE_API_URL}/api/notice/list`)
    const data = await res.json()
    if (data.code === 0) {
      noticeList.value = data.data
    } else {
      ElMessage.error(data.msg || '获取公告失败')
    }
  } catch (err) {
    console.error('获取公告异常：', err)
    ElMessage.error('网络错误，无法获取公告')
  }
}

// 筛选公告
const filteredNoticeList = computed(() => {
  if (!searchKey.value) return noticeList.value
  return noticeList.value.filter(item => 
    item.title.includes(searchKey.value) || 
    item.content.includes(searchKey.value)
  )
})

// 工具函数
const formatTime = (time) => centralizedFormatTime(time)

const getTimelineType = (type) => {
  switch (type) {
    case 0: return 'primary' // 系统
    case 1: return 'success' // 活动
    case 2: return 'warning' // 维护
    default: return 'info'
  }
}

const getTagType = (type) => {
  switch (type) {
    case 0: return '' 
    case 1: return 'success'
    case 2: return 'warning'
    default: return 'info'
  }
}

const getTypeName = (type) => {
  switch (type) {
    case 0: return '系统公告'
    case 1: return '活动通知'
    case 2: return '维护通知'
    default: return '普通消息'
  }
}

const handleMenuClick = (item) => {
  // 已经在Sidebar中处理了路由跳转
}

onMounted(() => {
  fetchNoticeList()
})
</script>

<style scoped>
.notice-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止主内容区域整体出现滚动条 */
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
  flex-shrink: 0;
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

.user-name { color: #606266; }

/* 公告内容区布局 */
.notice-container {
  flex: 1;
  padding: 24px;
  overflow-y: auto; /* 只有这个容器负责垂直滚动 */
  display: flex;
  justify-content: center; /* 居中显示公告 */
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.notice-main {
  width: 100%;
  max-width: 800px; /* 限制公告区域宽度，避免太宽难看 */
  min-width: 0;
  overflow: visible;
}

.filter-bar {
  margin-bottom: 24px;
}

.notice-timeline {
  padding-left: 20px;
}

.notice-card {
  border-radius: 12px;
  transition: all 0.3s;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.notice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  font-size: 13px;
  color: #909399;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.notice-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 不同类型的卡片边框装饰 */
.type-0 { border-left: 4px solid #409eff; }
.type-1 { border-left: 4px solid #67c23a; }
.type-2 { border-left: 4px solid #e6a23c; }

/* 空状态 */
.empty-state {
  padding: 60px 0;
  display: flex;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .notice-header {
    align-items: flex-start;
  }
}
</style>
