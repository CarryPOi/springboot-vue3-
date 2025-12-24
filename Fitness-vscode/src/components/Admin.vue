<template>
  <div class="admin-layout">
    <Sidebar v-model:isCollapsed="isCollapsed" />
    <div class="main-content">
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">{{ currentRouteEmoji }}</span>
          <span>{{ currentRouteTitle }}</span>
        </div>
        <div class="top-right">
          <el-tag type="danger" effect="dark" style="margin-right: 15px">管理员</el-tag>
          <el-dropdown class="user-dropdown-container">
            <span class="user-dropdown">
              <el-avatar :size="32" class="user-avatar" :src="displayAvatarUrl">
                {{ displayName.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-name-text">{{ displayName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <div class="content-area">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import { ElMessage } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { logout as commonLogout, getFullUrl } from '@/utils/common'

const router = useRouter()
const route = useRoute()
const isCollapsed = ref(false)

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || 'Admin'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const routeMap = {
  '/admin': { title: '管理概览', emoji: '📊' },
  '/admin/users': { title: '用户管理', emoji: '👥' },
  '/admin/courses': { title: '课程管理', emoji: '🏫' },
  '/admin/notices': { title: '公告管理', emoji: '📢' },
  '/admin/comments': { title: '评论管理', emoji: '💬' },
  '/admin/diet': { title: '饮食推荐', emoji: '🥗' },
  '/admin/feedback': { title: '用户反馈', emoji: '📨' }
}

const currentRouteTitle = computed(() => {
  return routeMap[route.path]?.title || '管理中心'
})

const currentRouteEmoji = computed(() => {
  return routeMap[route.path]?.emoji || '⚙️'
})

const logout = () => {
  commonLogout()
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background-color: #f0f2f5;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.top-bar {
  height: 70px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.title-icon {
  font-size: 24px;
}

.top-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 8px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-dropdown:hover {
  background: #f5f7fa;
}

.user-name-text {
  font-size: 14px;
  color: #606266;
}

.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #f0f2f5;
}

/* 动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>