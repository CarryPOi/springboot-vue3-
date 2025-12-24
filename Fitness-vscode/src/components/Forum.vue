<template>
  <div class="page-layout">
    <!-- 侧边栏 -->
    <Sidebar
      v-model:isCollapsed="isCollapsed"
      @menu-click="handleMenuClick"
    />

    <!-- 主内容 -->
    <div class="main-content">
      <!-- 顶部栏 -->
      <div class="top-bar">
        <div class="page-title">
          <span class="title-icon">💬</span>
          <span>论坛广场</span>
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

      <!-- 内容区 -->
      <div class="content-area">
        <div class="forum-stream">
          <!-- 发布区 (置顶) -->
          <el-card class="editor-card stream-item">
            <div class="editor-header">
              <el-avatar :size="40" :src="displayAvatarUrl" class="user-avatar-small">
                {{ displayName.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="editor-title">发布动态</span>
            </div>
            <el-form class="editor-form">
              <el-form-item>
                <el-input
                  v-model="content"
                  type="textarea"
                  :rows="3"
                  placeholder="分享你的健身心得、经验或疑问..."
                  maxlength="500"
                  show-word-limit
                  resize="none"
                />
              </el-form-item>
              
              <!-- 图片上传 -->
              <div class="upload-section">
                <el-upload
                  v-model:file-list="fileList"
                  :action="UPLOAD_IMAGE_URL"
                  list-type="picture-card"
                  :on-preview="handlePictureCardPreview"
                  :on-success="handleUploadSuccess"
                  :limit="3"
                  :on-exceed="handleExceed"
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
                <el-dialog v-model="dialogVisible">
                  <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%" />
                </el-dialog>
              </div>

              <div class="editor-footer">
                <el-button
                  type="primary"
                  class="submit-btn"
                  :loading="submitting"
                  @click="submitComment"
                >
                  <el-icon><Position /></el-icon> 发布动态
                </el-button>
              </div>
            </el-form>
          </el-card>

          <!-- 统计摘要 -->
          <div class="stream-stats">
            <span class="stat-tag">全部动态 ({{ comments.length }})</span>
            <span class="stat-tag">社区用户 ({{ uniqueUsers }})</span>
          </div>

          <!-- 动态列表 -->
          <div ref="commentTopRef"></div>
          
          <el-empty
            v-if="pagedComments.length === 0"
            description="暂无动态，快来发布第一条吧！"
            :image-size="120"
          />

          <transition-group name="comment-fade" tag="div">
            <div
              v-for="item in pagedComments"
              :key="item.id"
              class="comment-card stream-item"
            >
              <div class="card-main">
                <div class="user-avatar-container">
                  <el-avatar 
                    :size="48" 
                    class="avatar"
                    :src="getFullUrl(item.avatarUrl)"
                  >
                    {{ getItemDisplayName(item).charAt(0).toUpperCase() }}
                  </el-avatar>
                </div>
                
                <div class="card-content">
                  <div class="card-header">
                    <div class="user-meta">
                      <span class="username">{{ getItemDisplayName(item) }}</span>
                      <span class="time">{{ formatTime(item.createTime) }}</span>
                    </div>
                    <el-dropdown trigger="click" v-if="item.userId === currentUserId || userInfo.role === 'ADMIN'">
                      <span class="more-actions"><el-icon><MoreFilled /></el-icon></span>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item @click="handleDelete(item.id)" style="color: #f56c6c">
                            <el-icon><Delete /></el-icon> 删除
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>

                  <div class="comment-text">
                    {{ item.content }}
                  </div>

                  <!-- 动态图片展示 -->
                  <div v-if="item.images" class="comment-images">
                    <el-image 
                      v-for="(img, index) in item.images.split(',')" 
                      :key="index"
                      :src="getFullUrl(img)" 
                      :preview-src-list="item.images.split(',').map(url => getFullUrl(url))"
                      fit="cover"
                      class="post-image"
                    />
                  </div>

                  <!-- 交互栏 -->
                  <div class="card-actions">
                    <div class="action-item" :class="{ active: isLiked(item.id) }" @click="handleLike(item)">
                      <el-icon><Star /></el-icon>
                      <span>{{ item.likeCount || 0 }}</span>
                    </div>
                    <div class="action-item" @click="toggleReply(item.id)">
                      <el-icon><ChatDotRound /></el-icon>
                      <span>{{ item.children?.length || 0 }}</span>
                    </div>
                  </div>

                  <!-- 二级评论区 -->
                  <div class="reply-section" v-if="showReplyId === item.id || (item.children && item.children.length > 0)">
                    <!-- 回复输入框 -->
                    <div class="reply-input-wrapper" v-if="showReplyId === item.id">
                      <el-input
                        v-model="replyContent"
                        type="textarea"
                        :rows="1"
                        autosize
                        :placeholder="'回复 @' + (replyTargetName || getItemDisplayName(item)) + '...'"
                        class="reply-input"
                      />
                      <div class="reply-btns">
                        <el-button size="small" @click="cancelReply">取消</el-button>
                        <el-button type="primary" size="small" :loading="replySubmitting" @click="submitReply(item)">发送</el-button>
                      </div>
                    </div>

                    <!-- 子评论列表 -->
                    <div class="sub-comments" v-if="item.children && item.children.length > 0">
                      <div v-for="child in item.children" :key="child.id" class="sub-comment-item">
                        <el-avatar :size="24" :src="getFullUrl(child.avatarUrl)" class="sub-avatar">
                          {{ getItemDisplayName(child).charAt(0).toUpperCase() }}
                        </el-avatar>
                        <div class="sub-content">
                          <div class="sub-header">
                            <span class="sub-username">{{ getItemDisplayName(child) }}</span>
                            <span v-if="child.replyUserId" class="reply-text">
                              回复 <span class="reply-target">@{{ child.replyRealName || child.replyUsername }}</span>
                            </span>
                            <span class="sub-time">{{ formatTime(child.createTime) }}</span>
                          </div>
                          <div class="sub-text">{{ child.content }}</div>
                          <div class="sub-actions">
                            <span class="sub-action-btn" @click="startReply(item.id, child)">回复</span>
                            <span v-if="child.userId === currentUserId || userInfo.role === 'ADMIN'" 
                                  class="sub-action-btn delete" @click="handleDelete(child.id)">删除</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </transition-group>

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="comments.length > pageSize">
            <el-pagination
              background
              layout="prev, pager, next"
              :page-size="pageSize"
              :total="comments.length"
              v-model:current-page="currentPage"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from '@/components/Sidebar.vue'
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, SwitchButton, Position, Delete, Plus, MoreFilled, Star, ChatDotRound } from '@element-plus/icons-vue'
import { getCommentList, addComment, deleteComment, likeComment } from '@/api/comment'
import { useUserInfo, updateUserInfo } from '@/utils/storage'
import { formatTime, getFullUrl, UPLOAD_IMAGE_URL } from '@/utils/common'

const router = useRouter()
const isCollapsed = ref(false)
const submitting = ref(false)

// 用户信息
const userInfo = useUserInfo()
const currentUserId = computed(() => userInfo.value.id)
const getUserId = () => userInfo.value.id
const getUsername = () => userInfo.value.username

const displayName = computed(() => {
  return userInfo.value.realName || userInfo.value.username || '用户'
})

const displayAvatarUrl = computed(() => getFullUrl(userInfo.value.avatarUrl))

// 评论显示的名称
const getItemDisplayName = (item) => {
  return item.realName || item.username || '匿名用户'
}

// 动态数据
const comments = ref([])
const content = ref('')

// 图片上传相关
const fileList = ref([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    // 自动收集已上传的图片URL
  }
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传 3 张图片')
}

// 回复功能
const showReplyId = ref(null)
const replyContent = ref('')
const replyTargetUser = ref(null)
const replyTargetName = ref('')
const replySubmitting = ref(false)

const toggleReply = (id) => {
  if (showReplyId.value === id) {
    showReplyId.value = null
  } else {
    showReplyId.value = id
    replyTargetUser.value = null
    replyTargetName.value = ''
    replyContent.value = ''
  }
}

const startReply = (parentId, targetComment) => {
  showReplyId.value = parentId
  replyTargetUser.value = targetComment.userId
  replyTargetName.value = getItemDisplayName(targetComment)
  replyContent.value = ''
}

const cancelReply = () => {
  showReplyId.value = null
  replyTargetUser.value = null
  replyTargetName.value = ''
  replyContent.value = ''
}

const submitReply = async (parentComment) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }

  replySubmitting.value = true
  try {
    await addComment({
      content: replyContent.value,
      userId: getUserId(),
      username: getUsername(),
      parentId: parentComment.id,
      replyUserId: replyTargetUser.value
    })

    ElMessage.success('回复成功')
    cancelReply()
    await loadComments()
  } catch (err) {
    ElMessage.error('回复失败')
  } finally {
    replySubmitting.value = false
  }
}

