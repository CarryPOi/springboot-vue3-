import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// Element Plus 中文语言包
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
// 导入需要的图标
import { House, User, Calendar, ChatDotRound, Fold, Expand, Setting, Star, Avatar } from '@element-plus/icons-vue'

// 🔥 新增1：导入 Sidebar 组件（确保文件路径正确）
import Sidebar from '@/components/Sidebar.vue'

const app = createApp(App)
app.use(router)
// 使用 Element Plus，并设置为中文
app.use(ElementPlus, { locale: zhCn })

// 全局注册图标
app.component('House', House)
app.component('User', User)
app.component('Calendar', Calendar)
app.component('ChatDotRound', ChatDotRound)
app.component('Fold', Fold)
app.component('Expand', Expand)
app.component('Setting', Setting)
app.component('Star', Star)
app.component('Avatar', Avatar)

// 🔥 新增2：全局注册 Sidebar 组件（命名为 Sidebar，可直接使用）
app.component('Sidebar', Sidebar)

app.mount('#app')