<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 注册卡片 -->
    <div class="register-card">
      <!-- 左侧表单区 -->
      <div class="card-left">
        <h2>创建账号</h2>
        <p class="subtitle">开启您的健身之旅</p>

        <el-form 
          ref="registerFormRef" 
          :model="registerForm" 
          :rules="registerRules"
          class="register-form"
          @keyup.enter="handleRegister"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              placeholder="请确认密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input 
              v-model="registerForm.email" 
              placeholder="请输入邮箱（选填）"
              prefix-icon="Message"
              size="large"
            />
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading"
              class="register-btn"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>已有账号？</span>
            <el-button type="primary" link @click="$router.push('/login')">
              返回登录
            </el-button>
          </div>
        </el-form>
      </div>

      <!-- 右侧装饰区 -->
      <div class="card-right">
        <div class="brand">
          <div class="brand-icon">💪</div>
          <h1>健身管理系统</h1>
          <p>科学健身，健康生活</p>
        </div>
        <div class="benefits">
          <div class="benefit-item">
            <div class="benefit-icon">✅</div>
            <div class="benefit-text">
              <h4>免费使用</h4>
              <p>所有功能完全免费</p>
            </div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">🔒</div>
            <div class="benefit-text">
              <h4>数据安全</h4>
              <p>您的数据将被安全存储</p>
            </div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">🎯</div>
            <div class="benefit-text">
              <h4>个性定制</h4>
              <p>量身定制的训练计划</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { BASE_API_URL } from '@/utils/common'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 确认密码校验
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      // 调用真实后端接口
      const res = await fetch(`${BASE_API_URL}/api/user/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
          username: registerForm.username,
          password: registerForm.password,
          email: registerForm.email || null
        })
      })
      const data = await res.json()
      
      if (data.code === 200 || data.code === 0) {
        ElMessage.success('注册成功，请登录！')
        router.push('/login')
      } else {
        ElMessage.error(data.msg || '注册失败，请稍后重试')
      }
    } catch (err) {
      console.error('注册失败：', err)
      ElMessage.error('网络错误，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
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
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
  animation: float 6s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 40%;
  right: 10%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

/* 注册卡片 */
.register-card {
  display: flex;
  width: 900px;
  min-height: 550px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  z-index: 10;
}

/* 左侧表单区 */
.card-left {
  flex: 1;
  padding: 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.card-left h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  margin-bottom: 30px;
}

.register-form {
  width: 100%;
}

.register-form :deep(.el-input__wrapper) {
  padding: 8px 15px;
  border-radius: 10px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.register-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border: none;
  transition: all 0.3s;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(17, 153, 142, 0.3);
}

.form-footer {
  text-align: center;
  color: #909399;
  margin-top: 15px;
}

/* 右侧装饰区 */
.card-right {
  width: 45%;
  padding: 50px 40px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
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
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 10px;
}

.brand p {
  font-size: 14px;
  opacity: 0.9;
}

.benefits {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
}

.benefit-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(-5px);
}

.benefit-icon {
  font-size: 28px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
}

.benefit-text h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.benefit-text p {
  font-size: 13px;
  opacity: 0.85;
}

/* 响应式 */
@media (max-width: 900px) {
  .register-card {
    width: 90%;
    flex-direction: column-reverse;
  }
  
  .card-left {
    padding: 40px 30px;
  }
  
  .card-right {
    width: 100%;
    padding: 30px;
  }
}
</style>
