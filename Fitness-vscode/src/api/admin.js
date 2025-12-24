import request from '@/utils/request'

/**
 * 获取所有用户列表
 */
export function adminGetUsers() {
  return request({
    url: '/api/admin/users',
    method: 'get'
  })
}

/**
 * 更新用户角色
 * @param {Object} data { id: Long, role: String }
 */
export function adminUpdateUserRole(data) {
  return request({
    url: '/api/admin/user/updateRole',
    method: 'post',
    data
  })
}

/**
 * 删除用户
 * @param {Number} id 用户ID
 */
export function adminDeleteUser(id) {
  return request({
    url: `/api/admin/user/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有公告列表
 */
export function adminGetNotices() {
  return request({
    url: '/api/admin/notices',
    method: 'get'
  })
}

/**
 * 保存/更新公告
 */
export function adminSaveNotice(data) {
  return request({
    url: '/api/admin/notice/save',
    method: 'post',
    data
  })
}

/**
 * 删除公告
 */
export function adminDeleteNotice(id) {
  return request({
    url: `/api/admin/notice/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有课程列表
 */
export function adminGetCourses() {
  return request({
    url: '/api/admin/courses',
    method: 'get'
  })
}

/**
 * 保存/更新课程
 */
export function adminSaveCourse(data) {
  return request({
    url: '/api/admin/course/save',
    method: 'post',
    data
  })
}

/**
 * 删除课程
 */
export function adminDeleteCourse(id) {
  return request({
    url: `/api/admin/course/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获取课程订阅者列表
 */
export function adminGetCourseSubscribers(id) {
  return request({
    url: `/api/admin/course/subscribers/${id}`,
    method: 'get'
  })
}
