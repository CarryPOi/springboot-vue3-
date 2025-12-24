<template>
  <div class="sidebar" :class="{ collapsed: isCollapsed }">
    <!-- Logo区域 -->
    <div class="logo-section">
      <div class="logo-icon">🏋️</div>
      <transition name="fade">
        <span v-if="!isCollapsed" class="logo-text">健身管理</span>
      </transition>
    </div>

    <!-- 菜单列表 -->
    <el-menu
      :collapse="isCollapsed"
      :default-active="activeMenu"
      router
      class="sidebar-menu"
    >
      <el-menu-item
        v-for="item in filteredMenuList"
        :key="item.index"
        :index="item.path"
        @click="handleMenuClick(item)"
        class="menu-item"
      >
        <span class="menu-icon">{{ item.emoji }}</span>
        <template #title>
          <span class="menu-title">{{ item.title }}</span>
        </template>
      </el-menu-item>
    </el-menu>

    <!-- 折叠按钮 -->
    <div class="collapse-section">
      <el-button
        class="collapse-btn"
        @click="toggleCollapse"
        circle
      >
        <el-icon :size="18">
          <Expand v-if="isCollapsed" />
          <Fold v-else />
        </el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Expand, Fold } from '@element-plus/icons-vue'
import { useUserInfo } from '@/utils/storage'

const props = defineProps({
  isCollapsed: { type: Boolean, default: false }
})

const emit = defineEmits(['update:isCollapsed', 'menu-click'])

const toggleCollapse = () => {
  emit('update:isCollapsed', !props.isCollapsed)
}

const handleMenuClick = (item) => {
  emit('menu-click', item)
}

const menuList = [
  // 用户功能
  { index: '1', title: '首页', emoji: '🏠', path: '/home', userOnly: true },
  { index: '2', title: '个人中心', emoji: '👤', path: '/user', userOnly: true },
  { index: '3', title: '训练计划', emoji: '📅', path: '/training-plan', userOnly: true },
  { index: '4', title: '身材数据', emoji: '📊', path: '/body-data', userOnly: true },
  { index: '5', title: '论坛广场', emoji: '💬', path: '/forum', userOnly: true },
  { index: '6', title: '每日公告', emoji: '📢', path: '/notice', userOnly: true },
  { index: '7', title: '每日饮食', emoji: '🥗', path: '/diet', userOnly: true },
  { index: '8', title: '健康百科', emoji: '📖', path: '/knowledge', userOnly: true },
  { index: '9', title: '课程订阅', emoji: '📚', path: '/courses', userOnly: true },
  { index: '10', title: '训练打卡', emoji: '🔥', path: '/workout-log', userOnly: true },
  { index: '11', title: '用户反馈', emoji: '✉️', path: '/feedback', userOnly: true },
  
  // 管理员功能
  { index: '12', title: '管理概览', emoji: '📊', path: '/admin', adminOnly: true },
  { index: '13', title: '用户管理', emoji: '👥', path: '/admin/users', adminOnly: true },
  { index: '14', title: '知识管理', emoji: '📖', path: '/admin/knowledge', adminOnly: true },
  { index: '15', title: '课程管理', emoji: '🏫', path: '/admin/courses', adminOnly: true },
  { index: '16', title: '公告管理', emoji: '📢', path: '/admin/notices', adminOnly: true },
  { index: '17', title: '评论管理', emoji: '💬', path: '/admin/comments', adminOnly: true },
  { index: '18', title: '饮食推荐', emoji: '🥗', path: '/admin/diet', adminOnly: true },
  { index: '19', title: '反馈管理', emoji: '📨', path: '/admin/feedback', adminOnly: true }
]

const userInfo = useUserInfo()

// 根据角色过滤菜单
const filteredMenuList = computed(() => {
  const role = userInfo.value.role || 'USER'
  return menuList.filter(item => {
    if (item.adminOnly && role !== 'ADMIN') return false
    if (item.userOnly && role === 'ADMIN') return false
    return true
  })
})

const route = useRoute()
const activeMenu = computed(() => {
  const match = menuList.find(item => item.path === route.path)
  return match ? match.path : '/home'
})
</script>

<style scoped>
.sidebar {
  width: 220px;
  height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 70px;
}

/* Logo区域 */
.logo-section {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 15px;
}

.logo-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 菜单 */
.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 15px 0;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 5px 10px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.sidebar-menu :deep(.el-menu--collapse) {
  width: 70px;
}

.sidebar-menu :deep(.el-menu--collapse .el-menu-item) {
  padding: 0 !important;
  justify-content: center;
  margin: 5px;
}

.menu-icon {
  font-size: 20px;
  margin-right: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
}

.sidebar.collapsed .menu-icon {
  margin-right: 0;
}

.menu-title {
  font-size: 14px;
  font-weight: 500;
}

/* 折叠按钮 */
.collapse-section {
  padding: 15px;
  display: flex;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapse-btn {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

/* 动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
