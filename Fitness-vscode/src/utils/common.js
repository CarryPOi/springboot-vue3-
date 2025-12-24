/**
 * 公共工具函数
 */
import { ElMessage } from 'element-plus'
import router from '@/router'

/**
 * 获取当前登录用户信息
 * @returns {Object|null} 用户信息对象
 */
export function getUserInfo() {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      return JSON.parse(userInfoStr)
    }
  } catch (e) {
    console.warn('解析用户信息失败：', e)
  }
  return null
}

/**
 * 获取当前用户名
 * @returns {string} 用户名
 */
export function getUsername() {
  const userInfo = getUserInfo()
  return userInfo?.username || ''
}

/**
 * 退出登录
 */
export function logout() {
  localStorage.removeItem('userInfo')
  ElMessage.success('退出成功')
  router.push('/login')
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export function isLoggedIn() {
  return !!getUserInfo()
}

/**
 * 格式化时间（智能转换：刚刚、x分钟前、x小时前、日期）
 * @param {string|Array|Date} time 时间
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(time) {
  if (!time) return ''
  let date
  try {
    if (Array.isArray(time)) {
      // 处理后端返回的 LocalDateTime 数组格式 [YYYY, MM, DD, HH, mm, ss]
      date = new Date(time[0], time[1] - 1, time[2], time[3] || 0, time[4] || 0, time[5] || 0)
    } else if (typeof time === 'string') {
      // 处理 ISO 格式或其他字符串格式
      // 兼容处理：将 T 替换为空格，将 - 替换为 / 以兼容不同浏览器
      let t = time.replace('T', ' ').replace(/-/g, '/')
      date = new Date(t)
      // 如果解析失败，尝试直接使用原字符串
      if (isNaN(date.getTime())) {
        date = new Date(time)
      }
    } else {
      date = new Date(time)
    }
    
    if (isNaN(date.getTime())) return '时间格式错误'
  } catch (e) {
    return '时间解析失败'
  }

  const now = new Date()
  const diff = now - date
  
  // 处理未来时间（防止服务器时间偏差）
  if (diff < 0) return '刚刚'
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)} 天前`
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

/**
 * API 基础地址
 */
export const BASE_API_URL = 'http://localhost:8080'

/**
 * 图片上传地址
 */
export const UPLOAD_IMAGE_URL = `${BASE_API_URL}/api/upload/image`

/**
 * 获取完整的文件访问 URL
 * @param {string} url 相对路径或绝对路径
 * @returns {string} 完整 URL
 */
export function getFullUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${BASE_API_URL}${url.startsWith('/') ? '' : '/'}${url}`
}

/**
 * 格式化日期
 * @param {string|Date} date 日期
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * 格式化时长（分钟转换为小时分钟）
 * @param {number} minutes 分钟数
 * @returns {string} 格式化后的时长字符串
 */
export function formatDuration(minutes) {
  if (!minutes || minutes === 0) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0 && mins > 0) {
    return `${hours}小时${mins}分钟`
  } else if (hours > 0) {
    return `${hours}小时`
  } else {
    return `${mins}分钟`
  }
}

/**
 * API 基础地址
 */
export const API_BASE_URL = 'http://localhost:8080'

/**
 * 封装 fetch 请求
 * @param {string} url 请求地址
 * @param {Object} options 请求配置
 * @returns {Promise<Object>} 响应数据
 */
export async function request(url, options = {}) {
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    }
  }
  
  const finalOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers
    }
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, finalOptions)
    const data = await response.json()
    return data
  } catch (error) {
    console.error('请求失败：', error)
    throw error
  }
}








