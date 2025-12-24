<template>
  <div class="user-layout">
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
          <span class="title-icon">👤</span>
          <span>个人中心</span>
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
              <el-dropdown-item @click="logout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <!-- 用户头像和基本信息卡片 -->
      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="100" class="large-avatar" :src="displayAvatarUrl">
            {{ displayName.charAt(0).toUpperCase() }}
          </el-avatar>
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :data="{ username: userName }"
            name="file"
            :on-success="handleAvatarSuccess"
            :on-error="handleAvatarError"
            :before-upload="beforeAvatarUpload">
            <el-button type="primary" size="small">上传头像</el-button>
          </el-upload>
          <div class="user-basic">
            <h2>{{ displayName }}</h2>
            <p>@{{ userName }}</p>
            <div class="user-tags">
              <el-tag v-if="userForm.trainingType" type="primary" size="small">
                {{ userForm.trainingType }}
              </el-tag>
              <el-tag v-if="userForm.age" type="info" size="small">
                {{ userForm.age }}岁
              </el-tag>
            </div>
          </div>
        </div>
        <div class="stats-overview">
          <div class="stat-item">
            <div class="stat-value">{{ userForm.height || '--' }}</div>
            <div class="stat-label">身高(cm)</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userForm.weight || '--' }}</div>
            <div class="stat-label">体重(kg)</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userForm.trainingDuration || '--' }}</div>
            <div class="stat-label">训练时长(分钟)</div>
          </div>
        </div>
      </div>

      <!-- 表单区域 -->
      <div class="forms-container">
        <!-- 训练信息 -->
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span class="card-icon">🏋️</span>
              <span class="card-title">训练信息</span>
            </div>
          </template>
          <el-form :model="userForm" label-width="120px" label-position="top">
            <el-form-item label="训练计划">
              <el-input 
                v-model="userForm.trainingPlan" 
                type="textarea"
                :rows="3"
                placeholder="请描述您的训练计划，如：每周三次力量训练，两次有氧运动" 
              />
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="训练时长(分钟)">
                  <el-input-number 
                    v-model="userForm.trainingDuration" 
                    :min="0" 
                    :max="1000"
                    placeholder="每次训练时长"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="训练类型">
                  <el-select 
                    v-model="userForm.trainingType" 
                    placeholder="选择训练类型"
                    style="width: 100%;"
                    clearable
                  >
                    <el-option label="力量训练" value="力量训练" />
                    <el-option label="有氧运动" value="有氧运动" />
                    <el-option label="HIIT" value="HIIT" />
                    <el-option label="瑜伽" value="瑜伽" />
                    <el-option label="柔韧性训练" value="柔韧性训练" />
                    <el-option label="核心训练" value="核心训练" />
                    <el-option label="综合训练" value="综合训练" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>

        <!-- 基本信息 -->
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span class="card-icon">📋</span>
              <span class="card-title">基本信息</span>
            </div>
          </template>
          <el-form :model="userForm" label-width="120px" label-position="top">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <el-input v-model="userForm.username" disabled>
                    <template #prefix>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名">
                  <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="年龄">
                  <el-input-number 
                    v-model="userForm.age" 
                    :min="1" 
                    :max="120"
                    placeholder="年龄"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="身高(cm)">
                  <el-input-number 
                    v-model="userForm.height" 
                    :min="50" 
                    :max="250"
                    :precision="1"
                    placeholder="身高"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="体重(kg)">
                  <el-input-number 
                    v-model="userForm.weight" 
                    :min="20" 
                    :max="300"
                    :precision="1"
                    placeholder="体重"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" placeholder="请输入手机号">
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 保存按钮 -->
      <div class="save-area">
        <el-button type="primary" size="large" :loading="saving" @click="saveUserInfo">
          <el-icon><Check /></el-icon>
          {{ saving ? '保存中...' : '保存所有信息' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Phone, Check, SwitchButton } from '@element-plus/icons-vue'

import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { getFullUrl, BASE_API_URL, logout as commonLogout } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const saving = ref(false)

// 使用共享的响应式用户信息
const userInfo = useUserInfo()

const userName = computed(() => userInfo.value.username || 'user')

const displayName = computed(() => {
  return userForm.value.realName || userInfo.value.realName || userName.value
})

const displayAvatarUrl = computed(() => {
  const url = userForm.value.avatarUrl || userInfo.value.avatarUrl
  return getFullUrl(url)
})
const uploadUrl = `${BASE_API_URL}/api/user/info/upload-avatar`

const userForm = ref({
  id: '',
  username: userInfo.value.username || '',
  realName: '',
  age: null,
  height: null,
  weight: null,
  phone: '',
  trainingPlan: '',
  trainingDuration: null,
  trainingType: '',
  avatarUrl: ''
})

const handleMenuClick = (item) => {
  console.log('点击菜单：', item.title)
}

const logout = () => {
  commonLogout()
}

const getUserInfo = async () => {
  if (!userName.value) return
  try {
    const res = await fetch(`${BASE_API_URL}/api/user/info/get?username=${userName.value}`)
    const data = await res.json()
    if ((data.code === 0 || data.code === 200) && data.data) {
      userForm.value = { ...userForm.value, ...data.data }
      
      // 同步到 localStorage
      try {
        const lsInfo = { ...userInfo.value }
        let changed = false
        
        if (data.data.avatarUrl && data.data.avatarUrl !== lsInfo.avatarUrl) {
          lsInfo.avatarUrl = data.data.avatarUrl
          changed = true
        }
        if (data.data.realName && data.data.realName !== lsInfo.realName) {
          lsInfo.realName = data.data.realName
          changed = true
        }
        
        if (changed) {
          updateUserInfo(lsInfo)
        }
      } catch (e) {
        console.error('同步本地存储失败：', e)
      }
    }
  } catch (err) {
    console.error('查询个人信息失败：', err)
  }
}

const saveUserInfo = async () => {
  if (!userForm.value.username) {
    ElMessage.warning('用户名不能为空')
    return
  }
  saving.value = true
  try {
    const res = await fetch(`${BASE_API_URL}/api/user/info/save`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify(userForm.value)
    })
    const data = await res.json()
    if (data.code === 0 || data.code === 200) {
      ElMessage.success('保存成功！')
      // 更新 localStorage 中的用户信息，确保其他页面同步显示
      try {
        const lsInfo = { ...userInfo.value }
        lsInfo.realName = userForm.value.realName
        lsInfo.avatarUrl = userForm.value.avatarUrl
        updateUserInfo(lsInfo)
      } catch (e) {
        console.error('更新本地存储失败：', e)
      }
      getUserInfo() // 重新获取最新信息
    } else {
      ElMessage.error(data.msg || '保存失败')
    }
  } catch (err) {
    console.error('保存失败：', err)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  getUserInfo()
})

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('只能上传图片文件')
  if (!isLt2M) ElMessage.error('图片大小不能超过2MB')
  return isImage && isLt2M
}

