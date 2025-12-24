import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

service.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    ElMessage.error('请求发送失败')
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data

    // ✅ 与后端 Result.code 对齐 (兼容 0 和 200)
    if (res.code !== 0 && res.code !== 200) {
      ElMessage.error(res.msg || '操作失败')
      return Promise.reject(res)
    }

    return res
  },
  (error) => {
    ElMessage.error('网络异常，请检查后端是否启动')
    return Promise.reject(error)
  }
)


export default service
