import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/components/Login.vue'
import Register from '@/components/Register.vue'
import Home from '@/components/Home.vue'
import TrainingPlan from '@/components/TrainingPlan.vue'
import UserInfo from '@/components/UserInfo.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/home', name: 'Home', component: Home, meta: { requiresAuth: true } },
  { path: '/training-plan', name: 'TrainingPlan', component: TrainingPlan, meta: { requiresAuth: true } },
  { path: '/user', name: 'UserInfo', component: UserInfo, meta: { requiresAuth: true } },
  { path: '/notice', name: 'Notice', component: () => import('@/components/Notice.vue'), meta: { requiresAuth: true } },
  { path: '/diet', name: 'DailyDiet', component: () => import('@/components/DailyDiet.vue'), meta: { requiresAuth: true } },
  { path: '/courses', name: 'CourseSubscription', component: () => import('@/components/CourseSubscription.vue'), meta: { requiresAuth: true } },
  // router/index.js
  { path: '/body-data', name: 'BodyDataAnalysis', component: () => import('@/components/BodyDataAnalysis.vue'), meta: { requiresAuth: true } },
  { path: '/forum', name: 'Forum', component: () => import('@/components/Forum.vue'), meta: { requiresAuth: true } },
  { path: '/workout-log', name: 'WorkoutLog', component: () => import('@/components/WorkoutLog.vue'), meta: { requiresAuth: true } },
  { path: '/feedback', name: 'UserFeedback', component: () => import('@/components/UserFeedback.vue'), meta: { requiresAuth: true } },
  { path: '/knowledge', name: 'KnowledgeCenter', component: () => import('@/components/KnowledgeCenter.vue'), meta: { requiresAuth: true } },
  { 
    path: '/admin', 
    name: 'Admin', 
    component: () => import('@/components/Admin.vue'), 
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('@/components/admin/Dashboard.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/components/admin/UserManagement.vue') },
      { path: 'courses', name: 'AdminCourses', component: () => import('@/components/admin/CourseManagement.vue') },
      { path: 'notices', name: 'AdminNotices', component: () => import('@/components/admin/NoticeManagement.vue') },
      { path: 'comments', name: 'AdminComments', component: () => import('@/components/admin/CommentManagement.vue') },
      { path: 'diet', name: 'AdminDiet', component: () => import('@/components/admin/DietRecommendation.vue') },
      { path: 'feedback', name: 'AdminFeedback', component: () => import('@/components/admin/Feedback.vue') },
      { path: 'knowledge', name: 'AdminKnowledge', component: () => import('@/components/admin/KnowledgeManagement.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 3. 全局挂载router（供Sidebar组件直接访问）
window.$router = router

// 4. 导入ElMessage（路由守卫中提示登录）
import { ElMessage } from 'element-plus'

// 路由守卫（优化：添加登录提示和权限校验）
router.beforeEach((to, from, next) => {
  const userInfoStr = localStorage.getItem('userInfo')
  const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null

  if (to.meta.requiresAuth && !userInfo) {
    ElMessage.warning('请先登录后再访问！')
    next('/login')
  } else if (to.meta.requiresAdmin && userInfo?.role !== 'ADMIN') {
    ElMessage.error('权限不足，无法访问管理页面！')
    next('/home')
  } else {
    next()
  }
})

export default router