<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 左侧装饰区 -->
      <div class="card-left">
        <div class="brand">
          <div class="brand-icon">🏋️</div>
          <h1>健身管理系统</h1>
          <p>记录每一次进步，遇见更好的自己</p>
        </div>
        <div class="features">
          <div class="feature-item">
            <span class="icon">📊</span>
            <span>智能数据分析</span>
          </div>
          <div class="feature-item">
            <span class="icon">📅</span>
            <span>个性化训练计划</span>
          </div>
          <div class="feature-item">
            <span class="icon">💪</span>
            <span>身材数据追踪</span>
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="card-right">
        <h2>欢迎回来</h2>
        <p class="subtitle">请登录您的账号</p>

        <el-form 
          ref="loginFormRef" 
          :model="loginForm" 
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>还没有账号？</span>
            <el-button type="primary" link @click="$router.push('/register')">
              立即注册
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { BASE_API_URL } from '@/utils/common'
import { updateUserInfo } from '@/utils/storage'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度在 3 到 20 个字符', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      // 调用真实后端接口
      const res = await fetch(`${BASE_API_URL}/api/user/login?username=${loginForm.username}&password=${loginForm.password}`, {
        method: 'POST'
      })
      const data = await res.json()
      
      if (data.code === 200 || data.code === 0) {
        // 登录成功，保存用户信息
        const userInfo = {
          id: data.data?.id || Date.now(),
          username: loginForm.username,
          email: data.data?.email,
          role: data.data?.role || 'USER', // 新增：保存角色信息
          avatarUrl: data.data?.avatarUrl, // 新增：保存头像地址
          realName: data.data?.realName // 新增：保存真实姓名
        }
        updateUserInfo(userInfo)
        ElMessage.success('登录成功！')
        // 根据角色跳转到不同页面
        if (userInfo.role === 'ADMIN') {
          router.push('/admin')
        } else {
          router.push('/home')
        }
      } else {
        ElMessage.error(data.msg || '登录失败，请检查用户名和密码')
      }
    } catch (err) {
      console.error('登录失败：', err)
      ElMessage.error('网络错误，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  right: -50px;
  animation: float 6s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 10%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

/* 登录卡片 */
.login-card {
  display: flex;
  width: 900px;
  min-height: 500px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  z-index: 10;
}

/* 左侧装饰区 */
.card-left {
  width: 45%;
  padding: 60px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand {
  text-align: center;
  margin-bottom: 40px;
}

.brand-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.brand h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 10px;
}

.brand p {
  font-size: 14px;
  opacity: 0.9;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(5px);
}

.feature-item .icon {
  font-size: 24px;
}

/* 右侧表单区 */
.card-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.card-right h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  margin-bottom: 40px;
}

.login-form {
  width: 100%;
}

.login-form :deep(.el-input__wrapper) {
  padding: 8px 15px;
  border-radius: 10px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 25px;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.form-footer {
  text-align: center;
  color: #909399;
  margin-top: 20px;
}

/* 响应式 */
@media (max-width: 900px) {
  .login-card {
    width: 90%;
    flex-direction: column;
  }
  
  .card-left {
    width: 100%;
    padding: 40px 30px;
  }
  
  .card-right {
    padding: 40px 30px;
  }
}
</style>