const handleAvatarSuccess = (response) => {
  let resp = response
  if (typeof resp === 'string') {
    try { resp = JSON.parse(resp) } catch (e) {}
  }
  const url = resp?.data?.avatarUrl || resp?.avatarUrl
  if (url) {
    userForm.value.avatarUrl = url
    try {
      const lsInfo = { ...userInfo.value }
      lsInfo.avatarUrl = url
      updateUserInfo(lsInfo)
    } catch (e) {}
    getUserInfo()
    ElMessage.success('头像更新成功')
  } else {
    ElMessage.warning('上传成功，但未返回头像地址')
  }
}

const handleAvatarError = (err) => {
  ElMessage.error('上传失败，请稍后重试')
  console.error('上传失败', err)
}
</script>

<style scoped>
.user-layout {
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

.title-icon {
  font-size: 20px;
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

.user-name-text {
  color: #606266;
}

/* 头像区域 */
.profile-header {
  margin: 24px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.large-avatar {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 40px;
  font-weight: 600;
  border: 4px solid rgba(255, 255, 255, 0.3);
}

.user-basic h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.user-basic p {
  opacity: 0.8;
  margin-bottom: 12px;
}

.user-tags {
  display: flex;
  gap: 8px;
}

.user-tags .el-tag {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
}

.stats-overview {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.85;
}

/* 表单区域 */
.forms-container {
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-card {
  border-radius: 12px;
  overflow: hidden;
}

.form-card :deep(.el-card__header) {
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-icon {
  font-size: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.form-card :deep(.el-card__body) {
  padding: 20px;
}

.form-card :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

/* 保存按钮区域 */
.save-area {
  padding: 24px;
  display: flex;
  justify-content: center;
}

.save-area .el-button {
  min-width: 200px;
  height: 48px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.save-area .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

/* 响应式 */
@media (max-width: 1200px) {
  .forms-container {
    grid-template-columns: 1fr;
  }
  
  .profile-header {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
  
  .avatar-section {
    flex-direction: column;
  }
  
  .stats-overview {
    width: 100%;
    justify-content: space-around;
  }
}
</style>
