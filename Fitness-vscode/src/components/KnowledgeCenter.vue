<template>
  <div class="knowledge-center-layout">
    <Sidebar v-model:isCollapsed="isCollapsed" />
    
    <div class="main-content">
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">📖</span>
          <span>健康百科</span>
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
          <el-tabs v-model="activeTab" class="knowledge-tabs">
            <el-tab-pane label="健身讲堂" name="fitness">
              <div class="filter-section">
                <el-radio-group v-model="selectedCategory" size="large" @change="fetchKnowledge">
                  <el-radio-button label="">全部</el-radio-button>
                  <el-radio-button label="健身入门">健身入门</el-radio-button>
                  <el-radio-button label="增肌">增肌</el-radio-button>
                  <el-radio-button label="减脂">减脂</el-radio-button>
                  <el-radio-button label="拉伸">拉伸</el-radio-button>
                </el-radio-group>
              </div>

              <div class="knowledge-grid" v-loading="loading">
                <el-card 
                  v-for="item in knowledgeList" 
                  :key="item.id" 
                  class="knowledge-card" 
                  :body-style="{ padding: '0px', display: 'flex', flexDirection: 'column', height: '100%' }"
                  @click="showDetail(item)"
                >
                  <div class="card-cover">
                    <el-image 
                      :src="item.coverUrl || defaultCover" 
                      fit="cover"
                      class="cover-image"
                    >
                      <template #error>
                        <div class="image-placeholder">健身知识</div>
                      </template>
                    </el-image>
                    <div class="category-badge">{{ item.category }}</div>
                  </div>
                  <div class="card-info">
                    <h3 class="card-title">{{ item.title }}</h3>
                    <p class="card-excerpt">{{ truncate(item.content, 60) }}</p>
                    <div class="card-footer">
                      <span class="publish-time">{{ formatTime(item.createTime) }}</span>
                      <el-button link type="primary">阅读全文</el-button>
                    </div>
                  </div>
                </el-card>
              </div>
              <el-empty v-if="!loading && knowledgeList.length === 0" description="暂无相关知识文章" />
            </el-tab-pane>

            <el-tab-pane label="膳食指南" name="diet">
              <div class="diet-section">
                <div class="diet-grid">
                  <el-card 
                    v-for="diet in dietList" 
                    :key="diet.id" 
                    class="diet-card"
                    :body-style="{ padding: '0px', display: 'flex', flexDirection: 'column', height: '100%' }"
                    @click="showDietDetail(diet)"
                  >
                    <div class="diet-card-header">
                      <div class="diet-type-tag">{{ diet.mealType }}</div>
                      <h3 class="diet-card-title">{{ diet.title }}</h3>
                    </div>
                    <div class="diet-card-body">
                      <p class="diet-card-excerpt">{{ truncate(diet.content, 80) }}</p>
                    </div>
                    <div class="diet-card-footer">
                      <span class="diet-calories" v-if="diet.calories">🔥 {{ diet.calories }} kcal</span>
                      <el-button link type="success">查看方案</el-button>
                    </div>
                  </el-card>
                </div>
                <el-empty v-if="dietList.length === 0" description="暂无饮食推荐" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <!-- 精美的全屏文章阅读框架 -->
    <transition name="fade-slide">
      <div v-if="detailVisible" class="reader-overlay">
        <div class="reader-container">
          <!-- 顶部工具栏 -->
          <div class="reader-toolbar">
            <el-button @click="detailVisible = false" circle :icon="ArrowLeft" class="back-btn" />
            <div class="toolbar-title">{{ currentArticle.title }}</div>
            <div class="toolbar-actions">
              <el-button link @click="detailVisible = false">关闭</el-button>
            </div>
          </div>

          <!-- 文章主体 -->
          <div class="reader-content-scroll" ref="scrollContainer">
            <article class="article-wrapper">
              <!-- 文章头图 -->
              <div v-if="currentArticle.coverUrl" class="article-hero">
                <el-image :src="currentArticle.coverUrl" fit="cover" class="hero-image" />
                <div class="hero-overlay"></div>
              </div>

              <div class="article-main">
                <!-- 文章标题与元数据 -->
                <header class="article-header">
                  <div class="article-category">
                    <el-tag v-if="currentArticle.category" effect="plain">{{ currentArticle.category }}</el-tag>
                    <el-tag v-else-if="currentArticle.mealType" type="success" effect="plain">{{ currentArticle.mealType }}</el-tag>
                  </div>
                  <h1 class="article-title">{{ currentArticle.title }}</h1>
                  <div class="article-meta-info">
                    <span class="meta-item" v-if="currentArticle.createTime">
                      <el-icon><Calendar /></el-icon>
                      {{ formatTime(currentArticle.createTime) }}
                    </span>
                    <span class="meta-item" v-if="currentArticle.calories">
                      <el-icon><IceTea /></el-icon>
                      {{ currentArticle.calories }} kcal
                    </span>
                    <span class="meta-item">
                      <el-icon><User /></el-icon>
                      {{ currentArticle.author || '系统推荐' }}
                    </span>
                  </div>
                </header>

                <!-- 文章正文 -->
                <div class="article-body">
                  {{ currentArticle.content }}
                </div>

                <!-- 文章底部 -->
                <footer class="article-footer">
                  <div class="footer-divider"></div>
                  <div class="footer-content">
                    <p>声明：本文内容仅供参考，如有身体不适请及时咨询专业人士。</p>
                    <el-button type="primary" plain round @click="detailVisible = false">完成阅读</el-button>
                  </div>
                </footer>
              </div>
            </article>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { User, SwitchButton, ArrowLeft, Calendar, View, IceTea } from '@element-plus/icons-vue'
