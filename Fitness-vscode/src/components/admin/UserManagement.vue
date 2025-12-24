<template>
  <div class="user-management">
    <el-card class="user-table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">系统用户列表</span>
            <el-tag size="small" type="info" class="user-count">共 {{ userList.length }} 位用户</el-tag>
          </div>
          <el-button type="primary" :icon="Refresh" @click="fetchUsers">刷新数据</el-button>
        </div>
      </template>
      
      <el-table :data="userList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150">
          <template #default="scope">
            <div class="user-cell">
              <el-avatar :size="24" class="mini-avatar" :src="getAvatarUrl(scope.row.avatarUrl)">
                {{ (scope.row.realName || scope.row.username || '?').charAt(0).toUpperCase() }}
              </el-avatar>
              <span>{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120">
          <template #default="scope">
            {{ scope.row.realName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="220" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'success'" effect="light">
              {{ scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="200">
          <template #default="scope">
            <el-button 
              size="small" 
              :type="scope.row.role === 'ADMIN' ? 'info' : 'warning'"
              plain
              @click="handleRoleChange(scope.row)"
            >
              {{ scope.row.role === 'ADMIN' ? '设为普通用户' : '设为管理员' }}
            </el-button>
            <el-popconfirm 
              title="确定要删除该用户吗？此操作不可逆！" 
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(scope.row.id)"
            >
              <template #reference>
                <el-button size="small" type="danger" plain>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="userList.length"
          :page-size="10"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminGetUsers, adminUpdateUserRole, adminDeleteUser } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getFullUrl } from '@/utils/common'

const userList = ref([])
const loading = ref(false)

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await adminGetUsers()
    if (res.code === 200) {
      userList.value = res.data
    }
  } catch (error) {
    console.error('Fetch users error:', error)
  } finally {
    loading.value = false
  }
}

const handleRoleChange = async (user) => {
  const newRole = user.role === 'ADMIN' ? 'USER' : 'ADMIN'
  try {
    const res = await adminUpdateUserRole({ id: user.id, role: newRole })
    if (res.code === 200) {
      ElMessage.success('更新成功')
      fetchUsers()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    const res = await adminDeleteUser(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchUsers()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 处理头像路径
const getAvatarUrl = (url) => getFullUrl(url)

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}
.user-table-card {
  border-radius: 12px;
  border: none;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-title {
  font-weight: 600;
  font-size: 16px;
}
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
