import { ref, watchEffect } from 'vue'

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

// 监听本地存储变化 (其他标签页)
window.addEventListener('storage', (e) => {
  if (e.key === 'userInfo') {
    try {
      userInfo.value = JSON.parse(e.newValue || '{}')
    } catch (err) {
      userInfo.value = {}
    }
  }
})

// 提供一个手动更新的方法 (同标签页)
export function updateUserInfo(newInfo) {
  if (newInfo) {
    localStorage.setItem('userInfo', JSON.stringify(newInfo))
    userInfo.value = newInfo
  } else {
    localStorage.removeItem('userInfo')
    userInfo.value = {}
  }
  // 触发 storage 事件供其他标签页同步
  window.dispatchEvent(new Event('storage'))
}

export function useUserInfo() {
  return userInfo
}
