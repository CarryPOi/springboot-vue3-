<template>
  <div class="layout">
    <!-- 左侧侧边栏 -->
    <Sidebar v-model:isCollapsed="isCollapsed" />

    <!-- 右侧主区域 -->
    <div class="main">
      <!-- 顶部栏 -->
      <div class="top-bar">
        <div>健身管理系统</div>
        <el-dropdown>
          <span>{{ displayName }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <!-- 内容区：论坛 / 计划 / 数据 -->
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Sidebar from '@/components/Sidebar.vue'

const router = useRouter()
const isCollapsed = ref(false)

const displayName = computed(() => {
  try {
    const lsInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    return lsInfo.realName || lsInfo.username || '用户'
  } catch (e) {
    return '用户'
  }
})

const logout = () => {
  localStorage.removeItem('userInfo')
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  width: 100vw;
  height: 100vh;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.top-bar {
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  background: #fff;
}

.content {
  flex: 1;
  overflow: auto;
  background: #f9f9f9;
}
</style>