import Sidebar from '@/components/Sidebar.vue'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { logout as commonLogout, getFullUrl, BASE_API_URL, formatTime } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const activeTab = ref('fitness')
const loading = ref(false)
const knowledgeList = ref([])
const dietList = ref([])
const selectedCategory = ref('')
const detailVisible = ref(false)
const currentArticle = ref({})
const defaultCover = 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438'

const userInfo = useUserInfo()

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

const logout = () => {
  commonLogout()
}

const fetchKnowledge = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${BASE_API_URL}/api/knowledge/list`, {
      params: { category: selectedCategory.value }
    })
    if (res.data.code === 200 || res.data.code === 0) {
      knowledgeList.value = res.data.data
    }
  } catch (error) {
    console.error('Fetch knowledge error:', error)
  } finally {
    loading.value = false
  }
}

const fetchDietList = async () => {
  try {
    const res = await axios.get(`${BASE_API_URL}/api/diet-recommendations/list`)
    if (res.data.code === 200 || res.data.code === 0) {
      dietList.value = res.data.data
    }
  } catch (error) {
    console.error('Fetch diet error:', error)
  }
}

const showDetail = (item) => {
  currentArticle.value = item
  detailVisible.value = true
}

const showDietDetail = (diet) => {
  // 统一使用阅读器展示
  currentArticle.value = {
    ...diet,
    category: '', // 清空category以触发膳食指南的tag显示
    coverUrl: '' // 膳食推荐通常没有封面图
  }
  detailVisible.value = true
}

const activeCategory = ref('全部')

const truncate = (text, len) => {
  if (!text) return ''
  return text.length > len ? text.substring(0, len) + '...' : text
}

onMounted(() => {
  fetchKnowledge()
  fetchDietList()
})
</script>

<style scoped>
.knowledge-center-layout {
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
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
}

.scroll-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.knowledge-tabs {
  background: transparent;
}

.filter-section {
  margin-bottom: 24px;
  text-align: center;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.knowledge-card {
  height: 100%;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  border-radius: 12px;
  overflow: hidden;
}

.knowledge-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
}

.card-cover {
  flex-shrink: 0;
  position: relative;
  height: 160px;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.category-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
}

.card-title {
  margin: 0 0 10px;
  font-size: 16px;
  color: #2c3e50;
  line-height: 1.4;
}

.card-excerpt {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.card-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.publish-time {
  font-size: 12px;
  color: #909399;
}

.diet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.diet-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.diet-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
  border-color: #67c23a;
}

.diet-card-header {
  padding: 16px;
  border-bottom: 1px dashed #f0f0f0;
}

.diet-type-tag {
  display: inline-block;
  padding: 2px 8px;
  background: #f0f9eb;
  color: #67c23a;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 8px;
}

.diet-card-title {
  margin: 0;
  font-size: 16px;
  color: #2c3e50;
  line-height: 1.4;
  font-weight: 600;
}

.diet-card-body {
  padding: 16px;
  flex: 1;
}

.diet-card-excerpt {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.diet-card-footer {
  padding: 12px 16px;
  background: #fafafa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f0f0f0;
}

.diet-calories {
  font-size: 12px;
  color: #e6a23c;
  font-weight: 500;
}

/* 文章阅读器样式 */
.reader-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  z-index: 2000;
  display: flex;
  flex-direction: column;
}

.reader-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.reader-toolbar {
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.toolbar-title {
  font-weight: 600;
  font-size: 16px;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 60%;
}

.reader-content-scroll {
  flex: 1;
  overflow-y: auto;
  scroll-behavior: smooth;
  background-color: #f9f9f9;
}

.article-wrapper {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  min-height: 100%;
  box-shadow: 0 0 20px rgba(0,0,0,0.05);
}

.article-hero {
  width: 100%;
  height: 400px;
  position: relative;
  overflow: hidden;
}

.hero-image {
  width: 100%;
  height: 100%;
}

.hero-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 150px;
  background: linear-gradient(to top, #fff, transparent);
}

.article-main {
  padding: 40px 60px;
}

.article-header {
  margin-bottom: 40px;
  text-align: center;
}

.article-category {
  margin-bottom: 16px;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 20px;
  line-height: 1.3;
}

.article-meta-info {
  display: flex;
  justify-content: center;
  gap: 24px;
  color: #8c8c8c;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.article-body {
  font-size: 18px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
}

.article-footer {
  margin-top: 60px;
  padding-bottom: 40px;
}

.footer-divider {
  height: 1px;
  background: #f0f0f0;
  margin-bottom: 30px;
}

.footer-content {
  text-align: center;
  color: #8c8c8c;
  font-size: 14px;
}

.footer-content p {
  margin-bottom: 20px;
}

/* 动画效果 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
}
</style>
