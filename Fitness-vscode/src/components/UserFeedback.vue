<template>
  <div class="feedback-layout">
    <Sidebar v-model:isCollapsed="isCollapsed" />
    
    <div class="main-content">
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">✉️</span>
          <span>用户反馈</span>
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
        <div class="content-wrapper">
          <el-card class="feedback-card">
            <template #header>
              <div class="card-header">
                <span>📝 提交您的意见或建议</span>
              </div>
            </template>
            
            <el-form :model="feedbackForm" :rules="rules" ref="feedbackFormRef" label-position="top">
              <el-form-item label="反馈类型" prop="type">
                <el-radio-group v-model="feedbackForm.type">
                  <el-radio-button label="Bug反馈" />
                  <el-radio-button label="功能建议" />
                  <el-radio-button label="吐槽" />
                  <el-radio-button label="其他" />
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="反馈内容" prop="content">
                <el-input
                  v-model="feedbackForm.content"
                  type="textarea"
                  :rows="6"
                  placeholder="请详细描述您遇到的问题或建议，我们会认真阅读每一条反馈..."
                />
              </el-form-item>
              
              <div class="submit-section">
                <el-button type="primary" :loading="submitting" @click="submitFeedback" size="large">
                  提交反馈
                </el-button>
                <p class="hint">您的反馈仅管理员可见，我们将尽快处理您的建议。</p>
              </div>
            </el-form>
          </el-card>

          <div class="feedback-info">
            <h3>常见问题解答</h3>
            <el-collapse>
              <el-collapse-item title="反馈后多久能得到处理？" name="1">
                <div>我们通常会在 1-3 个工作日内查看并处理您的反馈。对于重大的 Bug，我们会立即进行修复。</div>
              </el-collapse-item>
              <el-collapse-item title="我的反馈在哪里可以查看结果？" name="2">
                <div>目前反馈处理结果将直接体现在系统更新中。如果您留下了联系方式（可在反馈内容中注明），我们也可能直接联系您。</div>
              </el-collapse-item>
              <el-collapse-item title="如何提交更有效的反馈？" name="3">
                <div>请尽可能详细地描述问题发生的场景、步骤，如果能附带您的操作环境（如浏览器版本等）会更有助于我们定位问题。</div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, SwitchButton, ChatLineRound, Message, Check } from '@element-plus/icons-vue'
import Sidebar from '@/components/Sidebar.vue'
import { useUserInfo } from '@/utils/storage'
import { logout as commonLogout, getFullUrl, BASE_API_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const getUsername = () => userInfo.value.username

const logout = () => {
  commonLogout()
}

const submitting = ref(false)
const feedbackForm = reactive({
  type: '功能建议',
  content: '',
  contact: ''
})

const rules = {
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}

const submitFeedback = async () => {
  const username = getUsername()
  if (!feedbackForm.content.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }
  
  submitting.value = true
  try {
    const res = await fetch(`${BASE_API_URL}/api/feedback/submit`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...feedbackForm,
        username: username
      })
    })
    const data = await res.json()
    if (data.code === 200 || data.code === 0) {
      ElMessage.success('反馈提交成功，感谢您的支持！')
      feedbackForm.content = ''
      feedbackForm.contact = ''
    } else {
      ElMessage.error(data.msg || '提交失败')
    }
  } catch (error) {
    console.error('Submit feedback error:', error)
    ElMessage.error('服务器连接失败，请稍后再试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.feedback-layout {
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

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.title-icon {
  font-size: 22px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name-text {
  font-size: 14px;
  color: #606266;
}

.scroll-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.feedback-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.card-header {
  font-weight: 600;
  color: #303133;
}

.submit-section {
  margin-top: 30px;
  text-align: center;
}

.hint {
  margin-top: 12px;
  font-size: 13px;
  color: #909399;
}

.feedback-info {
  margin-top: 40px;
  background-color: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.feedback-info h3 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 18px;
}

:deep(.el-collapse) {
  border: none;
}

:deep(.el-collapse-item__header) {
  font-size: 15px;
  font-weight: 500;
}
</style>