// 点赞功能
const likedPosts = ref(new Set())
const isLiked = (id) => likedPosts.value.has(id)

const handleLike = async (item) => {
  if (isLiked(item.id)) return
  
  try {
    const res = await likeComment(item.id)
    if (res.code === 200 || res.code === 0) {
      item.likeCount = res.data
      likedPosts.value.add(item.id)
    }
  } catch (err) {
    console.error('点赞失败', err)
  }
}

// 分页
const currentPage = ref(1)
const pageSize = 10

// 锚点
const commentTopRef = ref(null)

// 统计独立用户数
const uniqueUsers = computed(() => {
  const users = new Set()
  comments.value.forEach(c => {
    users.add(c.userId)
    if (c.children) {
      c.children.forEach(child => users.add(child.userId))
    }
  })
  return users.size
})

// 加载动态
const loadComments = async () => {
  try {
    const res = await getCommentList()
    comments.value = res.data || []
  } catch (err) {
    console.error('加载动态失败：', err)
  }
}

// 当前页数据
const pagedComments = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return comments.value.slice(start, start + pageSize)
})

// 发布动态
const submitComment = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('动态内容不能为空')
    return
  }

  submitting.value = true
  try {
    // 收集图片地址
    const imageUrls = fileList.value
      .map(f => f.response?.data || f.url)
      .filter(url => url && !url.startsWith('blob:'))
      .join(',')

    await addComment({
      content: content.value,
      userId: getUserId(),
      username: getUsername(),
      images: imageUrls
    })

    ElMessage.success('动态发布成功！')
    content.value = ''
    fileList.value = []
    currentPage.value = 1

    await loadComments()
    await nextTick()
    commentTopRef.value?.scrollIntoView({ behavior: 'smooth' })
  } catch (err) {
    ElMessage.error('发布失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 删除动态
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除这条内容吗？', '提示', {
      type: 'warning'
    })
    await deleteComment(id, getUserId())
    ElMessage.success('删除成功')
    loadComments()
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleMenuClick = (item) => {
  console.log(item.title)
}

