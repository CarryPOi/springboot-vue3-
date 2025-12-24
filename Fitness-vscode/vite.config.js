import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 配置@别名，方便路径引用
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8081, // 前端端口
    open: true, // 启动后自动打开浏览器
    // 跨域代理（解决前端请求后端跨域）
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端接口地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