const logout = () => {
  updateUserInfo(null)
  ElMessage.success('退出成功')
  router.push('/login')
}

onMounted(loadComments)
</script>

<style scoped>
.page-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: #f0f2f5;
}

.main-content {
  flex: 1;
  overflow-y: auto;
}

/* 顶部栏 */
.top-bar {
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  color: #1a1a1a;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-avatar {
  background: linear-gradient(135deg, #409eff 0%, #1e80ff 100%);
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
}

/* 内容流布局 */
.content-area {
  padding: 30px 20px;
}

.forum-stream {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stream-item {
  border-radius: 12px;
  border: none;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 发布器 */
.editor-card {
  padding: 4px;
}

.editor-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.editor-title {
  font-weight: 600;
  color: #333;
}

.editor-form :deep(.el-textarea__inner) {
  border-color: #f0f0f0;
  background: #f9f9f9;
  border-radius: 8px;
  padding: 12px;
  font-size: 15px;
}

.editor-form :deep(.el-textarea__inner:focus) {
  background: #fff;
  border-color: #409eff;
}

.upload-section {
  margin: 16px 0;
}

.upload-section :deep(.el-upload--picture-card) {
  width: 80px;
  height: 80px;
  line-height: 90px;
  border-radius: 8px;
}

.upload-section :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.editor-footer {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.submit-btn {
  padding: 0 24px;
  height: 38px;
  border-radius: 19px;
  font-weight: 600;
}

/* 统计标签 */
.stream-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 5px;
}

.stat-tag {
  font-size: 13px;
  color: #8590a6;
  background: #fff;
  padding: 4px 12px;
  border-radius: 15px;
}

/* 动态卡片 */
.comment-card {
  background: #fff;
  padding: 20px;
  transition: transform 0.2s;
}

.card-main {
  display: flex;
  gap: 16px;
}

.user-avatar-container {
  flex-shrink: 0;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.user-meta {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 15px;
}

.time {
  font-size: 12px;
  color: #8590a6;
  margin-top: 2px;
}

.more-actions {
  color: #8590a6;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.more-actions:hover {
  background: #f0f2f5;
}

.comment-text {
  font-size: 15px;
  line-height: 1.6;
  color: #1a1a1a;
  white-space: pre-wrap;
  margin-bottom: 12px;
}

.comment-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.post-image {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  cursor: zoom-in;
}

/* 交互栏 */
.card-actions {
  display: flex;
  gap: 24px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f6f6f6;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #8590a6;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  color: #409eff;
}

.action-item.active {
  color: #409eff;
  font-weight: 600;
}

.action-item .el-icon {
  font-size: 18px;
}

/* 回复区 */
.reply-section {
  margin-top: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  padding: 12px;
}

.reply-input-wrapper {
  margin-bottom: 12px;
}

.reply-input :deep(.el-textarea__inner) {
  background: #fff;
  border-radius: 4px;
}

.reply-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

.sub-comments {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sub-comment-item {
  display: flex;
  gap: 10px;
}

.sub-avatar {
  flex-shrink: 0;
  margin-top: 2px;
}

.sub-content {
  flex: 1;
  font-size: 14px;
}

.sub-header {
  margin-bottom: 4px;
}

.sub-username {
  font-weight: 600;
  color: #444;
}

.reply-text {
  margin: 0 4px;
  color: #8590a6;
  font-size: 13px;
}

.reply-target {
  color: #409eff;
  font-weight: 500;
}

.sub-time {
  font-size: 12px;
  color: #bfbfbf;
  margin-left: 8px;
}

.sub-text {
  color: #1a1a1a;
  line-height: 1.5;
}

.sub-actions {
  margin-top: 4px;
  display: flex;
  gap: 12px;
}

.sub-action-btn {
  font-size: 12px;
  color: #8590a6;
  cursor: pointer;
}

.sub-action-btn:hover {
  color: #409eff;
}

.sub-action-btn.delete:hover {
  color: #f56c6c;
}

.pagination-wrapper {
  padding: 20px 0 50px;
  display: flex;
  justify-content: center;
}

/* 动画 */
.comment-fade-enter-active,
.comment-fade-leave-active {
  transition: all 0.4s ease;
}

.comment-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.comment-fade-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* 响应式 */
@media (max-width: 600px) {
  .forum-stream {
    width: 100%;
  }
  .post-image {
    width: 100px;
    height: 100px;
  }
}
</style>
